package com.example.rma1.views.movieList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.launch
import com.example.rma1.movies.MovieRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.withContext

class MovieListViewModel (
    private val movieRepository: MovieRepository
) : ViewModel() {

    //UI State flow
    private val _state = MutableStateFlow(MovieListContract.UiState())
    val state = _state.asStateFlow()
    private fun setState(reducer: MovieListContract.UiState.() -> MovieListContract.UiState){
        _state.getAndUpdate(reducer)
    }

    //UI Event flow
    private val events = MutableSharedFlow<MovieListContract.UiEvent>()
    fun setEvent(event: MovieListContract.UiEvent){
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
                .observeMovies()
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
                .observeMovieCount()
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
                    is MovieListContract.UiEvent.SortMovies -> {
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
            movieRepository.setFilters(
                _state.value.filters.copy(
                    sortType = sortType,
                )
            )
            withContext(Dispatchers.IO){
                runCatching {  movieRepository.queryMovies() }
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
                runCatching {  movieRepository.queryMovies() }
                    .onFailure { setState { copy(error = it) } }
            }
           setState { copy(isLoading = false) }
        }
    }

}