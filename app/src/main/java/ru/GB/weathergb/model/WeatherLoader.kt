package ru.GB.weathergb.model

import android.util.Log
import com.google.gson.Gson
import org.json.JSONException
import ru.GB.weathergb.BuildConfig
import ru.GB.weathergb.model.WeatherDTO.WeatherDTO
import ru.GB.weathergb.utils.getLines
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.MalformedURLException
import java.net.URL
import javax.net.ssl.HttpsURLConnection
import kotlin.reflect.typeOf

object WeatherLoader {

    const val WEATHER_API_KEY = "X-Yandex-API-Key"

    fun requestWeather(lat: Double, lon: Double, onResponse: (WeatherDTO?) -> Unit) {
        val uri = URL("https://api.weather.yandex.ru/v2/informers?lat=${lat}&lon=${lon}")

        val myConnection = uri.openConnection() as HttpsURLConnection
        myConnection.readTimeout = 5000
        myConnection.addRequestProperty(WEATHER_API_KEY, BuildConfig.WEATHER_API_KEY)
        Thread {
            try {
                val reader = BufferedReader(InputStreamReader(myConnection.inputStream))
                val weatherDTO = Gson().fromJson(getLines(reader), WeatherDTO::class.java)
                onResponse(weatherDTO)
            } catch (e: JSONException) {
                Log.e("requestWeather Error JSONException", e.message.toString())
                onResponse(null)
            } catch (e: java.io.IOException){
                Log.e("requestWeather Error IOException", e.message.toString())
                onResponse(null)
            }catch (e: MalformedURLException){
                Log.e("requestWeather Error MalformedURLException", e.message.toString())
                onResponse(null)
            }catch (e: Exception){
                Log.e("requestWeather Error Exception", e.message.toString()
                        + " Необработан тип ошибки ${e::class.qualifiedName}")

                onResponse(null)
            }
            finally {
                myConnection.disconnect()
            }

        }.start()
    }

    fun requestWeatherBroadcast(){

    }

}

