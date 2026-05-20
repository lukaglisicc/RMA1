package com.example.rma1.views.filters

import com.example.rma1.movies.MovieRepository


interface FiltersContract {

    data class UiState(
        val genres: List<MovieRepository.Genre> = emptyList(),
        var filters: MovieRepository.Filters = MovieRepository.Filters(),
        val isLoading: Boolean = true,
        val error: Throwable? = null
    )

    sealed class UiEvent {
        data class ApplyFilters(
            val genreId: Int? = null,
            val query: String? = null,
            val minYear: Int? = null,
            val maxYear: Int? = null,
            val minRating: Float? = null,
        ): UiEvent()
    }

    sealed class SideEffect{
        data object FiltersApplied: SideEffect()
    }
}