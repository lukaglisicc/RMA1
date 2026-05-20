package com.example.rma1.views.movieList

import com.example.rma1.movies.MovieRepository

interface MovieListContract {

    data class UiState(
        val movieResponse: MovieRepository.MovieResponse? = null,
        var filters: MovieRepository.Filters = MovieRepository.Filters(),
        val isLoading: Boolean = true,
        val error: Throwable? = null
    )

    sealed class UiEvent {
        data class SortMovies(
            val sortBy: MovieRepository.SortType,
            val order: String = "desc",
            ) : UiEvent()
    }


}