package com.example.rma1.views.quiz

import androidx.compose.runtime.Composable
import com.example.rma1.views.ScreenBase

@Composable
fun QuizScreen(
    viewModel: QuizViewModel,
    onClose: () -> Unit,
) {
    ScreenBase(
        onBack = onClose,
        title = "Quiz",
    ){

    }
}