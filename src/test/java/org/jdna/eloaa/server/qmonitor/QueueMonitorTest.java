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

import org.jdna.eloaa.server.db.MovieEntry;
import static org.junit.Assert.*;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * Created by seans on 10/08/16.
 */
public class QueueMonitorTest {

    @Test
    public void testFilename() throws Exception {
        QueueMonitor m = new QueueMonitor();
        File source = new File("./dl/123.mkv");
        MovieEntry me = new MovieEntry();
        me.setTitle("My Title");
        me.setYear("2014");
        String name = m.filename(me, source);
        assertEquals("My Title (2014).mkv", name);

        me.setYear(null);
        name = m.filename(me, source);
        assertEquals("My Title.mkv", name);
    }

    @Test
    public void testResolveFile() throws Exception {
        QueueMonitor q = new QueueMonitor();

        deleteDir("target/unittest/");
        File dir = new File("target/unittest/dl/test1/");
        dir.mkdirs();
        Files.write(Paths.get(new File(dir, "m1.mkv").toURI()), "1".getBytes());
        Files.write(Paths.get(new File(dir, "m2.mkv").toURI()), "11".getBytes());
        Files.write(Paths.get(new File(dir, "m3.mkv").toURI()), "111".getBytes());

        File f = q.resolveFile(dir);
        assertEquals("m3.mkv", f.getName());
        assertEquals(3, f.length());
        deleteDir("target/unittest/");
    }

    @Test
    public void testFindLocalFile() throws Exception {
        QueueMonitor q = new QueueMonitor();

        deleteDir("target/unittest/");
        File dir = new File("target/unittest/dl/test1/subdir1/");
        dir.mkdirs();
        Files.write(Paths.get(new File(dir, "m1.mkv").toURI()), "1".getBytes());

        File f = q.findLocalFile(new File("target/unittest/dl/test1/"), "/dl/test1/subdir1/m1.mkv");
        assertEquals("m1.mkv", f.getName());

        deleteDir("target/unittest/");
    }


    @Test
    public void testStripPath() throws Exception {
        QueueMonitor q = new QueueMonitor();
        String path;
        assertEquals("b/test.mkv", path=q.stripPath("/a/b/test.mkv"));
        assertEquals("test.mkv", path=q.stripPath(path));
        assertNull(path=q.stripPath(path));
    }

    public static void deleteDir(String dir) {
        Path directory = Paths.get(dir);
        try {
            Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (Throwable t) {
        }
    }

}