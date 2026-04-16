package com.example.rma1.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.rma1.movies.Movie
import com.example.rma1.movies.MovieRepository
import com.example.rma1.movies.appliedFilters

@Composable
fun MainScreen(
    viewModel: MainViewModel,
    onMovieClick: (movieId: String) -> Unit,
    onFiltersClick: () -> Unit,
) {
    val state by viewModel.state.collectAsState()

    MainScreen(
        state = state,
        onMovieClick = onMovieClick,
        eventPublisher = viewModel::setEvent,
        onFiltersClick = onFiltersClick,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainScreen(
    state: MainContract.UiState,
    onMovieClick: (movieId: String) -> Unit,
    eventPublisher: (MainContract.UiEvent) -> Unit,
    onFiltersClick: () -> Unit,
) {

    val scrollState = rememberScrollState()
    var isSortExpanded by remember {mutableStateOf(false)}

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ){
                            Text(text = "Premiere")
                            Box{
                                Button(
                                    onClick = onFiltersClick,
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                ){
                                    Text("Filters")
                                }

                                if(state.filters.appliedFilters() > 0){
                                    Box(
                                        modifier = Modifier
                                            .align (Alignment.TopEnd)
                                            .offset(x = (-16).dp)
                                            .size(20.dp)
                                            .background(MaterialTheme.colorScheme.errorContainer, shape = CircleShape),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Text(
                                            text = state.filters.appliedFilters().toString(),
                                            style = MaterialTheme.typography.labelSmall,
                                        )
                                    }
                                }
                            }
                        }
                    },
                )
                Row (
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ){
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
                                    eventPublisher(MainContract.UiEvent.SortMovies(MovieRepository.SortType.RATING))
                                },
                            )
                            DropdownMenuItem(
                                text = {Text("Popularity")},
                                onClick = {
                                    isSortExpanded = false
                                    eventPublisher(MainContract.UiEvent.SortMovies(MovieRepository.SortType.POPULARITY))
                                },
                            )
                            DropdownMenuItem(
                                text = {Text("Year")},
                                onClick = {
                                    isSortExpanded = false
                                    eventPublisher(MainContract.UiEvent.SortMovies(MovieRepository.SortType.YEAR))
                                },
                            )
                            DropdownMenuItem(
                                text = {Text("Title")},
                                onClick = {
                                    isSortExpanded = false
                                    eventPublisher(MainContract.UiEvent.SortMovies(MovieRepository.SortType.TITLE, "asc"))
                                },
                            )
                        }
                    }
                    Text("${((state.movieResponse?.totalItems ?: 0).toString())} movies")
                }
            }
        },
        content = { paddingValues ->
            if (state.isLoading){
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            } else if (state.error != null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(text = "Error: ${state.error.message}")
                }
            } else if (state.movieResponse?.items?.isEmpty() ?: true) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(text = "No movies that match filters.")
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(state = scrollState)
                ) {
                    state.movieResponse.items.forEach { movie ->
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
            Column{
                Text(movie.year.toString())
                Text(
                    text = "⭐${(movie.rating)}  ${(formatVotes(movie.votes))} votes",
                    style = MaterialTheme.typography.labelSmall,
                )
                FlowRow {
                    movie.genres.forEach { genre ->
                        Card(
                            Modifier
                                .padding(2.dp)
                        ){
                            Text(
                                text = genre.name,
                                Modifier
                                    .padding(2.dp),
                                style = MaterialTheme.typography.labelSmall,
                            )
                        }
                    }
                }
            }
        },
        leadingContent = {
            AsyncImage(
                model = movie.posterPath,
                contentDescription = null,
                modifier = Modifier.fillMaxHeight(),
            )
        }
    )
}

private fun formatVotes(votes: Int): String{
    return if(votes >= 1_000_000_000){
        "${votes / 1000_000_000}B"
    }else if(votes >= 1_000_000){
        "${votes / 1_000_000}M"
    } else if (votes >= 1_000){
        "${votes / 1000}K"
    } else {
        votes.toString()
    }
}