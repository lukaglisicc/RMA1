package com.example.rma1.movies
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class MovieResponse(
    val page: Int,
    val pageSize: Int,
    val totalItems: Int,
    val totalPages: Int,
    val items: List<Movie>
)

@Serializable
data class Movie(
    @SerialName("imdbId")
    val id: String,
    val title: String,
    val year: Int,
    @SerialName("imdbRating")
    val rating: Float,
    @SerialName("imdbVotes")
    val votes: Int,
    val posterPath: String,
    val genres: List<Genre>
)

@Serializable
data class Genre(
    val id: Int,
    val name: String
)

@Serializable
data class MovieDetails(
    @SerialName("imdbId")
    val id: String,
    val tmdbId: Int,
    val title: String,
    val originalTitle: String,
    @SerialName("overview")
    val desc: String,
    val tagline: String,
    val releaseDate: String,
    val year: Int,
    val runtime: Int,
    val budget: Int,
    val revenue: Int,
    val languageCode: String,
    val popularity: Float,
    val imdbRating: Float,
    val tmdbRating: Float,
    val tmdbVotes: Int,
    val posterPath: String,
    val backdropPath: String,
    val homepage: String,
    val genres: List<Genre>
)

@Serializable
data class CastResponse(
    val page: Int,
    val pageSize: Int,
    val totalItems: Int,
    val totalPages: Int,
    val items: List<Cast>
)

@Serializable
data class Cast(
    @SerialName("imdbId")
    val id: String,
    val name: String,
    val professions: String,
    val department: String,
    val profilePath: String? = null
)

@Serializable
data class ConfigPair(
    val key: String,
    val value: String,
)
data class MovieDetailsFull(
    val movieDetails: MovieDetails,
    val imagePaths: List<String>,
    val cast: List<Cast>,
    val trailerPath: String,
)

@Serializable
data class MovieImage(
    val filePath: String,
)

@Serializable
data class Backdrops(
    val backdrops: List<MovieImage>
)

data class Filters(
    val genreId: Int? = null,
    val query: String? = null,
    val minYear: Int? = 1920,
    val maxYear: Int? = 2025,
    val minRating: Float? = 0F,
)

fun Filters.appliedFilters() : Int {
    var i = 0
    if (this.query != null) i++
    if (this.genreId != null) i++
    if (this.minYear != 1920) i++
    if (this.maxYear != 2025) i++
    if (this.minRating != 0F) i++
    return i
}

@Serializable
data class Trailer (
    val key: String,
)