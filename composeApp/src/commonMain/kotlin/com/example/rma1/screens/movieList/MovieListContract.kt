package com.example.rma1.screens.movieList

import com.example.rma1.movies.Filters
import com.example.rma1.movies.MovieRepository
import com.example.rma1.movies.MovieResponse

interface MovieListContract {

    data class UiState(
        val movieResponse: MovieResponse? = null,
        var filters: Filters = Filters(),
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