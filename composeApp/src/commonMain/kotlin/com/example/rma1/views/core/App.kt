package com.example.rma1.views.core

import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import com.example.rma1.auth.model.AuthState
import com.example.rma1.views.core.splash.BootState
import com.example.rma1.views.core.splash.SplashScreen
import com.example.rma1.views.core.splash.SplashViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
@Preview
fun App() {

    val splashViewModel: SplashViewModel = koinViewModel()
    val bootState by splashViewModel.bootState.collectAsState()
    val authState by splashViewModel.authStore.authState.collectAsState()

    when (bootState) {
        BootState.Success -> {
            when(authState){
                is AuthState.Authenticated -> {
                    MainNavigation()
                }
                is AuthState.Unauthenticated -> {
                    AuthNavigation()
                }
            }
        }

        is BootState.Failed -> {
        }

        BootState.Loading -> {
            SplashScreen()
        }
    }
}