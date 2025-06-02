package com.example.tmdbclient.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// For the list of movies
@JsonClass(generateAdapter = true)
data class MovieListResponse(
    @Json(name = "page") val page: Int,
    @Json(name = "results") val movies: List<Movie>,
    @Json(name = "total_pages") val totalPages: Int,
    @Json(name = "total_results") val totalResults: Int,
)

@JsonClass(generateAdapter = true)
data class Movie(
    @Json(name = "id") val id: Int,
    @Json(name = "title") val title: String,
    @Json(name = "poster_path") val posterPath: String?,
    @Json(name = "overview") val overview: String,
    @Json(name = "release_date") val releaseDate: String,
    @Json(name = "vote_average") val voteAverage: Double,
)

// For the movie details page
@JsonClass(generateAdapter = true)
data class MovieDetails(
    @Json(name = "id") val id: Int,
    @Json(name = "title") val title: String,
    @Json(name = "poster_path") val posterPath: String?,
    @Json(name = "backdrop_path") val backdropPath: String?,
    @Json(name = "overview") val overview: String,
    @Json(name = "release_date") val releaseDate: String,
    @Json(name = "vote_average") val voteAverage: Double,
    @Json(name = "tagline") val tagline: String?,
    @Json(name = "genres") val genres: List<Genre>,
    @Json(name = "runtime") val runtime: Int?,
)

@JsonClass(generateAdapter = true) // Recommended for Moshi codegen
data class Genre(
    @Json(name = "id") val id: Int,
    @Json(name = "name") val name: String,
)