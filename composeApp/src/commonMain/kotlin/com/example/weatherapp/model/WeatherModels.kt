package com.example.weatherapp.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeatherResponse(
    val name: String = "",
    val main: MainData = MainData(),
    val weather: List<WeatherDescription> = emptyList(),
    val wind: WindData = WindData(),
    val sys: SysData = SysData(),
    val visibility: Int? = null
)

@Serializable
data class MainData(
    val temp: Double = 0.0,
    @SerialName("feels_like") val feelsLike: Double = 0.0,
    val humidity: Int = 0,
    val pressure: Int = 0,
    @SerialName("temp_min") val tempMin: Double = 0.0,
    @SerialName("temp_max") val tempMax: Double = 0.0
)

@Serializable
data class WeatherDescription(
    val description: String = "",
    val icon: String = "",
    val main: String = ""
)

@Serializable
data class WindData(
    val speed: Double = 0.0
)

@Serializable
data class SysData(
    val country: String = ""
)

@Serializable
data class ForecastResponse(
    val list: List<ForecastEntry> = emptyList()
)

@Serializable
data class ForecastEntry(
    @SerialName("dt_txt") val dtTxt: String = "",
    val main: MainData = MainData(),
    val weather: List<WeatherDescription> = emptyList(),
    val wind: WindData = WindData()
)

data class CityWeatherItem(
    val city: String,
    val temp: Double?,
    val icon: String?,
    val description: String
)

data class ForecastItem(
    val day: String,
    val tempMin: Double,
    val tempMax: Double,
    val description: String,
    val icon: String,
    val windSpeed: Double,
    val humidity: Int
)