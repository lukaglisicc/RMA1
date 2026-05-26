package com.example.rma1.views.core

import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import com.example.rma1.views.core.splash.BootState
import com.example.rma1.views.core.splash.SplashScreen
import com.example.rma1.views.core.splash.SplashViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
@Preview
fun App() {

    val splashViewModel: SplashViewModel = koinViewModel()
    val bootState by splashViewModel.bootState.collectAsState()
    val isLoggedIn by splashViewModel.isLoggedIn.collectAsState()

    when (bootState) {
        BootState.Success -> {
            Navigation(
                startDestination = if (isLoggedIn) "main" else "welcome",
                authStore = splashViewModel.authStore
            )
        }

        is BootState.Failed -> {
        }

        BootState.Loading -> {
            SplashScreen()
        }
    }
}