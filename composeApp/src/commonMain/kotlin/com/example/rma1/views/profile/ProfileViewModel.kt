package com.example.rma1.views.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rma1.auth.AuthManager
import com.example.rma1.auth.model.AuthState
import com.example.rma1.movies.MovieRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileViewModel(
    private val movieRepository: MovieRepository,
    private val authManager: AuthManager,
) : ViewModel() {


    private val _state = MutableStateFlow(ProfileContract.UiState())
    val state = _state.asStateFlow()
    private fun setState(reducer: ProfileContract.UiState.() -> ProfileContract.UiState){
        _state.getAndUpdate(reducer)
    }

    //UI Event flow
    private val events = MutableSharedFlow<ProfileContract.UiEvent>()
    fun setEvent(event: ProfileContract.UiEvent){
        viewModelScope.launch { events.emit(event) }
    }

    init {
        getUserDetails()
        observeQuizInfo()
        observeWatchlistCount()
        observeFavoriteCount()
        observeEvents()
        refresh()
    }

    private fun getUserDetails() {
        when(val authState = authManager.getAuthState()){
            is AuthState.Authenticated -> {
                setState { copy(
                    username = authState.data.username ?: "",
                    realName = authState.data.realName ?: "",
                ) }
            }
            AuthState.Unauthenticated -> {}
        }
    }

    private fun observeWatchlistCount() {
        viewModelScope.launch {
            movieRepository
                .observeWatchlistCount()
                .collect { movieCount ->
                    setState {
                        this.copy(
                            watchlistCount = movieCount,
                        )
                    }
                }
        }
    }

    private fun observeFavoriteCount() {
        viewModelScope.launch {
            movieRepository
                .observeFavoritesCount()
                .collect { movieCount ->
                    setState { copy(
                            favoritesCount = movieCount,
                        ) }
                }
        }
    }

    private fun observeQuizInfo() {
        viewModelScope.launch {
            movieRepository
                .observeQuizInfo()
                .collect { quizInfo ->
                    setState { copy(
                        bestScore = quizInfo.bestScore,
                        quizCount = quizInfo.quizCount,
                    ) }
                }
        }
    }

    private fun observeEvents(){
        viewModelScope.launch {
            events.collect { event ->
                when(event){
                    is ProfileContract.UiEvent.LogOut -> {
                        authManager.logOut()
                    }
                }
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
                runCatching {
                    movieRepository.syncWatchlist()
                    movieRepository.syncFavorites()
                    movieRepository.syncQuizResults()
                }
                    .onFailure { setState { copy(error = it) } }
            }
            setState { copy(isLoading = false) }
        }
    }
}