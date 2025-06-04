package com.example.tmdbclient.navigation

object AppDestinations {
    const val MOVIE_LIST_ROUTE = "movie_list"
    const val MOVIE_DETAIL_ROUTE = "movie_detail"
    const val MOVIE_ID_ARG = "movieId"
    const val MOVIE_DETAIL_WITH_ARG_ROUTE = "$MOVIE_DETAIL_ROUTE/{$MOVIE_ID_ARG}"
}