package com.example.rma1.views.movieList

import com.example.rma1.movies.MovieRepository

interface MovieListContract {

    data class UiState(
        val movies: List<MovieRepository.Movie> = emptyList(),
        val movieCount: Int = 0,
        val filters: MovieRepository.Filters = MovieRepository.Filters(),
        val isLoading: Boolean = true,
        val error: Throwable? = null
    )

    sealed class UiEvent {
        data class SortMovies(
            val sortBy: MovieRepository.SortType,
            ) : UiEvent()
    }


}