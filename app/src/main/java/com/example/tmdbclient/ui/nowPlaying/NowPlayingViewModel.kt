package com.example.tmdbclient.ui.nowPlaying

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.tmdbclient.data.paging.MoviePagingSource
import com.example.tmdbclient.data.repository.MovieRepository
import com.example.tmdbclient.model.Movie
import kotlinx.coroutines.flow.Flow

class NowPlayingViewModel(private val movieRepository: MovieRepository) : ViewModel() {

    val movies: Flow<PagingData<Movie>> = Pager(
        config = PagingConfig(
            pageSize = 20,
            enablePlaceholders = false,
        ),
        pagingSourceFactory = { MoviePagingSource(movieRepository) }
    ).flow
        .cachedIn(viewModelScope)
}