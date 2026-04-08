package com.example.rma1.movieDetails

import com.example.rma1.movies.MovieDetailsFull

interface MovieDetailsContract {

    data class UiState(
        val movieDetailsFull: MovieDetailsFull? = null,
        val isLoading: Boolean = true,
        val error: Throwable? = null,
    )
}