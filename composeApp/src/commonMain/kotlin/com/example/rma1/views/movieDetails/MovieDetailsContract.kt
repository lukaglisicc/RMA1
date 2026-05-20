package com.example.rma1.views.movieDetails

import com.example.rma1.movies.MovieDetailsFull

interface MovieDetailsContract {

    data class UiState(
        val movieDetailsFull: MovieDetailsFull? = null,
        val isLoading: Boolean = true,
        val error: Throwable? = null,
    )

    sealed class UiEvent{
        data class LaunchTrailer(val path: String) : UiEvent()
    }

    sealed class SideEffect{
        data class TrailerLaunched(val path: String) : SideEffect()
    }
}