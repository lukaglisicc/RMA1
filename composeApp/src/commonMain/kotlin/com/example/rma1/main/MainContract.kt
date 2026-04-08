package com.example.rma1.main

import com.example.rma1.movies.Movie

interface MainContract {

    data class UiState(
        val movies: List<Movie> = emptyList(),
        val isLoading: Boolean = true,
        val error: Throwable? = null
    )


}