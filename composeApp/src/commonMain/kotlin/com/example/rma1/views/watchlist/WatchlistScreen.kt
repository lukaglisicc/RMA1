package com.example.rma1.views.watchlist

import androidx.compose.runtime.Composable
import com.example.rma1.views.core.shared.ScreenBase

@Composable
fun WatchlistScreen(
    viewModel: WatchlistViewModel,
    onClose: () -> Unit,
) {
    ScreenBase(
        onBack = onClose,
        title = "Watchlist",
    ){

    }
}