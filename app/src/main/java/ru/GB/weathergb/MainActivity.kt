package ru.GB.weathergb

import android.Manifest.permission.READ_CONTACTS
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat
import androidx.fragment.app.commit
import com.google.android.material.snackbar.Snackbar
import ru.GB.weathergb.databinding.ActivityMainBinding
import ru.GB.weathergb.domain.Weather
import ru.GB.weathergb.model.room.HistoryEntity
import ru.GB.weathergb.model.room.WeatherHistory
import ru.GB.weathergb.model.sharedPreferences.WeatherSP
import ru.GB.weathergb.view.fragments.CitiesListFragment
import ru.GB.weathergb.view.fragments.DetailsFragment

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private val REQUEST_CODE = 42


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

//        if (WeatherSP.haveTheLastWeatherSP()) goToDetailsFragment() else goToCitiesListFragment()
    }

    override fun onStart() {
        super.onStart()
        checkPermission()
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

//    region Permissions

    private fun checkPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED -> {
                showSnack("Доступ к контактам на телефоне есть")
                getContacts()
            }

            ActivityCompat.shouldShowRequestPermissionRationale(this, READ_CONTACTS) -> {
                AlertDialog.Builder(this)
                    .setTitle("Доступ к контактам")
                    .setMessage("Контакты нужны для ...")
                    .setPositiveButton(
                        "Разрешить"
                    ) { _, _ ->
                        showSnack("Доступ дапли")
                        requestPermission()
                    }
                    .setNegativeButton("Нет") { dialog, _ ->
                        showSnack("Доступ не хотят давать")
                        dialog.dismiss()
                    }.create().show()
            }

            else -> requestPermission()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults.first() == PackageManager.PERMISSION_GRANTED) {
                getContacts()
            } else {
                AlertDialog.Builder(this)
                    .setTitle("Доступ к контактам")
                    .setMessage("Контакты не получены")
                    .setNegativeButton("Закрыть") { dialog, _ -> dialog.dismiss() }
                    .create()
                    .show()
            }
        } else return
    }

    private fun requestPermission() {
        requestPermissions(this, arrayOf(READ_CONTACTS), REQUEST_CODE)
    }

//    endregion

    private fun showSnack(text: String) {
        Snackbar.make(this, binding.root, text, Snackbar.LENGTH_LONG).show()
    }
}

private fun Weather.toEntity(): HistoryEntity =
    HistoryEntity(0, this.city.name, this.temperature, this.feelsLike, this.icon)
