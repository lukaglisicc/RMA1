package com.example.rma1.views.core

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
    val authState by splashViewModel.authManager.observeAuthState().collectAsState()
    val isOffline by splashViewModel.isOffline.collectAsState()

    val padding = if(isOffline) 30.dp else 0.dp

    when (bootState) {
        BootState.Success -> {
            when (authState) {
                is AuthState.Authenticated -> {
                    Box(modifier = Modifier.padding(bottom = padding)){
                        MainNavigation()
                    }

                }

                is AuthState.Unauthenticated -> {
                    Box(modifier = Modifier.padding(bottom = padding)){
                        AuthNavigation()
                    }
                }
            }
        }

        is BootState.Failed -> {
        }

        BootState.Loading -> {
            SplashScreen()
        }
    }
    if(isOffline){
        Box(
            contentAlignment = Alignment.BottomCenter,
            modifier = Modifier.fillMaxSize(),
        ){
            OfflineBanner()
        }

    }

}

@Composable
private fun OfflineBanner(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Red)
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Offline mode",
            color = Color.White
        )
    }
}