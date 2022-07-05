package ru.GB.weathergb.model

import ru.GB.weathergb.domain.Weather
import ru.GB.weathergb.domain.getWeatherDomain

class WeatherRepo : APIWeather {
    override fun getWeather(cityName: String, onComplete: (Weather?) -> Unit) {
        onComplete(getWeatherDomain(cityName))
    }
}