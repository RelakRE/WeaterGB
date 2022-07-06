package ru.GB.weathergb.model

import ru.GB.weathergb.domain.City
import ru.GB.weathergb.domain.Weather
import ru.GB.weathergb.domain.getWeatherDomain
import ru.GB.weathergb.model.WeatherDTO.WeatherDTO
import ru.GB.weathergb.model.WeatherLoader.requestWeather

class WeatherRepo : APIWeather {

    override fun getWeather(city: City, onComplete: (Weather?) -> Unit) {
        requestWeather(city.lat, city.lon){
            onComplete(Weather(city, it.fact.temp, it.fact.feelsLike))
        }
    }
}
