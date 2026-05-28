package com.example.rma1.networking

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ConnectivityState {
    private val _isOffline = MutableStateFlow(false)
    val isOffline = _isOffline.asStateFlow()

    fun setOffline(value: Boolean) {
        _isOffline.value = value
    }
}