package ru.GB.weathergb.model

import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.*
import okio.IOException
import org.json.JSONException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import ru.GB.weathergb.BuildConfig
import ru.GB.weathergb.model.WeatherDTO.WeatherDTO
import ru.GB.weathergb.model.retrofit.WeatherAPI
import ru.GB.weathergb.model.retrofit.WeatherRetrofit
import ru.GB.weathergb.utils.getLines
import java.net.MalformedURLException
import java.net.URL
import javax.net.ssl.HttpsURLConnection

object WeatherLoader {

    private const val WEATHER_API_KEY = "X-Yandex-API-Key"
    private const val BASE_URL = "https://api.weather.yandex.ru/v2/informers"
    private const val REQUEST_TYPE = "Retrofit"

    fun requestWeather(lat: Double, lon: Double, onResponse: (WeatherDTO?) -> Unit) {

        when (REQUEST_TYPE) {
            "HttpsURLConnection" -> httpsURLConnectionRequest(lat, lon, onResponse)
            "OkHttp" -> okHttpRequest(lat, lon, onResponse)
            "Retrofit" -> retrofitRequest(lat, lon, onResponse)
            else -> Log.e("error request type", "don't selected type.")
        }
    }

    private fun retrofitRequest(lat: Double, lon: Double, onResponse: (WeatherDTO?) -> Unit) {

//        val weatherAPI = Retrofit.Builder()
//            .baseUrl("https://api.weather.yandex.ru/")
//            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
//            .build().create(WeatherAPI::class.java)

        WeatherRetrofit.weatherAPI
            .getWeather(BuildConfig.WEATHER_API_KEY, lat, lon)
            .enqueue(object : retrofit2.Callback<WeatherDTO> {
                override fun onResponse(
                    call: retrofit2.Call<WeatherDTO>,
                    response: retrofit2.Response<WeatherDTO>
                ) {
                    val serverResponse = response.body()
                    if (response.isSuccessful && serverResponse != null) {
                        onResponse(serverResponse)
                    } else onResponse(null)

                }

                override fun onFailure(call: retrofit2.Call<WeatherDTO>, t: Throwable) {
                    onResponse(null)
                }

            })

    }

    private fun okHttpRequest(lat: Double, lon: Double, onResponse: (WeatherDTO?) -> Unit) {
        val client = OkHttpClient()
        val builder = Request.Builder()

        builder.header(WEATHER_API_KEY, BuildConfig.WEATHER_API_KEY)
        builder.url(BASE_URL + "?lat=${lat}&lon=${lon}")

        val request = builder.build()
        val cal = client.newCall(request)

        cal.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                onResponse(null)
            }

            override fun onResponse(call: Call, response: Response) {
                val serverResponse = response.body.string()
                if (response.isSuccessful && serverResponse.isNotEmpty()) {
                    onResponse(Gson().fromJson(serverResponse, WeatherDTO::class.java))
                } else onResponse(null)
            }
        })
    }

    private fun httpsURLConnectionRequest(
        lat: Double,
        lon: Double,
        onResponse: (WeatherDTO?) -> Unit
    ) {
        val uri = URL(BASE_URL + "?lat=${lat}&lon=${lon}")
        var myConnection: HttpsURLConnection? = null

        Thread {
            try {
                myConnection = uri.openConnection() as HttpsURLConnection
                myConnection!!.readTimeout = 5000
                myConnection!!.addRequestProperty(WEATHER_API_KEY, BuildConfig.WEATHER_API_KEY)

                val reader = myConnection!!.inputStream.bufferedReader()
                val weatherDTO = Gson().fromJson(getLines(reader), WeatherDTO::class.java)
                onResponse(weatherDTO)
            } catch (e: JSONException) {
                Log.e("requestWeather Error JSONException", e.message.toString())
                onResponse(null)
            } catch (e: java.io.IOException) {
                Log.e("requestWeather Error IOException", e.message.toString())
                onResponse(null)
            } catch (e: MalformedURLException) {
                Log.e("requestWeather Error MalformedURLException", e.message.toString())
                onResponse(null)
            } catch (e: Exception) {
                Log.e(
                    "requestWeather Error Exception", e.message.toString()
                            + " Необработан тип ошибки ${e::class.qualifiedName}"
                )
                onResponse(null)
            } finally {
                myConnection?.disconnect()
            }
        }.start()
    }
}




