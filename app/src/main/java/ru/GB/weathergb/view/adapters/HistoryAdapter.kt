package ru.GB.weathergb.view.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.GB.weathergb.databinding.RecyclerItemHistoryBinding
import ru.GB.weathergb.model.room.WeatherHistory

class HistoryAdapter : RecyclerView.Adapter<HistoryAdapter.Holder>() {

    inner class Holder(private val binding: RecyclerItemHistoryBinding) :
        RecyclerView.ViewHolder(binding.root){
            private val cityNameTW = binding.itemCityName
            private val temperatureTW = binding.itemTemperature
            private val
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {

    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        WeatherHistory.historyDao.
    }
}