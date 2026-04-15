package com.example.rma1.movies

import kotlinx.coroutines.flow.Flow

interface MovieRepository {

    enum class sortType(){
        RATING,
        YEAR,
        TITLE,
        POPULARITY,
    }

    data class MoviesState(
        val movies: List<Movie> = emptyList(),
        val isLoading: Boolean = true,
        val error: Throwable? = null
    )

    fun observeMovies(): Flow<MoviesState>

    fun setQueryParams(
        pageSize: Int? = null,
        sortBy: sortType? = null,
        sortOrder: String? = null,
        genreId: Int? = null,
        query: String? = null,
        minYear: Int? = null,
        maxYear: Int? = null,
        minRating: Float? = null
    )

    fun resetQueryParams()

    fun resetQuerySort()

    fun queryMovies()

    suspend fun getMovieDetails(id: String) : MovieDetailsFull

}