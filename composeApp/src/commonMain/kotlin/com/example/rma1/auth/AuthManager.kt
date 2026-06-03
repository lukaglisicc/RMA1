package com.example.rma1.auth

import com.example.rma1.auth.model.AuthState
import com.example.rma1.movies.MovieRepository
import com.example.rma1.movies.network.NetworkMovieApi
import com.example.rma1.networking.auth.AuthToken
import com.example.rma1.views.core.shared.log
import kotlinx.coroutines.flow.StateFlow

class AuthManager(
    private val authStore: AuthStore,
    private val movieRepository: Lazy<MovieRepository>,
    private val movieApi: Lazy<NetworkMovieApi>,
) {

    suspend fun logIn(authToken: AuthToken){
        authStore.setAccessToken(authToken.token)
        val user = movieApi.value.getUser()
        log("here")
        log(user.username + " " + user.realName)
        authStore.setUser(
            username = user.username,
            realName = user.realName,
        )
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