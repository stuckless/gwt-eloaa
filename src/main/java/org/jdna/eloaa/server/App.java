package org.jdna.eloaa.server;

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

import com.uwetrottmann.tmdb2.Tmdb;
import com.uwetrottmann.tmdb2.services.ConfigurationService;
import com.uwetrottmann.tmdb2.services.MoviesService;
import com.uwetrottmann.tmdb2.services.SearchService;
import org.jdna.eloaa.server.db.DBManager;
import org.jdna.eloaa.server.qmonitor.QueueMonitor;
import org.jdna.newreleases.DVDReleaseDatesService;
import org.jdna.newreleases.NewReleases;
import org.jdna.newznab.api.NZBS;
import org.jdna.newznab.api.services.NZBSService;
import org.jdna.sabnzbd.api.SABNZBD;
import org.jdna.sabnzbd.api.services.SABNZBDService;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Created by seans on 06/08/16.
 */
public class App {
    private static final App INSTANCE = new App();
    private Tmdb tmdb;
    private String tmdb_base_url;
    private NZBS nzbs;
    private SABNZBD sabnzbd;
    private QueueMonitor queueMonitor;
    private File moviesDir;
    private File configDir;
    private org.jdna.eloaa.server.db.DBManager dbManager;
    private File downloadDir;
    private Properties properties;
    private File propFile;
    private NewReleases newReleases;

    public static App get() {
        return INSTANCE;
    }

    private static final List<String> propertyKeys = Arrays.asList(
            "eloaa.sabnzbd.apikey",
            "eloaa.sabnzbd.url",
            "eloaa.nzbs.apikey",
            "eloaa.nzbs.url",
            "eloaa.movies.dir",
            "eloaa.tmdb.apikey",
            "eloaa.download.dir"
    );

    public static List<String> getPropertyKeys() {
        return propertyKeys;
    }

    public App() {
        init();
    }

    private void init() {
        System.out.println("Starting...");
        // force config dir creation
        getConfigDir();

        initConfig();

        // force db creation
        getDbManager();

        // init tmdb
        initTMDB();

        // init NZBS indexer
        initNZBSearcher();

        // init sab
        initSAB();

        // init queue monitor
        initQueueMonitor();

        newReleases = new NewReleases();

        // dump settings..
        dumpProperty("eloaa.sabnzbd.url", null);
        dumpProperty("eloaa.nzbs.url", null);
        dumpProperty("eloaa.config.dir", getConfigDir());
        dumpProperty("eloaa.movies.dir", getMoviesDir());
        dumpProperty("eloaa.download.dir", getDownloadDir());
        System.out.println("Initialized...");
    }

    private void initConfig() {
        this.properties = new Properties();
        propFile = new File(getConfigDir(), "config.properties");
        if (propFile.exists()) {
            try {
                FileReader fr = new FileReader(propFile);
                this.properties.load(fr);
                fr.close();
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

    private void dumpProperty(String prop, Object val) {
        System.out.printf("PROPERTY: %s:'%s'\n", prop, (val==null)?getProperty(prop): val);
    }

    private void initQueueMonitor() {
        queueMonitor = new QueueMonitor();
        // initialize in a new thread
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                queueMonitor.monitor();
            }
        });
        t.start();
    }

    private void initSAB() {
        System.out.println("INitializing SABNZBD");
        String url = getProperty("eloaa.sabnzbd.url");
        String api = getProperty("eloaa.sabnzbd.apikey");
        if (url==null || api==null) {
            System.err.println("Need to configure SAB");
            return;
        }
        sabnzbd = new SABNZBD(url, api);
    }

    private void initNZBSearcher() {
        System.out.println("INitializing NZB Indexer");
        String url = getProperty("eloaa.nzbs.url");
        String api = getProperty("eloaa.nzbs.apikey");
        if (url==null || api==null) {
            System.err.println("Need to configure NZB Indexer");
            return;
        }
        nzbs = new NZBS(url, api);
    }

