package org.jdna.newreleases;

import org.jdna.eloaa.client.model.GMovie;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

import java.util.List;

/**
 * Created by seans on 08/11/14.
 */
public interface DVDReleaseDatesService {
    /**
     *
     * @param year
     * @param intMonth 1=January
     * @param strMonth
     * @return
     */
    @GET("/releases/{year}/{intmonth}/new-dvd-releases-{intmonth}-{year}")
    Call<List<GMovie>> getMovies(@Path("year") int year, @Path("intmonth") int intMonth);
}
