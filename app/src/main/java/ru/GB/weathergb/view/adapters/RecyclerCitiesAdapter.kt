package ru.GB.weathergb.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.GB.weathergb.databinding.RecyckerItemCityBinding
import ru.GB.weathergb.domain.City

class RecyclerCitiesAdapter(
    private val dataList: List<City>,
    private val callbacks: ItemActionRecycler
) : RecyclerView.Adapter<RecyclerCitiesAdapter.Holder>() {

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val binding = RecyckerItemCityBinding.bind(itemView)
        private val twCity = binding.itemCityName

        fun bind(cityName: String) {
            twCity.text = cityName
            binding.root.setOnClickListener {callbacks.clickOnItem(cityName)}
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {

        val binding = RecyckerItemCityBinding.inflate(LayoutInflater.from(parent.context))
        return Holder(binding.root)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(dataList[position].name)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }
}