package com.example.tmdbclient.data.repository

import app.cash.turbine.test
import com.example.tmdbclient.MainDispatcherRule
import com.example.tmdbclient.data.remote.TmdbApiService
import com.example.tmdbclient.model.Movie
import com.example.tmdbclient.model.MovieDetails
import com.example.tmdbclient.model.MovieListResponse
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MovieRepositoryImplTest {
    private val testDispatcher = StandardTestDispatcher()

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val mainCoroutineRule = MainDispatcherRule(testDispatcher)

    private lateinit var movieRepository: MovieRepositoryImpl
    private val mockApiService = mockk<TmdbApiService>(relaxed = true)

    @Before
    fun setUp() {
        movieRepository = MovieRepositoryImpl(mockApiService, testDispatcher)
    }

    @Test
    fun `getNowPlayingMoviesPage failure returns failure result`() = runTest(testDispatcher) {
        // Given
        val page = 1
        val exception = RuntimeException("Network error")
        coEvery { mockApiService.getNowPlayingMovies(page = page) } throws exception

        // When
        val result = movieRepository.getNowPlayingMoviesPage(page)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    @Test
    fun `getNowPlayingMoviesPage success returns success result`() = runTest(testDispatcher) {
        // Given
        val page = 1
        val fakeMovies = listOf(
            Movie(1, "Movie 1", "/poster1.jpg", "Overview 1", "2023-01-01", 7.0),
            Movie(2, "Movie 2", "/poster2.jpg", "Overview 2", "2023-01-02", 8.0)
        )
        val fakeResponse = MovieListResponse(page = page, movies = fakeMovies, totalPages = 10, totalResults = 100)
        coEvery { mockApiService.getNowPlayingMovies(page = page) } returns fakeResponse

        // When
        val result = movieRepository.getNowPlayingMoviesPage(page)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(fakeResponse, result.getOrNull())
    }

    @Test
    fun `getMovieDetails success emits success result`() = runTest(testDispatcher) {
        // Given
        val movieId = 123
        val fakeMovieDetails = MovieDetails(
            id = movieId, title = "Test Movie", originalTitle = "Test Movie OT",
            posterPath = "/poster.jpg", backdropPath = "/backdrop.jpg",
            overview = "Great movie", releaseDate = "2023-01-01",
            voteAverage = 8.5,
        )
        coEvery { mockApiService.getMovieDetails(movieId) } returns fakeMovieDetails

        // When & Then
        movieRepository.getMovieDetails(movieId).test {
            val result = awaitItem()
            assertTrue(result.isSuccess)
            assertEquals(fakeMovieDetails, result.getOrNull())
            awaitComplete()
        }
    }

    @Test
    fun `getMovieDetails failure emits failure result`() = runTest(testDispatcher) {
        // Given
        val movieId = 123
        val exception = RuntimeException("API error")
        coEvery { mockApiService.getMovieDetails(movieId) } throws exception

        // When & Then
        movieRepository.getMovieDetails(movieId).test {
            val result = awaitItem()
            assertTrue(result.isFailure)
            assertEquals(exception, result.exceptionOrNull())
            awaitComplete()
        }
    }
}