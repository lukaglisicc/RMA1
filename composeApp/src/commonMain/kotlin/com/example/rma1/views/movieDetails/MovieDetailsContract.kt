package com.example.rma1.views.movieDetails

import com.example.rma1.movies.MovieRepository


interface MovieDetailsContract {

    data class UiState(
        val movieDetails: MovieRepository.MovieDetails? = null,
        val isLoading: Boolean = true,
        val error: Throwable? = null,
        val isInWatchlist: Boolean = false,
        val isInFavorites: Boolean = false,
    )

    sealed class UiEvent{
        data class LaunchTrailer(val path: String) : UiEvent()
        data object AddToWatchlist : UiEvent()
        data object AddToFavorites : UiEvent()
        data object RemoveFromWatchlist : UiEvent()
        data object RemoveFromFavorites : UiEvent()
    }

    sealed class SideEffect{
        data class TrailerLaunched(val path: String) : SideEffect()
    }
}