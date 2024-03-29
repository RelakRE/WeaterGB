package ru.GB.weathergb.view.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_details.*
import ru.GB.weathergb.R
import ru.GB.weathergb.databinding.FragmentDetailsBinding
import ru.GB.weathergb.domain.City
import ru.GB.weathergb.domain.Weather
import ru.GB.weathergb.model.repositories.WeatherRepo
import ru.GB.weathergb.utils.Permissions
import ru.GB.weathergb.viewmodel.AppState
import ru.GB.weathergb.viewmodel.WeatherViewModel

class DetailsFragment : Fragment() {

    private val weatherViewModel: WeatherViewModel by viewModels()
    private var _binding: FragmentDetailsBinding? = null
    private val binding: FragmentDetailsBinding
        get() = _binding!!

    private var uploadReceiver: BroadcastReceiver? = null

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        uploadReceiver?.also { view?.context?.unregisterReceiver(it) }
    }

    companion object {

        const val WEATHER_INTENT_KEY = "WEATHER_INTENT_KEY"
        const val WEATHER_INTENT_NAME = "WEATHER_INTENT_NAME"
        const val REPO_TYPE = ""

        const val BUNDLE_WEATHER_EXTRA = "WeatherBundle"
        const val BUNDLE_CITY_EXTRA = "CityBundle"

        fun newInstance(weather: Weather?): DetailsFragment {
            val bundle = Bundle()
            bundle.putParcelable(BUNDLE_WEATHER_EXTRA, weather)
            val detailsFragment = DetailsFragment()
            detailsFragment.arguments = bundle
            return detailsFragment
        }

        fun newInstance(city: City): DetailsFragment {
            val bundle = Bundle()
            bundle.putParcelable(BUNDLE_CITY_EXTRA, city)
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
        configToolbar()
        return binding.root
    }

    private fun configToolbar() {
        binding.toolbarDetails.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.show_list -> goToListFragment()
                R.id.show_history -> goToHistoryFragment()
                R.id.show_contacts -> goToContacts()
                R.id.show_map -> goToMap()
            }
            return@setOnMenuItemClickListener true
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeViewModel()
        bindButtons()

        val bundleWeather = arguments?.getParcelable<Weather>(BUNDLE_WEATHER_EXTRA)
        if (bundleWeather != null) {
            renderData(bundleWeather)
        } else {
            arguments?.getParcelable<City>(BUNDLE_CITY_EXTRA)
                ?.also { renderData(it) }
        }
    }

    private fun goToListFragment() {
        replaceFragmentWith(CitiesListFragment())
    }

    private fun goToHistoryFragment() {
        replaceFragmentWith(HistoryFragment())
    }

    private fun goToMap() {

        if (Permissions.permissionReceived(
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                requireContext()
            )
        ) {
            replaceFragmentWith(GeolocationFragment())
        } else {
            Permissions.requestPermission(
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                requireActivity()
            )
        }
    }

    private fun goToContacts() {
        replaceFragmentWith(ContactsFragment())
    }

    private fun initializeViewModel() {
        weatherViewModel.getLiveData().observe(viewLifecycleOwner) {
            onChangeWeatherLiveData(it)
        }
    }

    private fun bindButtons() {
        with(binding)
        {
            showCityOnMap.setOnClickListener {
                weatherViewModel.currentCity?.also { city ->
                    showLocation(city.lon, city.lat)
                }
            }
        }
    }

    private fun showLocation(lon: Double, lat: Double) {

        if (Permissions.permissionReceived(
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                requireContext()
            )
        ) {
            val mapBuilder = MapsFragment.Builder()
            mapBuilder.setLocation(lon, lat)

            replaceFragmentWith(
                mapBuilder.build()
            )
        } else {
            Permissions.requestPermission(
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                requireActivity()
            )
        }
    }

    private fun replaceFragmentWith(fragment: Fragment) {
        requireActivity().supportFragmentManager.commit {
            replace(R.id.container, fragment)
                .addToBackStack(null)
        }
    }

    private fun onChangeWeatherLiveData(state: AppState) {

        binding.loading.isVisible = state == AppState.LoadingState

        when (state) {
            is AppState.Success -> renderData(state.data)
            is AppState.ErrorState -> requireView().showText("Поломка")
            else -> requireView().showText("Поломка")
        }
    }

    private fun renderData(weather: Weather) {
        with(binding) {
            cityName.text = weather.city.name
            temperatureValue.text = weather.temperature.toString()
            feelsLikeValue.text = weather.feelsLike.toString()
            cityCoordinates.text = "${weather.city.lat}/${weather.city.lon}"
        }
        weather.icon?.also {
            GlideToVectorYou.justLoadImage(
                requireActivity(),
                Uri.parse("https://yastatic.net/weather/i/icons/blueye/color/svg/${it}.svg"),
                weather_icon
            )
        }

        weatherViewModel.currentCity = weather.city

    }

    private fun renderData(city: City) {

        with(binding) {
            cityName.text = city.name
            cityCoordinates.text = "${city.lat}/${city.lon}"
        }

        when (REPO_TYPE) {
            "BRO" -> return
            else -> uploadWeather(city)
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


    private fun uploadWeather(city: City) {
        weatherViewModel.fetch(city)
    }

    //endregion

    //region BroadcastReceiver

    private fun BroadcastReceiverRepoImpl(city: City) {
        regReceiver()
        WeatherRepo().getWeather(city) { weather ->
            requireActivity().sendBroadcast(
                Intent(WEATHER_INTENT_NAME).putWeather(weather)
            )
        }
    }

    private fun regReceiver() {
        uploadReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                intent?.getParcelableExtra<Weather>(WEATHER_INTENT_KEY)?.also {
                    parseTheAnswer(it)
                }
            }
        }
        requireContext().registerReceiver(
            uploadReceiver,
            IntentFilter(WEATHER_INTENT_NAME)
        )
    }

    private fun parseTheAnswer(weather: Weather?) {
        if (weather != null) renderData(weather)
    }

    private fun Intent.putWeather(weather: Weather?) = this.putExtra(WEATHER_INTENT_KEY, weather)

    //endregion

}