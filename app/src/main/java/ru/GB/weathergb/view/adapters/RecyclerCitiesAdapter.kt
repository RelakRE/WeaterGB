package ru.GB.weathergb.view.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.GB.weathergb.databinding.RecyckerItemCityBinding
import ru.GB.weathergb.domain.City

class RecyclerCitiesAdapter(
    val dataList: MutableList<City>,
    private val callbacks: ItemActionRecycler
) : RecyclerView.Adapter<RecyclerCitiesAdapter.Holder>() {

    inner class Holder(private val binding: RecyckerItemCityBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val twCity = binding.itemCityName

        fun bind(city: City) {
            twCity.text = city.name
            binding.root.setOnClickListener { callbacks.clickOnItem(city) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {

        val binding =
            RecyckerItemCityBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(dataList[position])
    }

    override fun getItemCount(): Int {
        return dataList.size
    }
}