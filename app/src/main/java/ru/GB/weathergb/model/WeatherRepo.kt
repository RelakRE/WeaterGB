package ru.GB.weathergb.model

import ru.GB.weathergb.domain.getWeatherDomain
import ru.GB.weathergb.viewmodel.WeatherViewModel

class WeatherRepo : APIWeather {
    override fun getWeather(cityName: String, weatherViewModel: WeatherViewModel) {
        weatherViewModel.success(getWeatherDomain(cityName))
    }
}