package com.example.weatherapp.viewmodel

import com.example.weatherapp.model.CityWeatherItem
import kotlin.test.Test
import kotlin.test.*

class WeatherViewModelTest {

    @Test
    fun testUiStateInitialization() {
        val state = WeatherUiState()
        assertTrue(state.cities.isEmpty())
        assertNull(state.currentWeather)
        assertFalse(state.isLoading)
        assertNull(state.error)
    }

    @Test
    fun testUiStateWithCities() {
        val cities = listOf(
            CityWeatherItem("Минск", 20.0, "01d", "Ясно"),
            CityWeatherItem("Москва", 15.0, "02d", "Облачно")
        )
        val state = WeatherUiState(cities = cities, isLoading = false)
        assertEquals(2, state.cities.size)
        assertEquals("Минск", state.cities[0].city)
        assertEquals(20.0, state.cities[0].temp)
    }

    @Test
    fun testUiStateWithError() {
        val state = WeatherUiState(error = "Ошибка сети", isLoading = false)
        assertNotNull(state.error)
        assertEquals("Ошибка сети", state.error)
    }

    @Test
    fun testUiStateWithWeatherDetail() {
        val state = WeatherUiState(selectedCity = "Минск", showDetail = true, isLoading = false)
        assertEquals("Минск", state.selectedCity)
        assertTrue(state.showDetail)
    }

    @Test
    fun testUiStateWithForecast() {
        val state = WeatherUiState(showForecast = true, forecast = emptyList())
        assertTrue(state.showForecast)
        assertTrue(state.forecast.isEmpty())
    }

    @Test
    fun testCityWeatherItemValues() {
        val item = CityWeatherItem("Париж", 22.0, "03d", "Пасмурно")
        assertEquals("Париж", item.city)
        assertEquals(22.0, item.temp)
        assertEquals("Пасмурно", item.description)
    }

    @Test
    fun testCityWeatherItemNegativeTemp() {
        val item = CityWeatherItem("Якутск", -30.0, "13d", "Снег")
        assertEquals(-30.0, item.temp)
        assertTrue(item.temp!! < 0)
    }
}