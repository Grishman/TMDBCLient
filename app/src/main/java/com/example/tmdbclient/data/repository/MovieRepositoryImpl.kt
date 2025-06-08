package com.example.tmdbclient.data.repository


import com.example.tmdbclient.data.remote.TmdbApiService
import com.example.tmdbclient.model.MovieDetails
import com.example.tmdbclient.model.MovieListResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class MovieRepositoryImpl(private val apiService: TmdbApiService, val dispatcher: CoroutineDispatcher = Dispatchers.IO) : MovieRepository {

    override suspend fun getNowPlayingMoviesPage(page: Int): Result<MovieListResponse> {
        return runCatching {
            delay(1500)
            apiService.getNowPlayingMovies(page = page)
        }
    }

    override fun getMovieDetails(movieId: Int): Flow<Result<MovieDetails>> = flow {
        delay(1500)
        val movieDetails = apiService.getMovieDetails(movieId = movieId)
        emit(Result.success(movieDetails))
    }.catch {
        emit(Result.failure(it))
    }.flowOn(dispatcher)
}