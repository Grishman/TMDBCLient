package com.example.data.data.paging

import androidx.paging.PagingSource
import com.example.data.MainDispatcherRule
import com.example.data.model.Movie
import com.example.data.model.MovieListResponse
import com.example.data.paging.MoviePagingSource
import com.example.data.repository.MovieRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MoviePagingSourceTest {

    private val testDispatcher = StandardTestDispatcher()

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val mainCoroutineRule = MainDispatcherRule(testDispatcher)

    private val mockMovieRepository: MovieRepository = mockk(relaxed = true)
    private lateinit var moviePagingSource: MoviePagingSource

    // Sample data
    private val movie1 = Movie(1, "Movie 1", "/poster1.jpg", "Overview 1", "2023-01-01", 7.0)
    private val movie2 = Movie(2, "Movie 2", "/poster2.jpg", "Overview 2", "2023-01-02", 8.0)
    private val movie3 = Movie(3, "Movie 3", "/poster3.jpg", "Overview 3", "2023-01-03", 7.5)

    private val fakeMoviesPage1 = listOf(movie1, movie2)
    private val fakeMoviesPage2 = listOf(movie3)

    @Before
    fun setUp() {
        moviePagingSource = MoviePagingSource(mockMovieRepository)
    }

    @Test
    fun `load - initial refresh - success returns first page`() = runTest {
        // Given
        val responsePage1 = MovieListResponse(page = 1, movies = fakeMoviesPage1, totalPages = 2, totalResults = 3)
        coEvery { mockMovieRepository.getNowPlayingMoviesPage(1) } returns Result.success(responsePage1)

        // When
        val expectedResult = PagingSource.LoadResult.Page(
            data = fakeMoviesPage1,
            prevKey = null,
            nextKey = 2
        )
        val actualResult = moviePagingSource.load(
            PagingSource.LoadParams.Refresh(key = null, loadSize = 2, placeholdersEnabled = false)
        )

        // Then
        assertEquals(expectedResult, actualResult)
    }

    @Test
    fun `load - append - success returns next page`() = runTest {
        // Given
        val currentPageKey = 1
        val nextPageToLoad = currentPageKey + 1
        val responsePage2 = MovieListResponse(page = nextPageToLoad, movies = fakeMoviesPage2, totalPages = 2, totalResults = 3)
        coEvery { mockMovieRepository.getNowPlayingMoviesPage(nextPageToLoad) } returns Result.success(responsePage2)

        // When
        val expectedResult = PagingSource.LoadResult.Page(
            data = fakeMoviesPage2,
            prevKey = currentPageKey,
            nextKey = null
        )
        val actualResult = moviePagingSource.load(
            PagingSource.LoadParams.Append(key = nextPageToLoad, loadSize = 1, placeholdersEnabled = false)
        )

        // Then
        assertEquals(expectedResult, actualResult)
    }

    @Test
    fun `load - repository returns failure - returns Error`() = runTest {
        // Given
        val pageToLoad = 1
        val exception = RuntimeException("Network error from repository")
        coEvery { mockMovieRepository.getNowPlayingMoviesPage(pageToLoad) } returns Result.failure(exception)

        // When
        val actualResult = moviePagingSource.load(
            PagingSource.LoadParams.Refresh(key = pageToLoad, loadSize = 2, placeholdersEnabled = false)
        )

        // Then
        assertTrue(actualResult is PagingSource.LoadResult.Error)
        assertEquals(exception, (actualResult as PagingSource.LoadResult.Error).throwable)
    }
}