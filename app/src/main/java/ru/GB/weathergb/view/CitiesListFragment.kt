package ru.GB.weathergb.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import ru.GB.weathergb.databinding.FragmentCityListBinding
import ru.GB.weathergb.databinding.FragmentDetailsBinding
import ru.GB.weathergb.domain.City
import ru.GB.weathergb.view.adapters.ItemActionRecycler
import ru.GB.weathergb.view.adapters.RecyclerCitiesAdapter
import ru.GB.weathergb.viewmodel.WeatherViewModel

class CitiesListFragment : Fragment() {

    private lateinit var weatherViewModel: WeatherViewModel
    private var _binding: FragmentCityListBinding? = null
    private val binding: FragmentCityListBinding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCityListBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val list = listOf<City>(City.buildCity("Москва"))
        binding.recyclerCities.apply {
            adapter = RecyclerCitiesAdapter(list,
                object : ItemActionRecycler {
                    override fun clickOnItem(cityName: String) {

                    }
                })
            layoutManager = LinearLayoutManager(activity)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}