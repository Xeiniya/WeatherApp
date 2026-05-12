package com.example.weatherapp.model

import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class WeatherModelsTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun testCityWeatherItemCreation() {
        val item = CityWeatherItem("Минск", 15.5, "01d", "Ясно")
        assertEquals("Минск", item.city)
        assertEquals(15.5, item.temp)
        assertEquals("01d", item.icon)
        assertEquals("Ясно", item.description)
    }

    @Test
    fun testForecastItemCreation() {
        val item = ForecastItem("2024-01-15", -5.0, 2.0, "Снег", "13d", 3.5, 85)
        assertEquals("2024-01-15", item.day)
        assertEquals(-5.0, item.tempMin)
        assertEquals(2.0, item.tempMax)
        assertEquals("Снег", item.description)
        assertEquals(3.5, item.windSpeed)
        assertEquals(85, item.humidity)
    }

    @Test
    fun testWeatherResponseDeserialization() {
        val jsonString = """
        {"name":"Минск","main":{"temp":20.5,"feels_like":18.3,"humidity":65,"pressure":1013},"weather":[{"description":"ясно","icon":"01d","main":"Clear"}],"wind":{"speed":4.5},"sys":{"country":"BY"},"visibility":10000}
        """.trimIndent()
        val response = json.decodeFromString<WeatherResponse>(jsonString)
        assertEquals("Минск", response.name)
        assertEquals(20.5, response.main.temp)
        assertEquals(65, response.main.humidity)
        assertEquals("ясно", response.weather.first().description)
        assertEquals(4.5, response.wind.speed)
        assertEquals("BY", response.sys.country)
    }

    @Test
    fun testForecastResponseDeserialization() {
        val jsonString = """
        {"list":[{"dt_txt":"2024-01-15 12:00:00","main":{"temp":5.0,"feels_like":2.0,"temp_min":3.0,"temp_max":7.0,"humidity":70,"pressure":1015},"weather":[{"description":"облачно","icon":"02d","main":"Clouds"}],"wind":{"speed":3.0}}]}
        """.trimIndent()
        val response = json.decodeFromString<ForecastResponse>(jsonString)
        assertEquals(1, response.list.size)
        assertEquals("2024-01-15 12:00:00", response.list.first().dtTxt)
        assertEquals(5.0, response.list.first().main.temp)
    }

    @Test
    fun testCityWeatherItemNullTemp() {
        val item = CityWeatherItem("Лондон", null, null, "Нет данных")
        assertEquals(null, item.temp)
        assertEquals("Нет данных", item.description)
    }

    @Test
    fun testWindDataDefault() {
        val wind = WindData()
        assertEquals(0.0, wind.speed)
    }

    @Test
    fun testMainDataDefaults() {
        val main = MainData()
        assertEquals(0.0, main.temp)
        assertEquals(0, main.humidity)
        assertEquals(0, main.pressure)
    }
}