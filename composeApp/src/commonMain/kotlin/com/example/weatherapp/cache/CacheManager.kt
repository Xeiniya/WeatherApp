package com.example.weatherapp.cache

import com.example.weatherapp.model.CityWeatherItem
import com.example.weatherapp.model.ForecastItem

expect fun currentTimeMillis(): Long

class CacheManager {
    private val weatherCache = mutableMapOf<String, CityWeatherItem>()
    private val forecastCache = mutableMapOf<String, List<ForecastItem>>()
    private val timestampCache = mutableMapOf<String, Long>()

    companion object {
        private const val CACHE_DURATION_MS = 600_000L
    }

    fun cacheWeather(city: String, item: CityWeatherItem) {
        weatherCache[city] = item
        timestampCache[city] = currentTimeMillis()
    }

    fun getCachedWeather(city: String): CityWeatherItem? {
        return weatherCache[city]
    }

    fun cacheForecast(city: String, forecast: List<ForecastItem>) {
        forecastCache[city] = forecast
    }

    fun getCachedForecast(city: String): List<ForecastItem>? {
        return forecastCache[city]
    }

    fun isCacheValid(city: String, maxAgeMs: Long = CACHE_DURATION_MS): Boolean {
        val timestamp = timestampCache[city] ?: return false
        return (currentTimeMillis() - timestamp) < maxAgeMs
    }

    fun clearCache() {
        weatherCache.clear()
        forecastCache.clear()
        timestampCache.clear()
    }
}