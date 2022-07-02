package ru.GB.weathergb.model

import ru.GB.weathergb.viewmodel.WeatherViewModel

fun interface APIWeather {
    fun getWeather(cityName: String, weatherViewModel: WeatherViewModel)
}