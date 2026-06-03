package com.example.rma1.views.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.rma1.views.core.shared.ScreenBase
import com.example.rma1.views.core.shared.truncate
import okio.IOException

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onClose: () -> Unit,
) {

    val state by viewModel.state.collectAsState()
    val eventPublisher = viewModel::setEvent

    ScreenBase(
        onBack = onClose,
        title = "Profile",
    ) { paddingValues ->

        if(state.error != null && state.error !is IOException) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = "Error: ${state.error?.message}")
            }
        }

        else if(state.isLoading){
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        }

        else{
            MainScreen(
                paddingValues = paddingValues,
                state = state,
                eventPublisher = eventPublisher,
            )
        }
    }
}

@Composable
private fun MainScreen(
    paddingValues: PaddingValues,
    state: ProfileContract.UiState,
    eventPublisher: (ProfileContract.UiEvent) -> Unit,
){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(24.dp),
    ) {

        // Header
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Surface(
                shape = CircleShape,
                tonalElevation = 4.dp,
                modifier = Modifier.size(96.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = state.realName,
                style = MaterialTheme.typography.headlineSmall
            )

            Text(
                text = "@${state.username}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(Modifier.height(32.dp))

        LazyColumn {
            item {  }
        }


        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {


            item {
                StatCard(
                    title = "Best Score",
                    value = state.bestScore.truncate(1).toString(),
                    icon = Icons.Default.EmojiEvents
                )
            }

            item {
                StatCard(
                    title = "Quizzes",
                    value = state.quizCount.toString(),
                    icon = Icons.Default.Quiz
                )
            }

            item {
                StatCard(
                    title = "Favorites",
                    value = state.favoritesCount.toString(),
                    icon = Icons.Default.Favorite
                )
            }

            item {
                StatCard(
                    title = "Watchlist",
                    value = state.watchlistCount.toString(),
                    icon = Icons.Default.Bookmark
                )
            }
        }

        OutlinedButton(
            onClick = {
                eventPublisher(ProfileContract.UiEvent.LogOut)
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.error
            )
        ) {
            Icon(
                Icons.AutoMirrored.Default.Logout,
                contentDescription = null
            )

            Spacer(Modifier.width(8.dp))

            Text("Log out")
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null
            )

            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium
            )

            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}