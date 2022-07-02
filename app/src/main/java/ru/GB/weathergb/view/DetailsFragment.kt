package ru.GB.weathergb.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import ru.GB.weathergb.databinding.FragmentDetailsBinding
import ru.GB.weathergb.domain.Weather
import ru.GB.weathergb.viewmodel.AppState
import ru.GB.weathergb.viewmodel.WeatherViewModel

class DetailsFragment : Fragment() {

    lateinit var weatherViewModel: WeatherViewModel
    private var _binding: FragmentDetailsBinding? = null
    private val binding: FragmentDetailsBinding
        get() {
            return _binding!!
        }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val BUNDLE_WEATHER_EXTRA = "WeatherBundle"
        fun newInstance(weather: Weather): DetailsFragment {
            val bundle = Bundle()
            bundle.putParcelable(BUNDLE_WEATHER_EXTRA, weather)
            val detailsFragment = DetailsFragment()
            detailsFragment.arguments = bundle
            return detailsFragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        weatherViewModel = ViewModelProvider(this).get(WeatherViewModel::class.java)
        weatherViewModel.getLiveData().observe(viewLifecycleOwner) {
            onChangeWeatherLiveData(it)
        }

        val weather = (arguments?.getParcelable<Weather>(BUNDLE_WEATHER_EXTRA))
        if (weather != null)
            renderData(weather)
        binding.buttonMoscow.setOnClickListener { weatherViewModel.fetch("Москва") }
    }

    private fun onChangeWeatherLiveData(state: AppState) {
        when (state) {
            is AppState.Success -> renderData(state.data)
        }
    }

    private fun renderData(weather: Weather) {
        binding.cityName.text = weather.city.name
        binding.temperatureValue.text = weather.temperature.toString()
        binding.feelsLikeValue.text = weather.feelsLike.toString()
        binding.cityCoordinates.text = "${weather.city.lat}/${weather.city.lon}"
    }


}