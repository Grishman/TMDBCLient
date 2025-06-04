package com.example.tmdbclient.ui.movieDetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tmdbclient.data.repository.MovieRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class MovieDetailViewModel(
    private val movieId: Int,
    private val movieRepository: MovieRepository,
) : ViewModel() {
    private val _movieDetailUiState: MutableStateFlow<MovieDetailUiState> =
        MutableStateFlow(MovieDetailUiState.Loading)
    val movieDetailUiState: StateFlow<MovieDetailUiState> =
        _movieDetailUiState.asStateFlow()

    init {
        fetchMovieDetails()
    }

    private fun fetchMovieDetails() {
        viewModelScope.launch {
            movieRepository.getMovieDetails(movieId)
                .onStart { _movieDetailUiState.value = MovieDetailUiState.Loading }
                .catch { exception ->
                    _movieDetailUiState.value = MovieDetailUiState.Error(
                        errorMessage = exception.localizedMessage ?: "An unknown error occurred",
                        cause = exception
                    )
                }
                .collect { result ->
                    result.fold(
                        onSuccess = { movieDetails ->
                            _movieDetailUiState.value = MovieDetailUiState.Success(movieDetails)
                        },
                        onFailure = { exception ->
                            _movieDetailUiState.value = MovieDetailUiState.Error(
                                errorMessage = exception.localizedMessage ?: "Failed to load movie details",
                                cause = exception
                            )
                        }
                    )
                }
        }
    }
}