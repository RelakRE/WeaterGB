package ru.GB.weathergb

import android.Manifest.permission.CALL_PHONE
import android.Manifest.permission.READ_CONTACTS
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import ru.GB.weathergb.databinding.ActivityMainBinding
import ru.GB.weathergb.domain.Weather
import ru.GB.weathergb.model.room.HistoryEntity
import ru.GB.weathergb.model.room.WeatherHistory
import ru.GB.weathergb.model.sharedPreferences.WeatherSP
import ru.GB.weathergb.utils.Permissions
import ru.GB.weathergb.view.fragments.CitiesListFragment
import ru.GB.weathergb.view.fragments.DetailsFragment

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private val REQUEST_CODE = 42


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        if (WeatherSP.haveTheLastWeatherSP()) goToDetailsFragment() else goToCitiesListFragment()
    }

    override fun onStart() {
        super.onStart()
        Permissions.checkPermission(
            arrayOf(READ_CONTACTS, CALL_PHONE),
            this
        )
    }

    private fun goToDetailsFragment() {
        WeatherSP.getLastWeather()?.also { testAddToHistory(it) }
        supportFragmentManager.commit {
            add(
                R.id.container,
                DetailsFragment.newInstance(WeatherSP.getLastWeather())
            )
        }
    }

    private fun goToCitiesListFragment() {
        supportFragmentManager.commit {
            add(
                R.id.container,
                CitiesListFragment()
            )
        }
    }

    private fun testAddToHistory(weather: Weather) {
        Thread {
            WeatherHistory.historyDao.insert(weather.toEntity())
        }.start()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE) {
            if (grantResults.isNotEmpty() && !grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                AlertDialog.Builder(this)
                    .setTitle("Контакты не получены")
                    .setMessage("Страница с контактами будет пустая.")
                    .setNegativeButton("Закрыть") { dialog, _ -> dialog.dismiss() }
                    .create()
                    .show()
            }
        } else return
    }
}

private fun Weather.toEntity(): HistoryEntity =
    HistoryEntity(0, this.city.name, this.temperature, this.feelsLike, this.icon)
