package com.example.rma1.views.core

import androidx.compose.runtime.Composable
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.rma1.views.favorites.FavoritesScreen
import com.example.rma1.views.favorites.FavoritesViewModel
import com.example.rma1.views.filters.FiltersScreen
import com.example.rma1.views.filters.FiltersViewModel
import com.example.rma1.views.movieList.MainScreen
import com.example.rma1.views.movieList.MovieListViewModel
import com.example.rma1.views.movieDetails.MovieDetailsScreen
import com.example.rma1.views.movieDetails.MovieDetailsViewModel
import com.example.rma1.views.profile.ProfileScreen
import com.example.rma1.views.profile.ProfileViewModel
import com.example.rma1.views.quiz.QuizScreen
import com.example.rma1.views.quiz.QuizViewModel
import com.example.rma1.views.watchlist.WatchlistScreen
import com.example.rma1.views.watchlist.WatchlistViewModel
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
            route = "main"
        ) {
            val viewModel = koinViewModel<MovieListViewModel>()
            MainScreen(
                viewModel = viewModel,
                onMovieClick = { navController.navigateToMovie(it) },
                onFiltersClick = { navController.navigate("filters")},
                onFavoritesClick = {navController.navigate("favorites")},
                onProfileClick = {navController.navigate("profile")},
                onQuizClick = {navController.navigate("quiz")},
                onWatchlistClick = {navController.navigate("watchlist")},
            )
        }

        composable(
            route = "filters"
        ) {
            val viewModel = koinViewModel<FiltersViewModel>()
            FiltersScreen(
                viewModel = viewModel,
                onClose = {
                    navController.navigateUp()
                },
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
                },
            )
        }

        composable(
            route = "favorites"
        ) {
            val viewModel = koinViewModel<FavoritesViewModel>()
            FavoritesScreen(
                viewModel = viewModel,
                onClose = {
                    navController.navigateUp()
                },
            )
        }

        composable(
            route = "profile"
        ) {
            val viewModel = koinViewModel<ProfileViewModel>()
            ProfileScreen(
                viewModel = viewModel,
                onClose = {
                    navController.navigateUp()
                },
            )
        }

        composable(
            route = "quiz"
        ) {
            val viewModel = koinViewModel<QuizViewModel>()
            QuizScreen(
                viewModel = viewModel,
                onClose = {
                    navController.navigateUp()
                },
            )
        }

        composable(
            route = "watchlist"
        ) {
            val viewModel = koinViewModel<WatchlistViewModel>()
            WatchlistScreen(
                viewModel = viewModel,
                onClose = {
                    navController.navigateUp()
                },
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