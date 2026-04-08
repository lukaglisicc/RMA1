package com.example.rma1

import androidx.compose.runtime.Composable
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.rma1.main.MainScreen
import com.example.rma1.main.MainViewModel
import com.example.rma1.movieDetails.MovieDetailsScreen
import com.example.rma1.movieDetails.MovieDetailsViewModel
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun Navigation(
    startDestination: String,
) {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {
        composable(
            route = "com/example/rma1/main"
        ) {
            val viewModel = koinViewModel<MainViewModel>()
            MainScreen(
                viewModel = viewModel,
                onMovieClick = { navController.navigateToMovie(it) },
            )
        }

        composable(
            route = "main/{$MOVIE_ID}",
            arguments = listOf(
                navArgument(MOVIE_ID) {
                    type = NavType.StringType
                    nullable = false
                }
            ),
        ){
            val viewModel = koinViewModel<MovieDetailsViewModel>()
            MovieDetailsScreen(
                viewModel = viewModel,
                onClose = {
                    navController.navigateUp()
                }
            )
        }
    }
}

private fun NavController.navigateToMovie(movieId: String){
    navigate("main/$movieId")
}


const val MOVIE_ID = "movieId"
inline val SavedStateHandle.movieId: String? get() = get(MOVIE_ID)
inline val SavedStateHandle.movieIdOrThrow: String get() = get(MOVIE_ID)
    ?: throw IllegalStateException("$MOVIE_ID is mandatory and can not be null")