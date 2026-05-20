package com.example.rma1

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.example.rma1.di.initKoin
import com.example.rma1.views.core.App

fun main() = application {
    initKoin()
    Window(
        onCloseRequest = ::exitApplication,
        title = "RMA1",
    ) {
        App()
    }
}