package com.example.rma1.movies

import kotlinx.coroutines.flow.Flow

interface MovieRepository {

    data class MoviesState(
        val movies: List<Movie> = emptyList(),
        val isLoading: Boolean = true,
        val error: Throwable? = null
    )

    fun observeMovies(): Flow<MoviesState>

    fun queryMovies(
        pageSize: Int = 30,
        sortBy: String = "imdb_rating",
        sortOrder: String = "desc",
        genreId: Int? = null,
        query: String? = null,
        minYear: Int? = null,
        maxYear: Int? = null,
        minRating: Float? = null
    )

    suspend fun getMovieDetails(id: String) : MovieDetailsFull

}