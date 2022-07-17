package ru.GB.weathergb.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import ru.GB.weathergb.R
import ru.GB.weathergb.databinding.FragmentDetailsBinding
import ru.GB.weathergb.domain.Weather
import ru.GB.weathergb.viewmodel.AppState
import ru.GB.weathergb.viewmodel.WeatherViewModel

class DetailsFragment : Fragment() {

    private lateinit var weatherViewModel: WeatherViewModel
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

        arguments?.getParcelable<Weather>(BUNDLE_WEATHER_EXTRA)
            ?.also { renderData(it) } //also { ::renderData }

        initializeViewModel()
        bindButtons()

    }

    private fun initializeViewModel() {
        weatherViewModel = ViewModelProvider(this).get(WeatherViewModel::class.java)
        weatherViewModel.getLiveData().observe(viewLifecycleOwner) {
            onChangeWeatherLiveData(it)
        }
    }

    private fun bindButtons() {
        with(binding)
        {
            buttonMoscow.setOnClickListener { weatherViewModel.fetch("Москва") }
            buttonSpiderMan.setOnClickListener {
                replaceFragmentWith(DetailsFilmFragment())
            }
        }
    }

    private fun replaceFragmentWith(fragment: Fragment) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun onChangeWeatherLiveData(state: AppState) {

        binding.loading.visibility = if (state == AppState.LoadingState) View.VISIBLE
        else View.INVISIBLE

        when (state) {
            is AppState.Success -> renderData(state.data)
            is AppState.ErrorState -> requireView().showText("Поломка")
        }
    }

    private fun renderData(weather: Weather) {
        with(binding) {
            cityName.text = weather.city.name
            temperatureValue.text = weather.temperature.toString()
            feelsLikeValue.text = weather.feelsLike.toString()
            cityCoordinates.text = "${weather.city.lat}/${weather.city.lon}"
        }
    }

    //region extensions

    private fun View.showText(text: String, vararg actions: View.OnClickListener?) {
        Snackbar.make(
            this,
            text,
            Snackbar.LENGTH_LONG
        ).also { snack ->
            actions.forEach { snack.setAction("тест", it) }
            snack.show()
        }
    }

    private fun View.showText(text: String, pair: Pair<String, View.OnClickListener>) {
        Snackbar.make(
            this,
            text,
            Snackbar.LENGTH_LONG
        ).setAction(pair.first, pair.second).show()
    }

    //endregion

}