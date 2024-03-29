package ru.GB.weathergb.domain

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlin.random.Random

@Parcelize
class Weather(
    val city: City,
    val temperature: Int,
    val feelsLike: Int,
    val icon: String? = null
) : Parcelable

@Parcelize
data class City(
    val name: String,
    val lat: Double,
    val lon: Double
) : Parcelable {

    companion object {
        fun buildCity(cityName: String?): City {
            return if (cityName == "Москва") City("Москва", 55.75222, 37.61556)
            else City("no name", 1.0, 2.0)
        }
    }
}

private fun getDefaultCity() = City("Москва", 55.75222, 37.61556)

// как бы основная функция, которая получает название города, а возвращает погоду или Null
fun getWeatherDomain(CityName: String): Weather? {
    return if (CityName == "Москва") Weather(
        getDefaultCity(), Random.nextInt(10, 25),
        Random.nextInt(10, 25)
    )
    else null
}