package com.example.tmdbclient.data.remote

import com.example.tmdbclient.model.MovieDetails
import com.example.tmdbclient.model.MovieListResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TmdbApiService {

    // Get "Now Playing" movies (for the latest movies list)
    @GET("movie/now_playing")
    suspend fun getNowPlayingMovies(
        @Query("language") language: String = "en-US",
        @Query("page") page: Int,
    ): MovieListResponse

    // Get movie details
    @GET("movie/{movie_id}")
    suspend fun getMovieDetails(
        @Path("movie_id") movieId: Int,
        @Query("language") language: String = "en-US",
    ): MovieDetails
}