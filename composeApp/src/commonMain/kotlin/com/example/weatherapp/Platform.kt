package com.example.weatherapp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform