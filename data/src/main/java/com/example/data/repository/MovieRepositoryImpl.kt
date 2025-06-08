package com.example.data.repository


import com.example.data.model.MovieDetails
import com.example.data.model.MovieListResponse
import com.example.data.remote.TmdbApiService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class MovieRepositoryImpl(private val apiService: TmdbApiService, val dispatcher: CoroutineDispatcher = Dispatchers.IO) : MovieRepository {

    override suspend fun getNowPlayingMoviesPage(page: Int): Result<MovieListResponse> {
        return runCatching {
            apiService.getNowPlayingMovies(page = page)
        }
    }

    override fun getMovieDetails(movieId: Int): Flow<Result<MovieDetails>> = flow {
        val movieDetails = apiService.getMovieDetails(movieId = movieId)
        emit(Result.success(movieDetails))
    }.catch {
        emit(Result.failure(it))
    }.flowOn(dispatcher)
}