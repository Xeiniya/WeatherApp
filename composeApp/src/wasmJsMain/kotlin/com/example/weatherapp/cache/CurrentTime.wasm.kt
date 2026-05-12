package com.example.weatherapp.cache

import kotlinx.browser.window

actual fun currentTimeMillis(): Long = window.performance.now().toLong()