package com.example.rma1

import androidx.compose.ui.window.ComposeUIViewController
import com.example.rma1.di.initKoin
import com.example.rma1.views.core.App

fun MainViewController() = ComposeUIViewController {
    initKoin()
    App()
}