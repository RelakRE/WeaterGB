package ru.GB.weathergb.model.retrofit

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query
import ru.GB.weathergb.model.WeatherDTO.WeatherDTO
import ru.GB.weathergb.utils.WEATHER_API_KEY

interface WeatherAPI {

    @GET("v2/informers")
    fun getWeather(
        @Header(WEATHER_API_KEY) token: String,
        @Query("lat") lat: Double,
        @Query("lon") lon: Double
    ): Call<WeatherDTO>

}