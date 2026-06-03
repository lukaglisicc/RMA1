package com.example.rma1.views.core.shared

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable

@Composable
actual fun PlatformBackHandler(
    enabled: Boolean,
    onBack: () -> Unit,
) {
    BackHandler(
        enabled = enabled,
        onBack = onBack
    )
}