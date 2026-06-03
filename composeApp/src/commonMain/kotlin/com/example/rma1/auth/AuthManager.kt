package com.example.rma1.auth

import com.example.rma1.auth.model.AuthState
import com.example.rma1.movies.MovieRepository
import com.example.rma1.networking.auth.AuthToken
import kotlinx.coroutines.flow.StateFlow

class AuthManager(
    private val authStore: AuthStore,
    private val movieRepository: Lazy<MovieRepository>,
) {

    suspend fun logIn(authToken: AuthToken){
        authStore.setAccessToken(authToken.token)
    }
    suspend fun logOut(){
        movieRepository.value.clearFavorites()
        movieRepository.value.clearWatchlist()
        movieRepository.value.clearQuizResults()
        authStore.clearAuthData()
    }

    fun getAuthState(): AuthState{
        return authStore.authState.value
    }

    suspend fun awaitInitialAuthState(): AuthState {
        return authStore.awaitInitialAuthState()
    }

    fun observeAuthState(): StateFlow<AuthState> {
        return authStore.authState
    }

}