package com.example.weatherapp

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.example.weatherapp.ui.App

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Weather App",
        state = rememberWindowState(width = 480.dp, height = 800.dp)
    ) { App() }
}