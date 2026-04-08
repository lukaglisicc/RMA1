package com.example.rma1.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.launch
import com.example.rma1.movies.MovieRepository
import kotlin.time.Duration.Companion.seconds

class MainViewModel (
    private val movieRepository: MovieRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MainContract.UiState())
    val state = _state.asStateFlow()
    private fun setState(reducer: MainContract.UiState.() -> MainContract.UiState){
        _state.getAndUpdate(reducer)
    }

    init {
        observeMovies()
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

    private fun test(){
        viewModelScope.launch {
            delay(2.seconds)
            movieRepository.queryMovies(pageSize = 100)
        }
    }



}