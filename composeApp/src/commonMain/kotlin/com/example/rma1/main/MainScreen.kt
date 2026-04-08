package com.example.rma1.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import coil3.compose.AsyncImage
import com.example.rma1.movies.Movie

@Composable
fun MainScreen(
    viewModel: MainViewModel,
    onMovieClick: (movieId: String) -> Unit,
) {
    val state by viewModel.state.collectAsState()

    MainScreen(
        state = state,
        onMovieClick = onMovieClick,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainScreen(
    state: MainContract.UiState,
    onMovieClick: (movieId: String) -> Unit,
) {

    val scrollState = rememberScrollState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Premiere")
                },
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(state = scrollState)
            ) {
                if (state.isLoading){
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator()
                    }
                } else if (state.error != null) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(text = "Error: ${state.error.message}")
                    }
                } else if (state.movies.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(text = "No movies that match filters.")
                    }
                } else {
                    state.movies.forEach { movie ->
                        MovieListItem(
                            movie = movie,
                            onClick = {onMovieClick(movie.id)}
                        )
                    }
                }
            }

        }
    )
}

@Composable
private fun MovieListItem(
    movie: Movie,
    onClick: (() -> Unit),
) {
    ListItem(
        modifier = Modifier.clickable(
            onClick = {
                onClick()
            },
        ),
        headlineContent = {
            Text(text = movie.title)
        },
        supportingContent = {
            Text(movie.year.toString())
        },
        trailingContent = {
            Text(movie.rating.toString())
        },
        leadingContent = {
            AsyncImage(
                model = movie.posterPath,
                contentDescription = null,
            )
        }
    )
}