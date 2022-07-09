package ru.GB.weathergb.model

import android.util.Log
import com.google.gson.Gson
import ru.GB.weathergb.BuildConfig
import ru.GB.weathergb.model.WeatherDTO.WeatherDTO
import ru.GB.weathergb.utils.getLines
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import javax.net.ssl.HttpsURLConnection

object WeatherLoader {

    fun requestWeather(lat: Double, lon: Double, onResponse: (WeatherDTO?) -> Unit) {
        val uri = URL("https://api.weather.yandex.ru/v2/informers?lat=${lat}&lon=${lon}")

        val myConnection = uri.openConnection() as HttpsURLConnection
        myConnection.readTimeout = 5000
        myConnection.addRequestProperty("X-Yandex-API-Key", BuildConfig.WEATHER_API_KEY)
        Thread {
            try {
                val reader = BufferedReader(InputStreamReader(myConnection.inputStream))
                val weatherDTO = Gson().fromJson(getLines(reader), WeatherDTO::class.java)
                onResponse(weatherDTO)
            } catch (e: Exception) {
                Log.e("requestWeather Error", e.message.toString())
                onResponse(null)
            } finally {
                myConnection.disconnect()
            }

        }.start()
    }
}

