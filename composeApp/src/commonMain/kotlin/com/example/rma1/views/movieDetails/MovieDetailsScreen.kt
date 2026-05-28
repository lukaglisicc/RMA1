package com.example.rma1.views.movieDetails

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.rma1.movies.MovieRepository
import com.example.rma1.views.core.shared.ScreenBase
import com.example.rma1.views.core.shared.formatBudget
import com.example.rma1.views.core.shared.truncate
import okio.IOException

@Composable
fun MovieDetailsScreen(
    viewModel: MovieDetailsViewModel,
    onClose: () -> Unit,
) {
    val state by viewModel.state.collectAsState()

    val uriHandler = LocalUriHandler.current

    val eventPublisher = viewModel::setEvent


    LaunchedEffect(viewModel){
        viewModel.effects.collect{ effect ->
            when(effect){
                is MovieDetailsContract.SideEffect.TrailerLaunched -> {
                    uriHandler.openUri(
                        "https://www.youtube.com/watch?v=${effect.path}"
                    )
                }
            }
        }
    }

    MovieDetailsScreen(
        state = state,
        eventPublisher = eventPublisher,
        onClose = onClose,
    )
}

@Composable
private fun MovieDetailsScreen(
    state: MovieDetailsContract.UiState,
    eventPublisher: (MovieDetailsContract.UiEvent) -> Unit,
    onClose: () -> Unit,
) {

    ScreenBase(
        onBack = onClose,
        title = "",
        transparent = true,
    ) { padding ->
        if (state.isLoading){
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        }

        else if (state.error != null && state.error !is IOException) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = "Error: ${state.error.message}")
            }
        }

        else if (state.movieDetails == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = "No movie details.")
            }
        }

        else {
            MovieDetailsContent(
                movieDetails = state.movieDetails,
                eventPublisher = eventPublisher,
                padding = padding,
                state = state,
            )
        }
    }
}

@Composable
private fun MovieDetailsContent(
    movieDetails: MovieRepository.MovieDetails,
    eventPublisher: (MovieDetailsContract.UiEvent) -> Unit,
    padding: PaddingValues,
    state: MovieDetailsContract.UiState,
) {

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ){
        item {
            HeroSection(
                backdropUrl = movieDetails.backdropPath,
                posterUrl = movieDetails.posterPath,
                eventPublisher = eventPublisher,
                trailerUrl = movieDetails.trailerPath,
                state = state,
            )
        }
        item { MovieInfoSection(movieDetails) }
        item { OverviewSection(movieDetails.desc) }
        item { InfoStatsSection(movieDetails) }
        item { ImagesSection(movieDetails.imagePaths) }
        item { ActorsSection(movieDetails.cast) }
    }
}

@Composable
private fun HeroSection(
    backdropUrl: String,
    posterUrl: String,
    eventPublisher: (MovieDetailsContract.UiEvent) -> Unit,
    trailerUrl: String,
    state: MovieDetailsContract.UiState,
) {
    Box {
        // BACKDROP IMAGE
        AsyncImage(
            model = backdropUrl,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            contentScale = ContentScale.Crop
        )

        // gradient overlay
        Box(
            Modifier
                .fillMaxWidth()
                .height(300.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            MaterialTheme.colorScheme.background,
                        )
                    )
                )
        )

        Row(
            modifier = Modifier
                .align (Alignment.TopEnd)
                .padding(horizontal = 16.dp, vertical = 48.dp)
        ){
            //watchlist button
            IconButton(
                onClick = {
                    if(state.isInWatchlist){
                        eventPublisher(MovieDetailsContract.UiEvent.RemoveFromWatchlist)
                    } else {
                        eventPublisher(MovieDetailsContract.UiEvent.AddToWatchlist)
                    }
                },
            ) {
                Icon(
                    imageVector = if (state.isInWatchlist) {
                        Icons.Default.Bookmark
                    } else {
                        Icons.Outlined.BookmarkBorder
                    },
                    contentDescription = "Watchlist",
                    modifier = Modifier.size(36.dp)
                )
            }

            //favorite button
            IconButton(
                onClick = {
                    if(state.isInFavorites){
                        eventPublisher(MovieDetailsContract.UiEvent.RemoveFromFavorites)
                    } else {
                        eventPublisher(MovieDetailsContract.UiEvent.AddToFavorites)
                    }
                },
            ) {
                Icon(
                    imageVector = if (state.isInFavorites) {
                        Icons.Default.Favorite
                    } else {
                        Icons.Outlined.FavoriteBorder
                    },
                    contentDescription = "Favorite",
                    tint = if (state.isInFavorites) Color.Red else LocalContentColor.current,
                    modifier = Modifier.size(36.dp),
                )
            }
        }


        // play button centered
        FloatingActionButton(
            onClick = { eventPublisher(MovieDetailsContract.UiEvent.LaunchTrailer(trailerUrl)) },
            modifier = Modifier.align(Alignment.Center)
        ) {
            Icon(Icons.Default.PlayArrow, contentDescription = "Play")
        }

        // poster + title row at bottom
        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
            AsyncImage(
                model = posterUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
        }
    }
}

@Composable
private fun MovieInfoSection(movieDetails: MovieRepository.MovieDetails) {
    Column(modifier = Modifier.padding(16.dp)) {

        Text(
            text = movieDetails.title,
            style = MaterialTheme.typography.headlineMedium
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("⭐ ${movieDetails.imdbRating}/10")

            Spacer(Modifier.width(8.dp))

            movieDetails.genres.forEach {
                Card(
                    Modifier
                        .padding(2.dp)
                ){
                    Text(
                        text = it.name,
                        Modifier
                            .padding(4.dp)
                        )
                }
            }
        }
    }
}

@Composable
private fun OverviewSection(text: String) {
    Column(Modifier.padding(16.dp)) {
        Text("OVERVIEW", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        Text(text)
    }
}

@Composable
private fun InfoStatsSection(movieDetails: MovieRepository.MovieDetails) {
    Column(Modifier.padding(16.dp)) {
        Text("INFO", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            InfoCard("Budget", formatBudget(movieDetails.budget))
            InfoCard("Revenue", formatBudget(movieDetails.revenue))
            InfoCard("Language", movieDetails.languageCode)
            InfoCard("Popularity", movieDetails.popularity.truncate(1).toString())
        }
    }
}

@Composable
private fun InfoCard(title: String, value: String) {
    Card {
        Column(Modifier.padding(8.dp)) {
            Text(title, style = MaterialTheme.typography.labelSmall)
            Text(value, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
private fun ImagesSection(imagePaths: List<String>){
    Column(Modifier.padding(16.dp)) {
        Text("IMAGES", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        LazyRow {
            items(imagePaths) { img ->
                AsyncImage(
                    model = img,
                    contentDescription = null,
                    modifier = Modifier
                        .size(120.dp)
                        .padding(4.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            }
        }
    }
}

@Composable
private fun ActorsSection(cast: List<MovieRepository.Cast>){
    Column(Modifier.padding(16.dp)) {
        Text("CAST", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        cast.forEach { cast ->
            Row (
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ){
                AsyncImage(
                    model = cast.profilePath,
                    contentDescription = null,
                    modifier = Modifier
                        .size(120.dp)
                        .padding(4.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
                Text(
                    text = cast.name,
                    style = MaterialTheme.typography.titleMedium,
                )
            }
        }
    }
}
