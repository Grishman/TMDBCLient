package com.example.tmdbclient.data.repository


import com.example.tmdbclient.data.remote.TmdbApiService
import com.example.tmdbclient.model.MovieDetails
import com.example.tmdbclient.model.MovieListResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class MovieRepository(private val apiService: TmdbApiService) {

    suspend fun getNowPlayingMoviesPage(page: Int): Result<MovieListResponse> {
        return try {
            delay(2500)
            val response = apiService.getNowPlayingMovies(page = page)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getMovieDetails(movieId: Int): Flow<Result<MovieDetails>> = flow {
        try {
            delay(2000)
            val movieDetails = apiService.getMovieDetails(movieId = movieId)
            emit(Result.success(movieDetails))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)
}