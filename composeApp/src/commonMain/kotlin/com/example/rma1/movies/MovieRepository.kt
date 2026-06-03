package com.example.rma1.movies

import kotlinx.coroutines.flow.Flow

interface MovieRepository {

    enum class SortType {
        RATING,
        YEAR,
        TITLE,
        POPULARITY,
    }

    data class Movie(
        val id: String,
        val title: String,
        val year: Int,
        val rating: Float,
        val votes: Int,
        val genres: List<Genre>,
        val posterPath: String,
    )

    data class Filters(
        val genreId: Int? = null,
        val query: String? = null,
        val minYear: Int? = 1920,
        val maxYear: Int? = 2025,
        val minRating: Float? = 0F,
        val sortType: SortType = SortType.RATING,
    ) {
        fun appliedFilters() : Int {
            var i = 0
            if (this.query != null) i++
            if (this.genreId != null) i++
            if (this.minYear != 1920) i++
            if (this.maxYear != 2025) i++
            if (this.minRating != 0F) i++
            return i
        }
    }

    data class Genre(
        val id: Int,
        val name: String,
    )

    data class MovieDetails(
        val id: String,
        val title: String,
        val year: Int,
        val desc: String,
        val budget: Int,
        val revenue: Int,
        val languageCode: String,
        val popularity: Float,
        val imdbRating: Float,
        val posterPath: String,
        val backdropPath: String,
        val genres: List<Genre>,
        val trailerPath: String,
        val imagePaths: List<String>,
        val cast: List<Cast>,
    )

    data class Cast(
        val name: String,
        val profilePath: String,
    )

    data class QuizResult(
        val score: Float,
        val category: Int = 1,
    )

    data class User(
        val username: String,
        val realName: String,
    )

    data class QuizInfo(
        val bestScore: Float,
        val quizCount: Int,
    )


    fun observeMovies(): Flow<List<Movie>>

    fun observeWatchlist(): Flow<List<Movie>>

    fun observeFavorites(): Flow<List<Movie>>

    fun observeFilters(): Flow<Filters>

    fun observeWatchlistFilters(): Flow<Filters>

    fun observeFavoritesFilters(): Flow<Filters>

    fun observeMovieDetails(id: String): Flow<MovieDetails?>

    fun observeMovieCount(): Flow<Int>

    fun observeWatchlistCount(): Flow<Int>

    fun observeFavoritesCount(): Flow<Int>

    suspend fun setFilters(filters: Filters)

    suspend fun setWatchlistFilters(filters: Filters)

    suspend fun setFavoritesFilters(filters: Filters)

    suspend fun queryMovies()

    suspend fun syncWatchlist()

    suspend fun syncFavorites()

    suspend fun syncQuizResults()

    suspend fun refreshMovieDetails(id: String)

    suspend fun getGenres() : List<Genre>
    suspend fun addToWatchlist(id : String)
    suspend fun addToFavorites(id : String)
    suspend fun removeFromWatchlist(id : String)
    suspend fun removeFromFavorites(id : String)
    suspend fun observeIsInWatchlist(id : String): Flow<Boolean>
    suspend fun observeIsInFavorites(id : String): Flow<Boolean>
    suspend fun isInWatchlist(id : String): Boolean
    suspend fun isInFavorites(id : String): Boolean
    suspend fun getMovieCache(count: Int, onlyLoaded: Boolean = false): List<MovieDetails>
    suspend fun clearFavorites()
    suspend fun clearWatchlist()
    suspend fun submitQuizResult(result: QuizResult): Int
    suspend fun clearQuizResults()
    suspend fun observeQuizInfo(): Flow<QuizInfo>
}