    private void initTMDB() {
        System.out.println("INitializing TMDB Search");
        String api = getProperty("eloaa.tmdb.apikey");
        if (api==null) {
            System.err.println("Need to configure TMDB");
            return;
        }
        tmdb = new Tmdb(api);

        ConfigurationService configurationService = tmdb.configurationService();
        com.uwetrottmann.tmdb2.entities.Configuration cfg = null;
        try {
            cfg = configurationService.configuration().execute().body();
            tmdb_base_url = cfg.images.base_url;

            for (String s: cfg.images.poster_sizes) {
                System.out.println("POSTERS: " + s);
            }

            tmdb_base_url += cfg.images.poster_sizes.get(0);

            System.out.println("Poster URL: " + tmdb_base_url);
        } catch (Throwable e) {
            e.printStackTrace();
            System.out.println("We have likely reached our limits with TMDB");
            tmdb_base_url = "http://image.tmdb.org/t/p/w92";
        }
    }

    public Tmdb getTMDB() {
        return tmdb;
    }

    public String getProperty(String prop) {
        return getProperty(prop, null);
    }

    public String getProperty(String prop, String defaultValue) {
        String val = properties.getProperty(prop, System.getenv(prop.toUpperCase().replace('.','_')));
        if (val==null || val.trim().length()==0) {
            val = defaultValue;
        }
        return val;
    }

    public SearchService getIMDBSearchService() {
        return tmdb.searchService();
    }

    public File getConfigDir() {
        if (configDir==null) {
            String dir = System.getenv("eloaa.config.dir".toUpperCase().replace('.','_'));
            if (dir==null) {
                dir = System.getProperty("eloaa.config.dir", new File("./config").getAbsolutePath());
            }
            configDir = new File(dir);
            configDir.mkdirs();
        }
        return configDir.getAbsoluteFile();
    }

    public File getMoviesDir() {
        if (moviesDir==null) {
            String dir = getProperty("eloaa.movies.dir", new File("./movies").getAbsolutePath());
            moviesDir = new File(dir);
            moviesDir.mkdirs();
            properties.setProperty("eloaa.movies.dir", moviesDir.getAbsolutePath());
        }
        return moviesDir.getAbsoluteFile();
    }

    public File getDownloadDir() {
        if (downloadDir==null) {
            String dir = getProperty("eloaa.download.dir", new File("./downloads").getAbsolutePath());
            downloadDir = new File(dir);
            downloadDir.mkdirs();
            properties.setProperty("eloaa.download.dir", downloadDir.getAbsolutePath());
        }
        return downloadDir.getAbsoluteFile();
    }

    public DBManager getDbManager() {
        if (dbManager == null) {
            try {
                dbManager = new DBManager(getConfigDir());
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
        return dbManager;
    }

    public String getPosterBaseUrl() {
        return tmdb_base_url;
    }

    public NZBSService getNZBIndexService() {
        return nzbs.NZBSService();
    }

    public MoviesService getIMDBLookupService() {
        return tmdb.moviesService();
    }


    public SABNZBDService getSABNZBDService() {
        return sabnzbd.NZBSService();
    }

    public NZBS getNZBS() {
        return nzbs;
    }

    public QueueMonitor getQueueMonitor() {
        return queueMonitor;
    }

    public Properties getProperties() {
        return properties;
    }

    public void propertiesChanged(Map<String, String> props) {
        System.out.println("Properties Changed, Reconfiguring Services");
        if (props.containsKey("eloaa.nzbs.url") || props.containsKey("eloaa.nzbs.apikey")) {
            // init NZBS indexer
            initNZBSearcher();
        }
        if (props.containsKey("eloaa.sabnzbd.url") || props.containsKey("eloaa.sabnzbd.apikey")) {
            // init sab
            initSAB();
        }

        if (props.containsKey("eloaa.tmdb.apikey")) {
            // init tmdb
            initTMDB();
        }

        downloadDir=null;
        moviesDir=null;

        try (FileWriter fw = new FileWriter(propFile)) {
            properties.store(fw,"web update");
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Properties Changed, Serviced Reconfigured");
    }

    public DVDReleaseDatesService getNewReleasesService() {
        return newReleases.DVDReleaseDatesService();
    }
}
