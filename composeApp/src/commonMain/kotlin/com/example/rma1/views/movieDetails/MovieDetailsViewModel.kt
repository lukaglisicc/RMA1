package com.example.rma1.views.movieDetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rma1.views.core.movieIdOrThrow
import com.example.rma1.movies.MovieRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.launch

class MovieDetailsViewModel (
    savedStateHandle: SavedStateHandle,
    private val movieRepository: MovieRepository,
): ViewModel() {

    private val argMovieId = savedStateHandle.movieIdOrThrow
    private val _state = MutableStateFlow(MovieDetailsContract.UiState())
    val state = _state.asStateFlow()

    private fun setState(reducer: MovieDetailsContract.UiState.() -> MovieDetailsContract.UiState){
        _state.getAndUpdate(reducer)
    }

    private val events = MutableSharedFlow<MovieDetailsContract.UiEvent>()

    fun setEvent(event: MovieDetailsContract.UiEvent){
        viewModelScope.launch { events.emit(event) }
    }

    private val _effects = MutableSharedFlow<MovieDetailsContract.SideEffect>()
    val effects = _effects.asSharedFlow()
    private fun setEffect(effect: MovieDetailsContract.SideEffect){
        viewModelScope.launch { _effects.emit(effect) }
    }


    init {
        loadMovieDetails(argMovieId)
        observeEvents()
    }

    private fun loadMovieDetails(movieId: String){
        viewModelScope.launch{
            setState { copy(isLoading = true) }
            runCatching {
                movieRepository.getMovieDetails(movieId)
            }.fold(
                onSuccess = { movieDetails ->
                    setState { copy(movieDetails = movieDetails, error = null) }
                },
                onFailure = { error ->
                    setState { copy(movieDetails = null, error = error) }
                }
            )
            setState { copy(isLoading = false) }
        }
    }

    private fun observeEvents(){
        viewModelScope.launch {
            events.collect { event ->
                when(event){
                    is MovieDetailsContract.UiEvent.LaunchTrailer -> {
                        setEffect(MovieDetailsContract.SideEffect.TrailerLaunched(path = event.path))
                    }
                }
            }
        }
    }
}