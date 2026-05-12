package com.example.weatherapp.ui

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weatherapp.ui.theme.WeatherAppTheme
import com.example.weatherapp.viewmodel.WeatherViewModel

@Composable
fun App() {
    val viewModel = viewModel { WeatherViewModel() }
    val uiState by viewModel.uiState.collectAsState()

    WeatherAppTheme {
        WeatherAppContent(
            uiState = uiState,
            onAddCity = { viewModel.addCity(it) },
            onRemoveCity = { viewModel.removeCity(it) },
            onRefresh = { viewModel.loadAllCities() },
            onCityClick = { viewModel.selectCity(it) },
            onLoadForecast = { viewModel.loadForecast(it) },
            onCloseDetail = { viewModel.closeDetail() },
            onCloseForecast = { viewModel.closeForecast() },
            onClearError = { viewModel.clearError() }
        )
    }
}