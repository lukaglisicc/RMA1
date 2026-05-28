package com.example.rma1.views.core.shared

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.rma1.movies.MovieRepository

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MovieListItem(
    movie: MovieRepository.Movie,
    onClick: () -> Unit,
    onDeleteClick: (() -> Unit)? = null,
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
        },
        trailingContent = {
            if (onDeleteClick != null){
                IconButton(
                    onClick = onDeleteClick,
                ){
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Remove"
                    )
                }
            }
        }
    )
}