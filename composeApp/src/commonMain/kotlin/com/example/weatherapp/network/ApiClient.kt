package com.example.weatherapp.network

import com.example.weatherapp.model.ForecastResponse
import com.example.weatherapp.model.WeatherResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class ApiClient {
    companion object {
        private const val API_KEY = "a04c1caadfa034328776284427407ad4"
        private const val BASE_URL = "https://api.openweathermap.org/data/2.5"
        private const val UNITS = "metric"
        private const val LANG = "ru"
    }

    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
                prettyPrint = false
            })
        }
    }

    suspend fun getCurrentWeather(city: String): WeatherResponse {
        return try {
            httpClient.get("$BASE_URL/weather") {
                parameter("q", city)
                parameter("appid", API_KEY)
                parameter("units", UNITS)
                parameter("lang", LANG)
            }.body()
        } catch (e: Exception) {
            throw Exception("Ошибка загрузки погоды для города $city: ${e.message}", e)
        }
    }

    suspend fun getForecast(city: String): ForecastResponse {
        return try {
            httpClient.get("$BASE_URL/forecast") {
                parameter("q", city)
                parameter("appid", API_KEY)
                parameter("units", UNITS)
                parameter("lang", LANG)
                parameter("cnt", 40)
            }.body()
        } catch (e: Exception) {
            throw Exception("Ошибка загрузки прогноза для города $city: ${e.message}", e)
        }
    }

    fun close() {
        httpClient.close()
    }
}