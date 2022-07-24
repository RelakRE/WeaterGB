package ru.GB.weathergb.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.GB.weathergb.domain.City
import ru.GB.weathergb.domain.Weather
import ru.GB.weathergb.model.repositories.WeatherRepo
import ru.GB.weathergb.model.sharedPreferences.WeatherSP
import ru.GB.weathergb.viewmodel.AppState.*

sealed class AppState {
    object DefaultState : AppState()
    object LoadingState : AppState()
    class Success(val data: Weather) : AppState()
    object ErrorState : AppState()
}

class WeatherViewModel(
    private val stateLiveData: MutableLiveData<AppState> = MutableLiveData<AppState>()
) : ViewModel() {

    var currentState: AppState = DefaultState
        private set

    fun getLiveData() = stateLiveData

    fun fetch(city: City) {
        if (currentState !is DefaultState) {
            refresh(city); return
        }
        currentState = LoadingState
        WeatherRepo().getWeather(city) { weather -> parseTheAnswer(weather) }

        stateLiveData.value = currentState
    }

    fun success(data: Weather) {
        if (currentState !is LoadingState) return
        currentState = Success(data)
        stateLiveData.postValue(currentState)
    }

    fun error() {
        if (currentState !is LoadingState) return
        currentState = ErrorState
        stateLiveData.postValue(currentState)
    }

    fun refresh(city: City) {
        if (currentState !is ErrorState && currentState !is Success) return
        currentState = LoadingState
        WeatherRepo().getWeather(city) { weather -> parseTheAnswer(weather) }
        stateLiveData.value = currentState
    }

    private fun parseTheAnswer(weather: Weather?) {
        if (weather != null) {
            success(weather)
            WeatherSP.refreshLastWeather(weather)
        }
        else error()
    }
}
