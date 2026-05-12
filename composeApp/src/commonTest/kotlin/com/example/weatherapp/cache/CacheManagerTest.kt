package com.example.weatherapp.cache

import com.example.weatherapp.model.CityWeatherItem
import com.example.weatherapp.model.ForecastItem
import kotlin.test.Test
import kotlin.test.*

class CacheManagerTest {
    private val cacheManager = CacheManager()

    @Test
    fun testCacheAndRetrieveWeather() {
        val item = CityWeatherItem("Минск", 20.0, "01d", "Ясно")
        cacheManager.cacheWeather("Минск", item)
        val cached = cacheManager.getCachedWeather("Минск")
        assertNotNull(cached)
        assertEquals("Минск", cached.city)
        assertEquals(20.0, cached.temp)
    }

    @Test
    fun testCacheValidity() {
        val item = CityWeatherItem("Москва", 15.0, "02d", "Облачно")
        cacheManager.cacheWeather("Москва", item)
        assertTrue(cacheManager.isCacheValid("Москва"))
    }

    @Test
    fun testCacheInvalidForNonExistentCity() {
        assertFalse(cacheManager.isCacheValid("Лондон"))
        assertNull(cacheManager.getCachedWeather("Лондон"))
    }

    @Test
    fun testCacheAndRetrieveForecast() {
        val forecast = listOf(
            ForecastItem("2024-01-15", -5.0, 2.0, "Снег", "13d", 3.0, 85),
            ForecastItem("2024-01-16", -3.0, 4.0, "Облачно", "02d", 2.5, 75)
        )
        cacheManager.cacheForecast("Минск", forecast)
        val cached = cacheManager.getCachedForecast("Минск")
        assertNotNull(cached)
        assertEquals(2, cached.size)
        assertEquals("Снег", cached[0].description)
    }

    @Test
    fun testClearCache() {
        val item = CityWeatherItem("Минск", 20.0, "01d", "Ясно")
        cacheManager.cacheWeather("Минск", item)
        cacheManager.clearCache()
        assertNull(cacheManager.getCachedWeather("Минск"))
    }
}