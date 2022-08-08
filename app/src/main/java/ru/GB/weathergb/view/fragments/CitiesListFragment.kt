package ru.GB.weathergb.view.fragments

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.recyclerview.widget.LinearLayoutManager
import ru.GB.weathergb.R
import ru.GB.weathergb.databinding.FragmentCityListBinding
import ru.GB.weathergb.domain.City
import ru.GB.weathergb.view.adapters.ItemActionRecycler
import ru.GB.weathergb.view.adapters.RecyclerCitiesAdapter

class CitiesListFragment : Fragment() {

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
            adapter = RecyclerCitiesAdapter(list.toMutableList(),
                object : ItemActionRecycler {
                    override fun clickOnItem(city: City) {
                        openCityWeather(city)
                    }
                })
            layoutManager = LinearLayoutManager(activity)
        }
        binding.fabAddMyLocation.setOnClickListener {
            addCurrentLocation()
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

    private fun addCurrentLocation() {
        getLocation()
    }


    fun addCity(city: City) {
        Handler(Looper.getMainLooper()).post {
            (binding.recyclerCities.adapter as RecyclerCitiesAdapter).apply {
                dataList.add(city)
                notifyItemInserted(dataList.size - 1)
            }
        }
//
//        binding.recyclerCities.addView(
//            layoutInflater.inflate(
//                R.layout.recycker_item_city,
//                binding.recyclerCities
//            )
//        )
//        binding.recyclerCities.adapter?.notifyDataSetChanged()
    }

    private fun getLocation() {
        requireActivity().let { context ->
            if (checkPermissions()) {
                val locationManager =
                    context.getSystemService(Context.LOCATION_SERVICE) as
                            LocationManager
                if (isLocationEnabled(locationManager)) {
                    val provider =
                        locationManager.getProvider(LocationManager.GPS_PROVIDER)
                    provider?.let {

                        locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)?.let {
                            getAddressAsync(context, it)
                        }
                    }
                } else {
                    Toast.makeText(requireContext(), "Включите GPS", Toast.LENGTH_LONG).show()
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivity(intent)
                }
            }
        }
    }


    private fun checkPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun isLocationEnabled(locationManager: LocationManager): Boolean {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }


    private fun getAddressAsync(
        context: Context,
        location: Location
    ) {
        val geoCoder = Geocoder(context)
        Thread {
            try {
                val addresses = geoCoder.getFromLocation(
                    location.latitude,
                    location.longitude,
                    1
                )
                if (addresses.isNotEmpty()) {
                    addCity(
                        City(
                            addresses[0].locality,
                            location.latitude,
                            location.longitude
                        )
                    )
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }


}
