package com.example.rma1.views.watchlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rma1.movies.MovieRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WatchlistViewModel(
    private val movieRepository: MovieRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(WatchlistContract.UiState())
    val state = _state.asStateFlow()
    private fun setState(reducer: WatchlistContract.UiState.() -> WatchlistContract.UiState){
        _state.getAndUpdate(reducer)
    }

    //UI Event flow
    private val events = MutableSharedFlow<WatchlistContract.UiEvent>()
    fun setEvent(event: WatchlistContract.UiEvent){
        viewModelScope.launch { events.emit(event) }
    }

    init {
        observeFilters()
        observeMovies()
        observeMovieCount()
        observeEvents()
        refresh()
    }

    private fun observeMovies() {
        viewModelScope.launch {
            movieRepository
                .observeWatchlist()
                .collect { movies ->
                    setState {
                        this.copy(
                            movies = movies,
                        )
                    }
                }
        }
    }

    private fun observeMovieCount() {
        viewModelScope.launch {
            movieRepository
                .observeWatchlistCount()
                .collect { movieCount ->
                    setState {
                        this.copy(
                            movieCount = movieCount,
                        )
                    }
                }
        }
    }

    private fun observeFilters() {
        viewModelScope.launch {
            movieRepository
                .observeWatchlistFilters()
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
                    is WatchlistContract.UiEvent.SortMovies -> {
                        sortMovies(event.sortBy)
                    }
                }
            }
        }
    }


    private fun sortMovies(
        sortType: MovieRepository.SortType,
    ){
        viewModelScope.launch {
            movieRepository.setWatchlistFilters(
                _state.value.filters.copy(
                    sortType = sortType,
                )
            )
            withContext(Dispatchers.IO){
                runCatching {  movieRepository.syncWatchlist() }
                    .onFailure { setState { copy(error = it) } }
            }
        }
    }

    private fun refresh() {
        viewModelScope.launch (Dispatchers.IO){
            setState { copy(
                isLoading = true,
                error = null,
            ) }
            withContext(Dispatchers.IO){
                runCatching {  movieRepository.syncWatchlist() }
                    .onFailure { setState { copy(error = it) } }
            }
            setState { copy(isLoading = false) }
        }
    }
}