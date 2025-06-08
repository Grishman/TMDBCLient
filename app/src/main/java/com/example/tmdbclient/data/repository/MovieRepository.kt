package com.example.tmdbclient.data.repository

import com.example.tmdbclient.model.MovieDetails
import com.example.tmdbclient.model.MovieListResponse
import kotlinx.coroutines.flow.Flow

interface MovieRepository {
    suspend fun getNowPlayingMoviesPage(page: Int): Result<MovieListResponse>
    fun getMovieDetails(movieId: Int): Flow<Result<MovieDetails>>
}