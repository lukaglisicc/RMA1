package com.example.rma1.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.launch
import com.example.rma1.movies.MovieRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlin.time.Duration.Companion.seconds

class MainViewModel (
    private val movieRepository: MovieRepository
) : ViewModel() {

    //UI State flow
    private val _state = MutableStateFlow(MainContract.UiState())
    val state = _state.asStateFlow()
    private fun setState(reducer: MainContract.UiState.() -> MainContract.UiState){
        _state.getAndUpdate(reducer)
    }

    //UI Event flow
    private val events = MutableSharedFlow<MainContract.UiEvent>()
    fun setEvent(event: MainContract.UiEvent){
        viewModelScope.launch { events.emit(event) }
    }

    init {
        observeMovies()
        observeEvents()
    }

    private fun observeMovies() {
        viewModelScope.launch {
            movieRepository
                .observeMovies()
                .collect { moviesState ->
                    setState {
                        this.copy(
                            movies = moviesState.movies,
                            isLoading = moviesState.isLoading,
                            error = moviesState.error,
                        )
                    }
                }
        }
    }

    private fun observeEvents(){
        viewModelScope.launch {
            events.collect { event ->
                when(event){
                    is MainContract.UiEvent.sortMovies -> {
                        sortMovies(event.sortBy, event.order)
                    }
                }
            }
        }
    }

    private fun sortMovies(
        sortType: MovieRepository.sortType,
        order: String,
    ){
        movieRepository.setQueryParams(
            sortBy = sortType,
            sortOrder = order,
        )
    }



}