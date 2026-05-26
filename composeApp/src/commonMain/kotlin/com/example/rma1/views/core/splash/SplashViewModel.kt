package com.example.rma1.views.core.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rma1.auth.AuthStore
import com.example.rma1.auth.model.AuthState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SplashViewModel(
    val authStore: AuthStore,
) : ViewModel() {

    private val _bootState = MutableStateFlow<BootState>(BootState.Loading)
    val bootState: StateFlow<BootState> = _bootState.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    init {
        checkAuthState()
    }

    private fun checkAuthState() = viewModelScope.launch {
        try {

            val authState = authStore.awaitInitialAuthState()

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
