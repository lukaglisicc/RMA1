package com.example.rma1.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ListItem
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import coil3.compose.AsyncImage
import com.example.rma1.movies.Movie
import com.example.rma1.movies.MovieRepository

@Composable
fun MainScreen(
    viewModel: MainViewModel,
    onMovieClick: (movieId: String) -> Unit,
) {
    val state by viewModel.state.collectAsState()

    MainScreen(
        state = state,
        onMovieClick = onMovieClick,
        eventPublisher = viewModel::setEvent,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainScreen(
    state: MainContract.UiState,
    onMovieClick: (movieId: String) -> Unit,
    eventPublisher: (MainContract.UiEvent) -> Unit,
) {

    val scrollState = rememberScrollState()
    var isSortExpanded by remember {mutableStateOf(false)}

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Text(text = "Premiere")
                    },
                )
                Box{
                    Button(
                        onClick = { isSortExpanded = true },
                    ){
                        Text("Sort")
                    }
                    DropdownMenu(
                        expanded = isSortExpanded,
                        onDismissRequest = { isSortExpanded = false },
                    ) {
                        DropdownMenuItem(
                            text = {Text("Rating")},
                            onClick = {
                                isSortExpanded = false
                                eventPublisher(MainContract.UiEvent.sortMovies(MovieRepository.sortType.RATING))
                            },
                        )
                        DropdownMenuItem(
                            text = {Text("Popularity")},
                            onClick = {
                                isSortExpanded = false
                                eventPublisher(MainContract.UiEvent.sortMovies(MovieRepository.sortType.POPULARITY))
                            },
                        )
                        DropdownMenuItem(
                            text = {Text("Year")},
                            onClick = {
                                isSortExpanded = false
                                eventPublisher(MainContract.UiEvent.sortMovies(MovieRepository.sortType.YEAR))
                            },
                        )
                        DropdownMenuItem(
                            text = {Text("Title")},
                            onClick = {
                                isSortExpanded = false
                                eventPublisher(MainContract.UiEvent.sortMovies(MovieRepository.sortType.TITLE, "asc"))
                            },
                        )
                    }
                }
            }
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