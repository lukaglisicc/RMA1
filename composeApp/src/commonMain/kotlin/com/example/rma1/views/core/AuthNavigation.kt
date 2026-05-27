package com.example.rma1.views.core

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.example.rma1.views.logIn.LogInScreen
import com.example.rma1.views.logIn.LogInViewModel
import com.example.rma1.views.signUp.SignUpScreen
import com.example.rma1.views.signUp.SignUpViewModel
import com.example.rma1.views.welcome.WelcomeScreen
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AuthNavigation(

) {
    val navController = rememberNavController()


    NavHost(
        navController = navController,
        startDestination = "authNav",
    ) {

        navigation(
            route = "authNav",
            startDestination = "welcome"
        ) {
            composable (
                route = "welcome"
            ) {
                WelcomeScreen(
                    onLogInClick = { navController.navigate("logIn") },
                    onSignUpClick = { navController.navigate("signUp") },
                )
            }

            composable (
                route = "logIn"
            ) {
                val viewModel = koinViewModel<LogInViewModel>()
                LogInScreen(
                    viewModel = viewModel,
                    onBackClick = { navController.navigateUp() },
                    onSignUpClick = { navController.navigate("signUp") },
                )
            }

            composable (
                route = "signUp"
            ) {
                val viewModel = koinViewModel<SignUpViewModel>()
                SignUpScreen(
                    viewModel = viewModel,
                    onBackClick = { navController.navigateUp() },
                    onLogInClick = { navController.navigate("logIn") },
                )
            }
        }
    }
}


