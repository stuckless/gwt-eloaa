package org.jdna.eloaa.server.db;

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

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.table.TableUtils;
import org.jdna.eloaa.shared.model.GMovie;
import org.jdna.eloaa.shared.model.MovieEntry;

import java.io.File;
import java.sql.SQLException;
import java.util.Date;

/**
 * Created by seans on 06/08/16.
 */
public class DBManager {
    private final File dbDIR;
    private final File dbFILE;
    private final String databaseUrl;
    private final JdbcConnectionSource connectionSource;
    private final Dao<MovieEntry, String> moviesDao;

    public DBManager(File baseDir) throws Exception {
        this.dbDIR=baseDir;
        this.dbFILE=new File(dbDIR,"movies.h2db");

        // this uses h2 but you can change it to match your database
        Class.forName("org.h2.Driver");
        databaseUrl = "jdbc:h2:"+dbFILE.getAbsolutePath();
        // create a connection source to our database
        connectionSource =
                new JdbcConnectionSource(databaseUrl);

        // instantiate the DAO to handle Account with String id
        moviesDao =
                DaoManager.createDao(connectionSource, MovieEntry.class);

        // upgrade
        moviesDao.executeRaw("ALTER TABLE IF EXISTS movieentry ADD COLUMN IF NOT EXISTS status VARCHAR(255);");
        moviesDao.executeRaw("ALTER TABLE IF EXISTS movieentry ADD COLUMN IF NOT EXISTS statusMessage VARCHAR(255);");
        moviesDao.executeRaw("ALTER TABLE IF EXISTS movieentry ADD COLUMN IF NOT EXISTS releaseDate TIMESTAMP;");

        // if you need to create the 'accounts' table make this call
        boolean devMode=false;
        if (devMode) {
            System.out.println("DEVMODE: Creating Tables");
            TableUtils.dropTable(connectionSource, MovieEntry.class, true);
            TableUtils.createTableIfNotExists(connectionSource, MovieEntry.class);
        } else {
            TableUtils.createTableIfNotExists(connectionSource, MovieEntry.class);
        }
        System.out.println("DBManager started");
    }

    public Dao<MovieEntry, String> getMoviesDao() {
        return moviesDao;
    }

    public MovieEntry addNewMovie(GMovie movie) throws SQLException, DBException {
        System.out.println("Adding Movie: " + movie);

        MovieEntry m = new MovieEntry();
        m.setId(movie.getId());
        m.setTitle(movie.getTitle());
        m.setYear(movie.getYear());
        m.setImdbID(movie.getImdbid());
        m.setDescription(movie.getDescription());
        m.setDateAdded(new Date());
        m.setPosterURL(movie.getPosterUrl());
        m.setReleaseDate(movie.getReleaseDate());

        MovieEntry m2 = moviesDao.queryForId(movie.getId());
        if (m2!=null) {
            throw new DBException(movie.getTitle()+" Already Exists");
        }

        System.out.println("DB Movie: " + m);

        try {
            moviesDao.create(m);
        } catch (SQLException se) {
            se.printStackTrace();
            throw se;
        }
        return  m;
    }

    public synchronized MovieEntry getMovieForId(String mid) {
        try {
            return moviesDao.queryForId(mid);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public synchronized void update(MovieEntry entry) {
        try {
            moviesDao.update(entry);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(String id) {
        try {
            moviesDao.deleteById(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
