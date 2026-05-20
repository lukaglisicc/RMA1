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

    data class MovieResponse(
        val totalItems: Int,
        val items: List<Movie>,
    )

    data class Filters(
        val genreId: Int? = null,
        val query: String? = null,
        val minYear: Int? = 1920,
        val maxYear: Int? = 2025,
        val minRating: Float? = 0F,
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
        val title: String,
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

    data class MoviesState(
        val movieResponse: MovieResponse? = null,
        val isLoading: Boolean = true,
        val error: Throwable? = null,
    )

    fun observeMovies(): Flow<MoviesState>

    fun observeFilters(): Flow<Filters>

    suspend fun setQueryFilters(
        genreId: Int? = null,
        query: String? = null,
        minYear: Int? = null,
        maxYear: Int? = null,
        minRating: Float? = null
    )

    suspend fun setQuerySorting(
        sortBy: SortType,
        sortOrder: String,
    )

    suspend fun queryMovies()

    suspend fun getMovieDetails(id: String) : MovieDetails

    suspend fun getGenres() : List<Genre>

}