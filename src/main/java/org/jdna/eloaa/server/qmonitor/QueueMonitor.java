package org.jdna.eloaa.server.qmonitor;

/*
 * #%L
 * GwtMaterial
 * %%
 * Copyright (C) 2015 - 2016 GwtMaterialDesign
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.j256.ormlite.stmt.QueryBuilder;
import org.jdna.eloaa.shared.model.GProgress;
import org.jdna.eloaa.server.App;
import org.jdna.eloaa.shared.model.MovieEntry;
import org.jdna.sabnzbd.api.model.Queue;
import org.jdna.sabnzbd.api.model.Slot;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by seans on 08/08/16.
 */
public class QueueMonitor {
    public void unmonitor(String id) {
        movies.remove(id);
    }

    private class MonitorTask extends TimerTask {
        @Override
        public void run() {
            List<MovieEntry> process = new ArrayList<>(movies.values());
            for (MovieEntry p: process) {
                updateProgress(p);
            }
            process.clear();
        }
    }

    private Map<String, MovieEntry> movies = new HashMap<String, MovieEntry>();
    Timer timer = null;

    public QueueMonitor() {
    }

    private void updateProgress(MovieEntry p) {
        if (p.isComplete() || p.isMoving()) {
            movies.remove(p.getId());
            return;
        }

        // once it is downloaded, we don't bother with updating the
        // movie entry with progress.
        if (p.isDownloaded()) {
            renameAndMove(p);
            return;
        }

        Slot slot = getSlot(p.getDownloadToken());
        if (slot==null) {
            // nothing to udpate, yet.
            return;
        }

        p.setStatus(slot.status);
        if (slot.isCompleted()) {
            p.setStatus(GProgress.STATUS_DOWNLOADED);
            p.setStatusMessage("Download Complete");
            p.setBytesDownloaded(slot.downloaded);
            p.setDownloaded(true);
            App.get().getDbManager().update(p);
            renameAndMove(p);
            return;
        }

        if (slot.isPaused()) {
            if (!p.isPaused()) {
                p.setStatus(GProgress.STATUS_PAUSED);
                p.setStatusMessage("Paused");
                updateByteCount(slot, p);
                App.get().getDbManager().update(p);
            }
            return;
        }

        if (slot.hasError()) {
            p.setStatus(GProgress.STATUS_ERROR);
            p.setStatusMessage(slot.status);
            updateByteCount(slot, p);
            App.get().getDbManager().update(p);
            return;
        }

        p.setStatus(GProgress.STATUS_IN_PROGRESS);
        p.setStatusMessage(slot.status);
        updateByteCount(slot, p);
        App.get().getDbManager().update(p);

    }

    private void updateByteCount(Slot slot, MovieEntry p) {
        double mb = parseDouble(slot.mb) * 1024d * 1024d;
        double mbleft = parseDouble(slot.mbleft) * 1024d * 1024d;
        p.setSize((long)mb);
        p.setBytesDownloaded((long)(mb-mbleft));
    }

    private double parseDouble(String val) {
        try {
            return Double.parseDouble(val);
        } catch (Throwable t) {
            return 0;
        }
    }

