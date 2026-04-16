package com.example.rma1.movies

import kotlinx.coroutines.flow.Flow

interface MovieRepository {

    enum class SortType(){
        RATING,
        YEAR,
        TITLE,
        POPULARITY,
    }

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

    suspend fun getMovieDetails(id: String) : MovieDetailsFull

    suspend fun getGenres() : List<Genre>

}