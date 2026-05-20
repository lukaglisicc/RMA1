package com.example.rma1.views.profile

import androidx.compose.runtime.Composable
import com.example.rma1.views.ScreenBase

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onClose: () -> Unit,
) {
    ScreenBase(
        onBack = onClose,
        title = "Profile",
    ){

    }
}