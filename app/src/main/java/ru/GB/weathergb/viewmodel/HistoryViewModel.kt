package ru.GB.weathergb.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.GB.weathergb.domain.Weather
import ru.GB.weathergb.model.room.WeatherHistory


class HistoryViewModel(
    private val historyLD: MutableLiveData<BasicAppState> = MutableLiveData<BasicAppState>()
) : ViewModel() {

    fun getLiveData() = historyLD

    var currentState: BasicAppState = BasicAppState.DefaultState

    fun fetch() {
        currentState = BasicAppState.LoadingState
        historyLD.postValue(currentState)
        WeatherHistory.fetchHistory { historyList -> parseRoomAnswer(historyList) }

    }

    fun error() {
        currentState = BasicAppState.ErrorState
        historyLD.postValue(currentState)
    }

    private fun success(historyList: List<Weather>) {
        currentState = BasicAppState.Success(historyList)
        historyLD.postValue(currentState)
    }

    private fun parseRoomAnswer(historyList: List<Weather>?) {
        if (historyList == null) {
            error()
        } else {
            success(historyList)
        }
    }


}