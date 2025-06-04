package com.example.tmdbclient.di

import com.example.tmdbclient.ui.nowPlaying.NowPlayingViewModel
import com.example.tmdbclient.ui.movieDetails.MovieDetailViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel {
        NowPlayingViewModel(get())
    }

    viewModel { (movieId: Int) ->
        MovieDetailViewModel(
            movieId = movieId,
            movieRepository = get()
        )
    }
}