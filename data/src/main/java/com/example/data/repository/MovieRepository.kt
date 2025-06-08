package com.example.data.repository

import kotlinx.coroutines.flow.Flow
import com.example.data.model.MovieDetails
import com.example.data.model.MovieListResponse

interface MovieRepository {
    suspend fun getNowPlayingMoviesPage(page: Int): Result<MovieListResponse>
    fun getMovieDetails(movieId: Int): Flow<Result<MovieDetails>>
}