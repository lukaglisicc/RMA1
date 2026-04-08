package com.example.rma1.movieDetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rma1.movieIdOrThrow
import com.example.rma1.movies.MovieRepository
import kotlinx.coroutines.flow.MutableStateFlow
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

    init {
        loadMovieDetails(argMovieId)
    }

    private fun loadMovieDetails(movieId: String){
        viewModelScope.launch{
            setState { copy(isLoading = true) }
            runCatching {
                movieRepository.getMovieDetails(movieId)
            }.fold(
                onSuccess = { movieDetails ->
                    setState { copy(movieDetailsFull = movieDetails, error = null) }
                },
                onFailure = { error ->
                    setState { copy(movieDetailsFull = null, error = error) }
                }
            )
            setState { copy(isLoading = false) }
        }
    }
}