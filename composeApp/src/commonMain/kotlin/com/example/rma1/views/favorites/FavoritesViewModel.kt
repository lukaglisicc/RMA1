package com.example.rma1.views.favorites

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

class FavoritesViewModel(
    private val movieRepository: MovieRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(FavoritesContract.UiState())
    val state = _state.asStateFlow()
    private fun setState(reducer: FavoritesContract.UiState.() -> FavoritesContract.UiState){
        _state.getAndUpdate(reducer)
    }

    //UI Event flow
    private val events = MutableSharedFlow<FavoritesContract.UiEvent>()
    fun setEvent(event: FavoritesContract.UiEvent){
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
                .observeFavorites()
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
                .observeFavoritesCount()
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
                .observeFavoritesFilters()
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
                    is FavoritesContract.UiEvent.SortMovies -> {
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
            movieRepository.setFavoritesFilters(
                _state.value.filters.copy(
                    sortType = sortType,
                )
            )
            withContext(Dispatchers.IO){
                runCatching {  movieRepository.syncFavorites() }
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
                runCatching {  movieRepository.syncFavorites() }
                    .onFailure { setState { copy(error = it) } }
            }
            setState { copy(isLoading = false) }
        }
    }
}