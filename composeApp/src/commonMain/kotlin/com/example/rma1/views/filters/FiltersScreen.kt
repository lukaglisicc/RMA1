package com.example.rma1.views.filters

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.rma1.views.core.shared.ScreenBase
import com.example.rma1.views.core.shared.truncate

@Composable
fun FiltersScreen (
    viewModel: FiltersViewModel,
    onClose: () -> Unit,
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(viewModel){
        viewModel.effects.collect{ effect ->
            when(effect){
                FiltersContract.SideEffect.FiltersApplied -> {
                    onClose()
                }
            }
        }
    }

    FiltersScreen(
        state = state,
        onClose = onClose,
        eventPublisher = viewModel::setEvent,
    )

}

@Composable
private fun FiltersScreen (
    state: FiltersContract.UiState,
    onClose: () -> Unit,
    eventPublisher: (FiltersContract.UiEvent) -> Unit,
) {

    ScreenBase(
        onBack = onClose,
        title = "Filters",
    ){ padding ->

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

        else if (state.error != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = "Error: ${state.error.message}")
            }
        }

        else {
            FiltersScreenMain(
                state = state,
                eventPublisher = eventPublisher,
                padding = padding,
            )
        }
    }
}

@Composable
private fun FiltersScreenMain (
    state: FiltersContract.UiState,
    eventPublisher: (FiltersContract.UiEvent) -> Unit,
    padding: PaddingValues,
) {
    var search by remember { mutableStateOf(state.filters.query ?: "") }
    var minYear by remember { mutableStateOf(state.filters.minYear ?: 1920) }
    var maxYear by remember { mutableStateOf(state.filters.maxYear ?: 2025) }
    var rating by remember { mutableStateOf(state.filters.minRating ?: 0F) }

    val genres = state.genres

    var selectedGenreId by remember {mutableStateOf(state.filters.genreId)}

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(horizontal = 16.dp)
            .verticalScroll(state = scrollState)
    ) {

        Text(
            text = "Clear All",
            modifier = Modifier
                .clickable {
                search = ""
                selectedGenreId = null
                minYear = 1920
                maxYear = 2025
                rating = 0f
            }
                .fillMaxWidth(),
            textAlign = TextAlign.Right,
        )

        Spacer(Modifier.height(16.dp))

        SectionTitle("SEARCH")

        TextField(
            value = search,
            onValueChange = { search = it },
            placeholder = { Text("Search by movie title...") },
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp)),
        )

        Spacer(Modifier.height(16.dp))

        SectionTitle("GENRE")

        FlowRow {
            genres.forEach { genre ->
                val isSelected = genre.id == selectedGenreId

                FilterChip(
                    selected = isSelected,
                    onClick = {
                        if (isSelected) selectedGenreId = null
                        else selectedGenreId = genre.id
                    },
                    label = {
                        Text(genre.name)
                    },
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        SectionTitle("YEAR RANGE")

        Column(
            modifier = Modifier.fillMaxWidth(),
        ) {
            YearInput("From", minYear) { minYear = it }
            YearInput("To", maxYear) { maxYear = it }
        }

        Spacer(Modifier.height(16.dp))

        SectionTitle("MINIMUM RATING")

        Column{
            Slider(
                value = rating,
                onValueChange = { rating = it },
                valueRange = 0f..10f,
            )
            Text("⭐ ${rating.truncate(1)}")
        }

        Spacer(Modifier.weight(1f))

        Button(
            onClick = {
                eventPublisher(FiltersContract.UiEvent.ApplyFilters(
                    genreId = selectedGenreId,
                    query = search.ifEmpty { null },
                    minYear = minYear,
                    maxYear = maxYear,
                    minRating = rating,
                ))
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Apply Filters")
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.labelLarge,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
private fun YearInput(
    label: String,
    value: Int,
    onValueChange: (Int) -> Unit
) {
    Column {
        Text(label)

        TextField(
            value = value.toString(),
            onValueChange = { it.toIntOrNull()?.let(onValueChange) },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
