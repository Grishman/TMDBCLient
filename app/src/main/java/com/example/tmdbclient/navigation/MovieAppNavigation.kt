package com.example.tmdbclient.navigation


import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.tmdbclient.ui.nowPlaying.NowPlayingMoviesScreen
import com.example.tmdbclient.ui.movieDetails.MovieDetailScreen
import com.example.tmdbclient.ui.movieDetails.MovieDetailViewModel
import com.example.tmdbclient.ui.nowPlaying.NowPlayingViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf


@Composable
fun AppNavigation() {
    val navController: NavHostController = rememberNavController()

    NavHost(
        navController = navController, startDestination = Routes.NowPlayingMovies
    ) {
        // Movie List Screen
        composable<Routes.NowPlayingMovies> {
            NowPlayingMoviesScreen(viewModel = koinViewModel<NowPlayingViewModel>(), onMovieClick = { movieId ->
                navController.navigate(Routes.MovieDetails(movieId))
            })
        }

        // Movie Detail Screen
        composable<Routes.MovieDetails> { backStackEntry ->
            val route = backStackEntry.toRoute<Routes.MovieDetails>()
            val detailViewModel: MovieDetailViewModel = koinViewModel(parameters = { parametersOf(route.movieId) })
            MovieDetailScreen(viewModel = detailViewModel, onNavigateUp = {
                navController.navigateUp()
            })
        }
    }
}