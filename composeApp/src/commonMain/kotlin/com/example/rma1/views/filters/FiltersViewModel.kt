package com.example.rma1.views.filters

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rma1.movies.MovieRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.launch

class FiltersViewModel(
    private val movieRepository: MovieRepository,
): ViewModel() {

    private val _state = MutableStateFlow(FiltersContract.UiState())
    val state = _state.asStateFlow()

    private val events = MutableSharedFlow<FiltersContract.UiEvent>()
    fun setEvent (event: FiltersContract.UiEvent){
        viewModelScope.launch { events.emit(event) }
    }

    private val _effects = MutableSharedFlow<FiltersContract.SideEffect>()
    val effects = _effects.asSharedFlow()
    private fun setEffect(effect: FiltersContract.SideEffect){
        viewModelScope.launch { _effects.emit(effect) }
    }


    private fun setState(reducer: FiltersContract.UiState.() -> FiltersContract.UiState){
        _state.getAndUpdate (reducer)
    }

    init {

        loadGenres()
        observeEvents()
        observeFilters()
    }

    private fun loadGenres(){
        viewModelScope.launch{
            setState { copy(isLoading = true) }
            runCatching {
                movieRepository.getGenres()
            }.fold(
                onSuccess = { genres ->
                    setState { copy(genres = genres, error = null) }
                },
                onFailure = { error ->
                    setState { copy(genres = emptyList(), error = error) }
                }
            )
            setState { copy(isLoading = false) }
        }
    }

    private fun observeFilters() {
        viewModelScope.launch {
            movieRepository
                .observeFilters()
                .collect { filters ->
                    setState {
                        this.copy(
                            filters = filters,
                        )
                    }
                }
        }
    }

    private fun observeEvents(){
        viewModelScope.launch {
            events.collect { event ->
                when(event){
                    is FiltersContract.UiEvent.ApplyFilters -> {
                        applyFilters(
                            genreId = event.genreId,
                            query = event.query,
                            minYear = event.minYear,
                            maxYear = event.maxYear,
                            minRating = event.minRating,
                        )
                    }
                }
            }
        }
    }

    private suspend fun applyFilters(
        genreId: Int? = null,
        query: String? = null,
        minYear: Int? = null,
        maxYear: Int? = null,
        minRating: Float? = null,
    ) {
        movieRepository.setFilters(
            MovieRepository.Filters(
                genreId = genreId,
                query = query,
                minYear = minYear,
                maxYear = maxYear,
                minRating = minRating,
                sortType = _state.value.filters.sortType,
            )
        )
        setEffect(FiltersContract.SideEffect.FiltersApplied)
    }
}