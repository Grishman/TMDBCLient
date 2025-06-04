package com.example.tmdbclient.ui.movieDetails

import com.example.tmdbclient.model.MovieDetails


sealed interface MovieDetailUiState {

    data object Loading : MovieDetailUiState
    data class Success(val movieDetails: MovieDetails) : MovieDetailUiState
    data class Error(val errorMessage: String, val cause: Throwable? = null) : MovieDetailUiState
}