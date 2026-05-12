package com.example.weatherapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherapp.model.CityWeatherItem
import com.example.weatherapp.model.ForecastItem
import com.example.weatherapp.model.WeatherResponse
import com.example.weatherapp.viewmodel.WeatherUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherAppContent(
    uiState: WeatherUiState,
    onAddCity: (String) -> Unit,
    onRemoveCity: (String) -> Unit,
    onRefresh: () -> Unit,
    onCityClick: (String) -> Unit,
    onLoadForecast: (String) -> Unit,
    onCloseDetail: () -> Unit,
    onCloseForecast: () -> Unit,
    onClearError: () -> Unit
) {
    var newCity by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Погода", fontWeight = FontWeight.Bold)
                },
                actions = {
                    TextButton(onClick = onRefresh) {
                        Text("🔄", style = MaterialTheme.typography.titleLarge)
                    }
                    TextButton(onClick = { showAddDialog = true }) {
                        Text("＋", style = MaterialTheme.typography.titleLarge)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            Column(modifier = Modifier.fillMaxSize()) {
                if (uiState.isLoading) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                uiState.error?.let { error ->
                    Snackbar(
                        modifier = Modifier.padding(16.dp),
                        action = {
                            TextButton(onClick = onClearError) {
                                Text("OK")
                            }
                        }
                    ) {
                        Text(error)
                    }
                }

                if (uiState.showDetail && uiState.currentWeather != null) {
                    CityDetailView(
                        weather = uiState.currentWeather!!,
                        forecast = uiState.forecast,
                        showForecast = uiState.showForecast,
                        onShowForecast = {
                            uiState.currentWeather?.name?.let { onLoadForecast(it) }
                        },
                        onClose = onCloseDetail,
                        onCloseForecast = onCloseForecast
                    )
                } else {
                    CitiesList(
                        cities = uiState.cities,
                        onCityClick = onCityClick,
                        onRemoveCity = onRemoveCity
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Добавить город") },
            text = {
                OutlinedTextField(
                    value = newCity,
                    onValueChange = { newCity = it },
                    label = { Text("Название города") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newCity.isNotBlank()) {
                            onAddCity(newCity.trim())
                            newCity = ""
                            showAddDialog = false
                        }
                    }
                ) {
                    Text("Добавить")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Отмена")
                }
            }
        )
    }
}

@Composable
fun CitiesList(
    cities: List<CityWeatherItem>,
    onCityClick: (String) -> Unit,
    onRemoveCity: (String) -> Unit
) {
    if (cities.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Нет добавленных городов\nНажмите + чтобы добавить",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(cities, key = { it.city }) { city ->
                CityWeatherCard(
                    city = city,
                    onClick = { onCityClick(city.city) },
                    onLongClick = { onRemoveCity(city.city) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CityWeatherCard(
    city: CityWeatherItem,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = city.city,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = city.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = city.temp?.let { "${it.toInt()}°C" } ?: "--",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = when {
                    city.temp == null -> MaterialTheme.colorScheme.onSurface
                    city.temp < 0 -> Color(0xFF64B5F6)
                    city.temp < 15 -> Color(0xFF80CBC4)
                    city.temp < 25 -> Color(0xFFA5D6A7)
                    else -> Color(0xFFFFCC80)
                }
            )
        }
    }
}

@Composable
fun CityDetailView(
    weather: WeatherResponse,
    forecast: List<ForecastItem>,
    showForecast: Boolean,
    onShowForecast: () -> Unit,
    onClose: () -> Unit,
    onCloseForecast: () -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            TextButton(onClick = onClose) {
                Text("← Назад к списку")
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "${weather.name}, ${weather.sys.country}",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "${(weather.main.temp * 10).toInt() / 10.0}",
                        fontSize = 64.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "Ощущается как ${(weather.main.feelsLike * 10).toInt() / 10.0}°C",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = weather.weather.firstOrNull()?.description
                            ?.replaceFirstChar { it.uppercase() } ?: "",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
                    Text(
                        text = "Подробности",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        DetailItem("💧", "Влажность", "${weather.main.humidity}%")
                        DetailItem("🌬", "Ветер", "${(weather.wind.speed * 10).toInt() / 10.0} м/с")
                        DetailItem("📊", "Давление", "${weather.main.pressure} гПа")
                    }
                    if (weather.visibility != null && weather.visibility > 0) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            DetailItem("👁", "Видимость", "${weather.visibility / 1000} км")
                        }
                    }
                }
            }
        }

        item {
            Button(
                onClick = onShowForecast,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("📅 Прогноз на 5 дней")
            }
        }

        if (showForecast && forecast.isNotEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Прогноз",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            TextButton(onClick = onCloseForecast) {
                                Text("✕")
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        forecast.forEach { item ->
                            ForecastRow(item)
                            if (item != forecast.last()) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    color = MaterialTheme.colorScheme.outlineVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DetailItem(emoji: String, label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = emoji, fontSize = 24.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun ForecastRow(item: ForecastItem) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = item.day, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
        Text(text = item.description, modifier = Modifier.weight(1.5f), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
            Text(text = "${item.tempMin.toInt()}°", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text = " / ", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text = "${item.tempMax.toInt()}°", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        }
    }
}