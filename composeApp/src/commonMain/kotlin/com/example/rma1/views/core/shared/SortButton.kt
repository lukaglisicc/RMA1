package com.example.rma1.views.core.shared

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.rma1.movies.MovieRepository

@Composable
fun SortButton(
    eventPublisher: (MovieRepository.SortType) -> Unit,
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
                    eventPublisher(MovieRepository.SortType.RATING)
                },
            )
            DropdownMenuItem(
                text = {Text("Popularity")},
                onClick = {
                    isSortExpanded = false
                    eventPublisher(MovieRepository.SortType.POPULARITY)
                },
            )
            DropdownMenuItem(
                text = {Text("Year")},
                onClick = {
                    isSortExpanded = false
                    eventPublisher(MovieRepository.SortType.YEAR)
                },
            )
            DropdownMenuItem(
                text = {Text("Title")},
                onClick = {
                    isSortExpanded = false
                    eventPublisher(MovieRepository.SortType.TITLE)
                },
            )
        }
    }
}