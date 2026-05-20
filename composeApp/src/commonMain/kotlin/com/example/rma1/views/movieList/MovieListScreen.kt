package com.example.rma1.views.movieList

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.rma1.movies.MovieRepository
import com.example.rma1.views.core.shared.MovieListItem
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MovieListViewModel,
    onMovieClick: (movieId: String) -> Unit,
    onFiltersClick: () -> Unit,
    onFavoritesClick: () -> Unit,
    onProfileClick: () -> Unit,
    onQuizClick: () -> Unit,
    onWatchlistClick: () -> Unit,
) {

    val state by viewModel.state.collectAsState()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val error = state.error
    val movieResponse = state.movieResponse
    val eventPublisher = viewModel::setEvent



    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerState = drawerState
            ) {
                DrawerMenuItem(
                    "Profile",
                    onProfileClick,
                    drawerState,
                )

                DrawerMenuItem(
                    "Watchlist",
                    onWatchlistClick,
                    drawerState,
                )

                DrawerMenuItem(
                    "Favorites",
                    onFavoritesClick,
                    drawerState,
                )

                DrawerMenuItem(
                    "Quiz",
                    onQuizClick,
                    drawerState,
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                Column {

                    TopAppBar(
                        title = {
                            Text("Showtime")
                        },
                        navigationIcon = {
                            IconButton(
                                onClick = {
                                    scope.launch {
                                        drawerState.open()
                                    }
                                }
                            ) {
                                Icon(
                                    Icons.Default.Menu,
                                    contentDescription = "Menu"
                                )
                            }
                        }
                    )

                    Row (
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ){

                        SortButton(eventPublisher)
                        FilterButton(
                            onClick = onFiltersClick,
                            state = state,
                        )
                    }

                    Text(
                        text = "${((movieResponse?.totalItems ?: 0).toString())} movies",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 40.dp),
                        textAlign = TextAlign.Right,
                        )
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
                }

                else if (error != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(text = "Error: ${error.message}")
                    }
                }

                else if (movieResponse?.items?.isEmpty() ?: true) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(text = "No movies that match filters.")
                    }
                }

                else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .verticalScroll(state = scrollState)
                    ) {
                        movieResponse.items.forEach { movie ->
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
}

@Composable
private fun FilterButton(
    onClick: () -> Unit,
    state: MovieListContract.UiState,
){
    Box{
        Button(
            onClick = onClick,
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

@Composable
private fun SortButton(
    eventPublisher: (MovieListContract.UiEvent) -> Unit,
){
    var isSortExpanded by remember {mutableStateOf(false)}

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
                    eventPublisher(MovieListContract.UiEvent.SortMovies(MovieRepository.SortType.RATING))
                },
            )
            DropdownMenuItem(
                text = {Text("Popularity")},
                onClick = {
                    isSortExpanded = false
                    eventPublisher(MovieListContract.UiEvent.SortMovies(MovieRepository.SortType.POPULARITY))
                },
            )
            DropdownMenuItem(
                text = {Text("Year")},
                onClick = {
                    isSortExpanded = false
                    eventPublisher(MovieListContract.UiEvent.SortMovies(MovieRepository.SortType.YEAR))
                },
            )
            DropdownMenuItem(
                text = {Text("Title")},
                onClick = {
                    isSortExpanded = false
                    eventPublisher(MovieListContract.UiEvent.SortMovies(MovieRepository.SortType.TITLE, "asc"))
                },
            )
        }
    }
}

@Composable
private fun DrawerMenuItem(
    title: String,
    onClick: () -> Unit,
    drawerState: DrawerState,
) {
    val scope = rememberCoroutineScope()

    Text(
        text = title,
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
                scope.launch {
                    drawerState.close()
                }
            }
            .padding(16.dp)
    )
}