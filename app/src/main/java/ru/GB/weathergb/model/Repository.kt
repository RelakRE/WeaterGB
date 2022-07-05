package ru.GB.weathergb.model

import ru.GB.weathergb.domain.Weather

fun interface APIWeather {
    fun getWeather(cityName: String, onComplete: (Weather?) -> Unit)
}