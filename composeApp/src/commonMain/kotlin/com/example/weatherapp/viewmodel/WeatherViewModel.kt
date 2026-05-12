package com.example.weatherapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.cache.CacheManager
import com.example.weatherapp.model.*
import com.example.weatherapp.network.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class WeatherUiState(
    val cities: List<CityWeatherItem> = emptyList(),
    val selectedCity: String? = null,
    val currentWeather: WeatherResponse? = null,
    val forecast: List<ForecastItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val showDetail: Boolean = false,
    val showForecast: Boolean = false
)

class WeatherViewModel : ViewModel() {
    private val apiClient = ApiClient()
    private val cacheManager = CacheManager()

    private val _uiState = MutableStateFlow(WeatherUiState())
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    private val defaultCities = mutableListOf("Витебск", "Новополоцк")

    init {
        loadAllCities()
    }

    fun loadAllCities() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val weatherItems = mutableListOf<CityWeatherItem>()

            for (city in defaultCities) {
                try {
                    val cached = cacheManager.getCachedWeather(city)
                    if (cached != null && cacheManager.isCacheValid(city)) {
                        weatherItems.add(cached)
                        continue
                    }

                    val response = apiClient.getCurrentWeather(city)
                    val item = CityWeatherItem(
                        city = response.name,
                        temp = response.main.temp,
                        icon = response.weather.firstOrNull()?.icon,
                        description = response.weather.firstOrNull()?.description
                            ?.replaceFirstChar { it.uppercase() } ?: ""
                    )
                    cacheManager.cacheWeather(city, item)
                    weatherItems.add(item)
                } catch (e: Exception) {
                    val cached = cacheManager.getCachedWeather(city)
                    if (cached != null) {
                        weatherItems.add(cached)
                    } else {
                        weatherItems.add(
                            CityWeatherItem(city, null, null, "Ошибка загрузки")
                        )
                    }
                }
            }

            _uiState.value = _uiState.value.copy(
                cities = weatherItems,
                isLoading = false
            )
        }
    }

    fun addCity(cityName: String) {
        if (cityName.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Введите название города")
            return
        }
        if (defaultCities.contains(cityName)) {
            _uiState.value = _uiState.value.copy(error = "Город уже добавлен")
            return
        }
        defaultCities.add(cityName)
        loadAllCities()
    }

    fun removeCity(cityName: String) {
        defaultCities.remove(cityName)
        loadAllCities()
    }

    fun selectCity(city: String) {
        _uiState.value = _uiState.value.copy(
            selectedCity = city,
            showDetail = true,
            showForecast = false
        )
        loadCityDetail(city)
    }

    fun loadCityDetail(city: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val weather = apiClient.getCurrentWeather(city)
                _uiState.value = _uiState.value.copy(
                    currentWeather = weather,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Ошибка загрузки данных: ${e.message}",
                    isLoading = false
                )
            }
        }
    }

    fun loadForecast(city: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                showForecast = true
            )
            try {
                val cached = cacheManager.getCachedForecast(city)
                if (cached != null) {
                    _uiState.value = _uiState.value.copy(
                        forecast = cached,
                        isLoading = false
                    )
                    return@launch
                }

                val response = apiClient.getForecast(city)
                val dayMap = LinkedHashMap<String, ForecastItem>()

                for (entry in response.list) {
                    val datePart = entry.dtTxt.substringBefore(" ")
                    val timePart = entry.dtTxt.substringAfter(" ")

                    if (!dayMap.containsKey(datePart) || timePart == "12:00:00") {
                        dayMap[datePart] = ForecastItem(
                            day = datePart,
                            tempMin = entry.main.tempMin,
                            tempMax = entry.main.tempMax,
                            description = entry.weather.firstOrNull()?.description
                                ?.replaceFirstChar { it.uppercase() } ?: "",
                            icon = entry.weather.firstOrNull()?.icon ?: "",
                            windSpeed = entry.wind.speed,
                            humidity = entry.main.humidity
                        )
                    }
                }

                val forecastList = dayMap.values.take(5).toList()
                cacheManager.cacheForecast(city, forecastList)

                _uiState.value = _uiState.value.copy(
                    forecast = forecastList,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Ошибка загрузки прогноза: ${e.message}",
                    isLoading = false
                )
            }
        }
    }

    fun closeDetail() {
        _uiState.value = _uiState.value.copy(
            showDetail = false,
            currentWeather = null
        )
    }

    fun closeForecast() {
        _uiState.value = _uiState.value.copy(showForecast = false)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun getCities(): List<String> = defaultCities.toList()

    override fun onCleared() {
        super.onCleared()
        apiClient.close()
    }
}