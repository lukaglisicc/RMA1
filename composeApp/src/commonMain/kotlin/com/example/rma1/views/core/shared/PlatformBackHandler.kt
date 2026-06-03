package com.example.rma1.views.core.shared

import androidx.compose.runtime.Composable

@Composable
expect fun PlatformBackHandler(
    enabled: Boolean = true,
    onBack: () -> Unit
)