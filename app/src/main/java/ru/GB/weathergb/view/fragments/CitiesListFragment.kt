package ru.GB.weathergb.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import ru.GB.weathergb.R
import ru.GB.weathergb.databinding.FragmentCityListBinding
import ru.GB.weathergb.domain.City
import ru.GB.weathergb.view.adapters.ItemActionRecycler
import ru.GB.weathergb.view.adapters.RecyclerCitiesAdapter
import ru.GB.weathergb.viewmodel.WeatherViewModel

class CitiesListFragment : Fragment() {

    //    private lateinit var weatherViewModel: WeatherViewModel
    private val weatherViewModel: WeatherViewModel by viewModels()
    private var _binding: FragmentCityListBinding? = null
    private val binding: FragmentCityListBinding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCityListBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val list = getCitiesList()
        binding.recyclerCities.apply {
            adapter = RecyclerCitiesAdapter(list,
                object : ItemActionRecycler {
                    override fun clickOnItem(city: City) {
                        openCityWeather(city)
                    }
                })
            layoutManager = LinearLayoutManager(activity)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getCitiesList(): List<City> {
        return listOf(City.buildCity("Москва"))
    }

    fun openCityWeather(city: City) {
        requireActivity().supportFragmentManager.commit {
            replace(
                R.id.container,
                DetailsFragment.newInstance(city)
            )
            addToBackStack(null)
        }
    }

}
