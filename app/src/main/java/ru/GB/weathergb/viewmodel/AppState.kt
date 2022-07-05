package ru.GB.weathergb.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.GB.weathergb.domain.Weather
import ru.GB.weathergb.model.WeatherRepo
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

    fun fetch(cityName: String) {
        if (currentState !is DefaultState) refresh(cityName)
        currentState = LoadingState
        WeatherRepo().getWeather(cityName) { weather -> parseTheAnswer(weather) }
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

    fun refresh(cityName: String) {
        if (currentState !is ErrorState && currentState !is Success) return
        currentState = LoadingState
        WeatherRepo().getWeather(cityName) { weather -> parseTheAnswer(weather) }
        stateLiveData.value = currentState
    }

    private fun parseTheAnswer(weather: Weather?) {
        if (weather != null) success(weather)
        else error()
    }


}