    public void monitor() {
        System.out.println("Begin Monitor");
        QueryBuilder<MovieEntry, String> builder = App.get().getDbManager().getMoviesDao().queryBuilder();
        try {
            builder.where().isNotNull("downloadToken").and().eq("downloaded",false);
            List<MovieEntry> mvs = builder.query();
            for (MovieEntry m: mvs) {
                monitor(m);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

        builder = App.get().getDbManager().getMoviesDao().queryBuilder();
        try {
            builder.where().isNotNull("downloadToken").and().eq("downloaded",true).and().ne("status",GProgress.STATUS_COMPLETE);
            List<MovieEntry> mvs = builder.query();
            for (MovieEntry m: mvs) {
                renameAndMove(m);
            }
            System.out.println("Moving and Renaming Count: " + mvs.size());
        } catch (Throwable e) {
            e.printStackTrace();
        }


        startMonitorThread();
        System.out.println("Monitor Started");
    }

    private void renameAndMove(MovieEntry m) {
        if (m.isComplete()) return;

        System.out.println("Rename and Move " + m);

        m.setStatus(GProgress.STATUS_MOVING);
        m.setStatusMessage("Moving and Renaming");
        App.get().getDbManager().update(m);

        File source = findLocalFile(m);
        if (source==null) {
            m.setStatus(GProgress.STATUS_ERROR);
            m.setStatusMessage("Unable to locate " + m.getLocalFile());
            App.get().getDbManager().update(m);
            return;
        }

        // we found the local file, now move to destination
        File destDir = App.get().getMoviesDir();
        if (!destDir.canWrite()) {
            m.setStatus(GProgress.STATUS_ERROR);
            m.setStatusMessage("Can't write to destination directory " + destDir.getAbsolutePath());
            App.get().getDbManager().update(m);
            return;
        }

        // rename and copy
        File newName = new File(destDir, filename(m, source));
        if (newName.exists()) {
            m.setStatus(GProgress.STATUS_ERROR);
            m.setStatusMessage("Destination file already exists " + newName.getAbsolutePath());
            App.get().getDbManager().update(m);
            return;
        }

        java.nio.file.Path target = null;
        try {
            target = Files.move(Paths.get(source.getAbsolutePath()), Paths.get(newName.getAbsolutePath()));
        } catch (IOException e) {
            e.printStackTrace();
            m.setStatus(GProgress.STATUS_ERROR);
            m.setStatusMessage(e.getMessage());
            App.get().getDbManager().update(m);
            return;
        }

        if (!newName.exists() || newName.length()==0) {
            m.setStatus(GProgress.STATUS_ERROR);
            m.setStatusMessage("Copy Failed.  Destination does not exist or was empty. ["+newName.getAbsolutePath()+"]");
            App.get().getDbManager().update(m);
            newName.delete();
            return;
        }

        // we copied OK
        m.setLocalFile(newName.getAbsolutePath());
        m.setStatus(GProgress.STATUS_COMPLETE);
        m.setStatusMessage("Copied to " + newName.getAbsolutePath());
        App.get().getDbManager().update(m);
        System.out.println("Movie Moved to " + newName.getAbsolutePath());
    }

    String filename(MovieEntry m, File source) {
        return m.getTitle() + (m.getYear()!=null?(" ("+m.getYear()+")"):"") + "." + getFileExtension(source);
    }

    private String getFileExtension(File file) {
        String name = file.getName();
        try {
            return name.substring(name.lastIndexOf(".") + 1);
        } catch (Exception e) {
            return "";
        }
    }

    private File findLocalFile(MovieEntry m) {
        Slot slot = getSlot(m.getDownloadToken());
        String filename = slot.filename;
        if (filename==null) filename=slot.storage;
        m.setLocalFile(filename);
        File dlDir = App.get().getDownloadDir();
        File source = findLocalFile(dlDir, filename);
        return source;
    }

    File findLocalFile(File dlDir, String filename) {
        if (filename==null) return null;
        if (filename.startsWith(File.separator)) filename=filename.substring(File.separator.length());

        File f = new File(dlDir, filename);
        if (f.exists()) {
            f =  resolveFile(f);
            return f;
        }
        return findLocalFile(dlDir, stripPath(filename));
    }

    File resolveFile(File fileOrDir) {
        if (fileOrDir==null) return null;
        if (fileOrDir.isDirectory()) {
            // we have a directory... let's find a valid file.
            try {
                // find the largest file
                List<File> files = Arrays.asList(fileOrDir.listFiles());
                Collections.sort(files, new Comparator<File>() {
                    @Override
                    public int compare(File file, File t1) {
                        return Long.compare(file.length(), t1.length()) * -1;
                    }
                });
                return files.get(0);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        } else if (fileOrDir.isFile()) {
            // we have a file
            return fileOrDir;
        }
        return null;
    }

    String stripPath(String filename) {
        if (filename.startsWith(File.separator)) filename=filename.substring(File.separator.length());
        if (filename.contains(File.separator)) {
            return filename.substring(filename.indexOf(File.separator)+1);
        }
        // no more paths
        return null;
    }

    private void startMonitorThread() {
        if (timer==null) {
            System.out.println("Starting Monitor Thread");
            timer = new Timer("dl-monitor", true);
            timer.scheduleAtFixedRate(new MonitorTask(), 5000, 5000);
        }
    }

    public void monitor(MovieEntry m) {
        if (movies.containsKey(m.getId())) return;
        if (m.isComplete()) return;
        if (m.getDownloadToken() == null) return;

        System.out.println("Monitoring: " + m);
        movies.put(m.getId(), m);
    }

    public GProgress getProgress(String mid) {
        System.out.println("Getting Progress for " + mid);

        // currently monitored movies are in our local map
        MovieEntry me = movies.get(mid);

        // This would happen if we have completed the download
        if (me==null) {
            System.out.println("No active monitor for " + mid + " will look it up");
            me = App.get().getDbManager().getMovieForId(mid);
            // if there isn't an entry, or the downloadload is null,
            // then we haven't stated any progress, yet.
            if (me == null || me.getDownloadToken() == null) {
                return new GProgress(mid, 0, 0).status(GProgress.STATUS_NOT_STARTED);
            }
        }

        GProgress progress = new GProgress(mid, me.getSize(), me.getBytesDownloaded());
        progress.status(me.getStatus());
        progress.statusMessage(me.getStatusMessage());

        return progress;
    }

    private Slot getSlot(String downloadToken) {
        if (downloadToken==null) return null;
        try {
            System.out.println("Finding Slot for " + downloadToken);
            Queue queue = App.get().getSABNZBDService().queue().execute().body().queue;
            Slot slot = findSlot(downloadToken, queue.slots);
            if (slot==null) {
                System.out.println("No active slot in queue for " + downloadToken + ", checking history");
                // check history
                queue = App.get().getSABNZBDService().history().execute().body().history;
                slot = findSlot(downloadToken, queue.slots);
            }
            if (slot==null) {
                System.out.println("Unable to find Slot for " + downloadToken);
            }
            return slot;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Slot findSlot(String downloadToken, List<Slot> queue) {
        if (downloadToken==null) return null;
        if (queue==null) {
            System.out.println("Queue is null");
            return null;
        }

        for (Slot s: queue) {
            if (s.nzo_id!=null && s.nzo_id.equals(downloadToken)) {
                return s;
            }
        }
        return null;
    }
}
