package com.example.rma1.movies.network
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

@Serializable
data class MovieImage(
    val filePath: String,
)

@Serializable
data class Backdrops(
    val backdrops: List<MovieImage>
)

@Serializable
data class Trailer (
    val key: String,
)
enum class ImageType(val id: Int){
    POSTER(1),
    BACKDROP(2),
    PROFILE(3),
    LOGO(4),
}

@Serializable
data class SignUpInfo(
    @SerialName("full_name")
    val name : String,
    val username: String,
    val password: String,
)

@Serializable
data class LogInInfo(
    val username: String,
    val password: String,
)

@Serializable
data class AuthToken(
    @SerialName("access_token")
    val token: String,
)

@Serializable
data class User(
    val username: String,
)

@Serializable
data class LeaderboardEntry(
    val rank: Int,
    @SerialName("user_id")
    val id: Int,
    val username: String,
    @SerialName("full_name")
    val fullName: String,
    val score: Float,
    @SerialName("played_at")
    val timestamp: Long,
    @SerialName("total_plays")
    val playCount: Int,
)

@Serializable
data class QuizResult(
    val score: Float,
    val category: Int = 1,
)

@Serializable
data class QuizResultWithTimestamp(
    val score: Float,
    val category: Int,
    @SerialName("played_at")
    val timestamp: Long,
)

@Serializable
data class QuizResultFull(
    val result: QuizResultWithTimestamp,
    val ranking: Int,
)