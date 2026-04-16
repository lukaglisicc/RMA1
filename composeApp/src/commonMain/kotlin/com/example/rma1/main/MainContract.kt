package com.example.rma1.main

import com.example.rma1.movies.Filters
import com.example.rma1.movies.Movie
import com.example.rma1.movies.MovieRepository
import com.example.rma1.movies.MovieResponse

interface MainContract {

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