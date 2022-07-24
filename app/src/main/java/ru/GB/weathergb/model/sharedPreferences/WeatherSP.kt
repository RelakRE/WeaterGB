package ru.GB.weathergb.model.sharedPreferences

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import ru.GB.weathergb.domain.City
import ru.GB.weathergb.domain.Weather
import ru.GB.weathergb.utils.LAST_WEATHER_SHARED_PREFERENCES

const val CITY_NAME = "CITY_NAME"
const val CITY_COORDINATES = "CITY_COORDINATES"
const val FACT_TEMP = "FACT_TEMP"
const val FEELS_LIKE = "FEELS_LIKE"
const val ICON = "ICON"

object WeatherSP {

    private lateinit var lastWeatherSP: SharedPreferences

    fun refreshLastWeather(weather: Weather) {
        lastWeatherSP?.edit()?.apply {
            putString(CITY_NAME, weather.city.name)
            putString(CITY_COORDINATES, "${weather.city.lat}/${weather.city.lon}")
            putInt(FACT_TEMP, weather.temperature)
            putInt(FEELS_LIKE, weather.feelsLike)
            putString(ICON, weather.icon)
            apply()
        }
    }

    fun getLastWeather(weatherSP: SharedPreferences? = lastWeatherSP): Weather? {
        return weatherSP?.let {
            Weather(
                City.buildCity(it.getString(CITY_NAME, "")),
                it.getInt(FACT_TEMP, 0),
                it.getInt(FEELS_LIKE, 0),
                it.getString(ICON, null)
            )
        }
    }

    fun configureSharedPreferences(applicationContext: Context) {
        lastWeatherSP =
            applicationContext.getSharedPreferences(LAST_WEATHER_SHARED_PREFERENCES, MODE_PRIVATE)
    }

    fun haveTheLastWeatherSP(): Boolean {
        return lastWeatherSP.getString(CITY_NAME, "") != ""
    }
}