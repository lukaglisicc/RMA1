package com.example.rma1.views.favorites

import androidx.compose.runtime.Composable
import com.example.rma1.views.ScreenBase

@Composable
fun FavoritesScreen(
    viewModel: FavoritesViewModel,
    onClose: () -> Unit,
) {
    ScreenBase(
        onBack = onClose,
        title = "Favorites",
    ){

    }
}