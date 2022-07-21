package ru.GB.weathergb.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.GB.weathergb.domain.City

class CitiesListViewModel(
    private val listLiveData: MutableLiveData<List<City>> = MutableLiveData<List<City>>()
) : ViewModel() {

    fun getLDList() = listLiveData

    fun addToEnd(city: City) {
        listLiveData.postValue(listLiveData.value?.plus(city) ?: listOf(city))
    }

    fun addListToEnd(list: List<City>) {
        listLiveData.postValue(listLiveData.value?.plus(list) ?: list)
    }
}