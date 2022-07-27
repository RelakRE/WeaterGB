package ru.GB.weathergb.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.GB.weathergb.domain.Weather
import ru.GB.weathergb.model.room.WeatherHistory

sealed class AppStateHistoryList {
    object DefaultState : AppStateHistoryList()
    object LoadingState : AppStateHistoryList()
    class Success(val data: List<Weather>) : AppStateHistoryList()
    object ErrorState : AppStateHistoryList()
}

class HistoryViewModel(
    private val historyLD: MutableLiveData<AppStateHistoryList> = MutableLiveData<AppStateHistoryList>()
) : ViewModel() {

    fun getLiveData() = historyLD

    var currentState: AppStateHistoryList = AppStateHistoryList.DefaultState

    fun fetch() {
        currentState = AppStateHistoryList.LoadingState
        historyLD.postValue(currentState)
        WeatherHistory.fetchHistory { historyList -> parseRoomAnswer(historyList) }

    }

    fun error() {
        currentState = AppStateHistoryList.ErrorState
        historyLD.postValue(currentState)
    }

    private fun success(historyList: List<Weather>) {
        currentState = AppStateHistoryList.Success(historyList)
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