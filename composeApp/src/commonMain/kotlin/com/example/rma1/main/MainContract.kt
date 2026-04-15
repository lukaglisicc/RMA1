package com.example.rma1.main

import com.example.rma1.movies.Movie
import com.example.rma1.movies.MovieRepository

interface MainContract {

    data class UiState(
        val movies: List<Movie> = emptyList(),
        val isLoading: Boolean = true,
        val error: Throwable? = null
    )

    sealed class UiEvent {
        data class sortMovies(
            val sortBy: MovieRepository.sortType,
            val order: String = "desc",
            ) : UiEvent()
    }


}