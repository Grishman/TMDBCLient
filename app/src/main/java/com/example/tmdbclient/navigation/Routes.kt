package com.example.tmdbclient.navigation

import kotlinx.serialization.Serializable

sealed class Routes {
    @Serializable
    data object NowPlayingMovies : Routes()

    @Serializable
    data class MovieDetails(val movieId: Int) : Routes()
}