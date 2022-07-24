package ru.GB.weathergb

import android.app.Application
import ru.GB.weathergb.model.retrofit.WeatherRetrofit
import ru.GB.weathergb.model.sharedPreferences.WeatherSP

class Application : Application() {

    override fun onCreate() {
        super.onCreate()
        WeatherRetrofit.configureRetrofit()
        WeatherSP.configureSharedPreferences(this.applicationContext)
    }
}