package ru.GB.weathergb.model.repositories

import ru.GB.weathergb.domain.City
import ru.GB.weathergb.domain.Weather
import ru.GB.weathergb.model.WeatherLoader.requestWeather

class WeatherRepo : APIWeather {

    override fun getWeather(city: City, onComplete: (Weather?) -> Unit) {
        requestWeather(city.lat, city.lon) {
            onComplete(
                if (it == null) null else Weather(
                    city,
                    it.fact.temp,
                    it.fact.feelsLike,
                    it.fact.icon
                )
            )
        }
    }
}
