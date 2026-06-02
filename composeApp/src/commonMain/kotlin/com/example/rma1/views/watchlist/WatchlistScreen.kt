package com.example.rma1.views.watchlist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.rma1.views.core.shared.FilterButton
import com.example.rma1.views.core.shared.MovieListItem
import com.example.rma1.views.core.shared.ScreenBase
import com.example.rma1.views.core.shared.SortButton
import okio.IOException

@Composable
fun WatchlistScreen(
    viewModel: WatchlistViewModel,
    onClose: () -> Unit,
    onMovieClick: (movieId: String) -> Unit,
    onFiltersClick: () -> Unit,
) {
    val state by viewModel.state.collectAsState()
    val error = state.error
    val movies = state.movies
    val movieCount = state.movieCount
    val eventPublisher = viewModel::setEvent


    ScreenBase(
        onBack = onClose,
        title = "Watchlist",
    ){ paddingValues ->
        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                SortButton(
                    eventPublisher = {
                        eventPublisher(WatchlistContract.UiEvent.SortMovies(it))
                    },
                )
                FilterButton(
                    onClick = onFiltersClick,
                    appliedFilters = state.filters.appliedFilters(),
                )
            }

            Text(
                text = "${((movieCount).toString())} movies",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp),
                textAlign = TextAlign.Right,
            )

            if (state.isLoading){
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }

            else if (error != null && error !is IOException) {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(text = "Error: ${error.message}")
                }
            }

            else if (movies.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    if(state.filters.appliedFilters() > 0)
                        Text(text = "No movies that match filters.")
                    else
                        Text(text = "No movies in watchlist.")
                }
            }

            else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    items(movies) { movie ->
                        MovieListItem(
                            movie = movie,
                            onClick = {onMovieClick(movie.id)},
                            onDeleteClick = {eventPublisher(WatchlistContract.UiEvent.RemoveFromWatchlist(movie.id))}
                        )
                    }
                }
            }
        }
    }
}