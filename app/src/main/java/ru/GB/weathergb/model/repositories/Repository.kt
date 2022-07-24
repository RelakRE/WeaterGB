package ru.GB.weathergb.model.repositories

import ru.GB.weathergb.domain.City
import ru.GB.weathergb.domain.Weather

fun interface APIWeather {
    fun getWeather(cityName: City, onComplete: (Weather?) -> Unit)
}