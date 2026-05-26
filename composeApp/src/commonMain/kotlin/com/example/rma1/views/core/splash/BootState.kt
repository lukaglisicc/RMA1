package com.example.rma1.views.core.splash


sealed interface BootState {

    data object Loading : BootState

    data object Success : BootState

    data class Failed(val error: Throwable) : BootState
}
