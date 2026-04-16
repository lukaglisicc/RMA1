package com.example.rma1.filters

import com.example.rma1.movies.Filters
import com.example.rma1.movies.Genre

interface FiltersContract {

    data class UiState(
        val genres: List<Genre> = emptyList(),
        var filters: Filters = Filters(),
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