package com.example.data.paging


import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.data.model.Movie
import com.example.data.repository.MovieRepository


class MoviePagingSource(
    private val movieRepository: MovieRepository,
) : PagingSource<Int, Movie>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
        val currentPage = params.key ?: 1 // Start with page 1 if no key is provided
        val responseResult = movieRepository.getNowPlayingMoviesPage(page = currentPage)

        return responseResult.fold(
            onSuccess = { movieListResponse ->
                LoadResult.Page(
                    data = movieListResponse.movies,
                    prevKey = if (currentPage == 1) null else currentPage - 1,
                    nextKey = if (movieListResponse.movies.isEmpty() || currentPage == movieListResponse.totalPages) null else currentPage + 1
                )
            },
            onFailure = { exception ->
                LoadResult.Error(exception)
            }
        )
    }

    override fun getRefreshKey(state: PagingState<Int, Movie>): Int? {
        // Try to find the page key of the closest page to anchorPosition
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}