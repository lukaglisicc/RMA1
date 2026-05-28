package com.example.rma1.views.movieDetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rma1.views.core.movieIdOrThrow
import com.example.rma1.movies.MovieRepository
import com.example.rma1.views.movieDetails.MovieDetailsContract.SideEffect.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
        observeMovieDetails()
        observeEvents()
        observeWatchlist()
        observeFavorites()
        refresh()
    }


    private fun observeMovieDetails() {
        viewModelScope.launch {
            movieRepository.observeMovieDetails(argMovieId).collect { details ->
                setState { copy(movieDetails = details) }
            }
        }
    }

    private fun observeEvents(){
        viewModelScope.launch {
            events.collect { event ->
                when(event){
                    is MovieDetailsContract.UiEvent.LaunchTrailer -> {
                        setEffect(TrailerLaunched(path = event.path))
                    }

                    is MovieDetailsContract.UiEvent.AddToFavorites -> {
                        runCatching {
                            setState { copy( isInFavorites = true ) }
                            withContext(Dispatchers.IO){
                                movieRepository.addToFavorites(argMovieId)
                            }
                        }
                            .onFailure {
                                favoriteRollback()
                            }
                    }
                    is MovieDetailsContract.UiEvent.AddToWatchlist -> {
                        runCatching {
                            setState { copy( isInWatchlist = true ) }
                            withContext(Dispatchers.IO){
                                movieRepository.addToWatchlist(argMovieId)
                            }
                        }
                            .onFailure {
                                watchlistRollback()
                            }
                    }

                    is MovieDetailsContract.UiEvent.RemoveFromFavorites -> {
                        runCatching {
                            setState { copy( isInFavorites = false ) }
                            withContext(Dispatchers.IO){
                                movieRepository.removeFromFavorites(argMovieId)
                            }
                        }
                            .onFailure {
                                favoriteRollback()
                            }
                    }
                    is MovieDetailsContract.UiEvent.RemoveFromWatchlist -> {
                        runCatching {
                            setState { copy( isInWatchlist = false ) }
                            withContext(Dispatchers.IO){
                                movieRepository.removeFromWatchlist(argMovieId)
                            }
                        }
                            .onFailure {
                                watchlistRollback()
                            }
                    }
                }
            }
        }
    }

    private fun observeWatchlist(){
        viewModelScope.launch {
            movieRepository.observeIsInWatchlist(argMovieId).collect { result ->
                setState { copy(isInWatchlist = result) }
            }
        }
    }

    private fun observeFavorites(){
        viewModelScope.launch {
            movieRepository.observeIsInFavorites(argMovieId).collect { result ->
                setState { copy(isInFavorites = result) }
            }
        }
    }

    private fun refresh() {
        viewModelScope.launch {
            setState { copy(
                isLoading = true,
                error = null
            ) }
            runCatching {
                withContext(Dispatchers.IO){
                    movieRepository.refreshMovieDetails(argMovieId)
                }
            }
                .onFailure { setState { copy(error = it) } }
            setState { copy(isLoading = false) }
        }
    }

    private fun favoriteRollback() {
        viewModelScope.launch (Dispatchers.IO) {
            val isFavorite = movieRepository.isInFavorites(argMovieId)
            setState { copy(
                isInFavorites = isFavorite
            ) }
        }
    }

    private fun watchlistRollback() {
        viewModelScope.launch (Dispatchers.IO) {
            val isInWatchlist = movieRepository.isInWatchlist(argMovieId)
            setState { copy(
                isInWatchlist = isInWatchlist
            ) }
        }

    }
}