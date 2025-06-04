package com.example.tmdbclient.navigation


import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.tmdbclient.ui.nowPlaying.NowPlayingMoviesScreen
import com.example.tmdbclient.ui.movieDetails.MovieDetailScreen
import com.example.tmdbclient.ui.movieDetails.MovieDetailViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf


@Composable
fun AppNavigation() {
    val navController: NavHostController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AppDestinations.MOVIE_LIST_ROUTE
    ) {
        // Movie List Screen
        composable(route = AppDestinations.MOVIE_LIST_ROUTE) {
            NowPlayingMoviesScreen(
                onMovieClick = { movieId ->
                    navController.navigate("${AppDestinations.MOVIE_DETAIL_ROUTE}/$movieId")
                }
            )
        }

        // Movie Detail Screen
        composable(
            route = AppDestinations.MOVIE_DETAIL_WITH_ARG_ROUTE,
            arguments = listOf(navArgument(AppDestinations.MOVIE_ID_ARG) {
                type = NavType.IntType
            })
        ) { backStackEntry ->
            val movieId = backStackEntry.arguments?.getInt(AppDestinations.MOVIE_ID_ARG)
            requireNotNull(movieId) { "Movie ID argument is missing" }

            val detailViewModel: MovieDetailViewModel = koinViewModel(
                parameters = { parametersOf(movieId) }
            )
            MovieDetailScreen(
                viewModel = detailViewModel,
                onNavigateUp = {
                    navController.navigateUp()
                }
            )
        }
    }
}