package ru.GB.weathergb.view.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.GB.weathergb.databinding.RecyclerItemHistoryBinding
import ru.GB.weathergb.domain.Weather

class HistoryAdapter(private val dataList: List<Weather>) :
    RecyclerView.Adapter<HistoryAdapter.Holder>() {

    inner class Holder(binding: RecyclerItemHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val cityNameTW = binding.itemCityName
        private val temperatureTW = binding.itemTemperature
        private val feelsLike = binding.itemFeelsLike

        fun bind(weather: Weather) {
            cityNameTW.text = weather.city.name
            temperatureTW.text = weather.temperature.toString()
            feelsLike.text = weather.feelsLike.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding =
            RecyclerItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(dataList[position])
    }

    override fun getItemCount(): Int {
        return dataList.size
    }
}