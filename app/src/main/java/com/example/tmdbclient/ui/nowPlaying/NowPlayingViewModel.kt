package com.example.tmdbclient.ui.nowPlaying

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import com.example.data.paging.MoviePagingSource
import com.example.data.repository.MovieRepository
import com.example.data.model.Movie
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest
import kotlin.text.contains
import kotlin.text.isBlank

class NowPlayingViewModel(private val movieRepository: MovieRepository) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val pager: Pager<Int, Movie> = Pager(
        config = PagingConfig(
            pageSize = 20,
            enablePlaceholders = false,
        ),
        pagingSourceFactory = { MoviePagingSource(movieRepository) }
    )

    private val unfilteredMovies: Flow<PagingData<Movie>> = pager.flow
        .cachedIn(viewModelScope)

    val movies: Flow<PagingData<Movie>> = Pager(
        config = PagingConfig(
            pageSize = 20,
            enablePlaceholders = false,
        ),
        pagingSourceFactory = { MoviePagingSource(movieRepository) }
    ).flow
        .cachedIn(viewModelScope)

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val moviesFiltered: Flow<PagingData<Movie>> = searchQuery.debounce(500L)
        .flatMapLatest { query ->
            unfilteredMovies.mapLatest { pagingData ->
                if (query.isBlank()) {
                    pagingData
                } else {
                    pagingData.filter { movie2 ->
                        movie2.title.contains(query, ignoreCase = true)
                    }
                }

            }
        }
        .cachedIn(viewModelScope)

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }
}