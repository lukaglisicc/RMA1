package com.example.rma1.views.core.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rma1.auth.AuthManager
import com.example.rma1.auth.model.AuthState
import com.example.rma1.networking.ConnectivityState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SplashViewModel(
    connectivityState: ConnectivityState,
    val authManager: AuthManager,
) : ViewModel() {

    private val _bootState = MutableStateFlow<BootState>(BootState.Loading)
    val bootState: StateFlow<BootState> = _bootState.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()
    val isOffline: StateFlow<Boolean> = connectivityState.isOffline

    init {
        checkAuthState()
    }

    private fun checkAuthState() = viewModelScope.launch {
        try {

            val authState = authManager.awaitInitialAuthState()

            _isLoggedIn.value = authState is AuthState.Authenticated

            _bootState.value = BootState.Success
        } catch (e: Exception) {
            _bootState.value = BootState.Failed(e)
        }
    }

    fun retryBoot() {
        _bootState.value = BootState.Loading
        checkAuthState()
    }
}
