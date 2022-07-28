package ru.GB.weathergb

import android.Manifest
import android.Manifest.permission.READ_CONTACTS
import android.content.*
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.ConnectivityManager.CONNECTIVITY_ACTION
import android.os.Bundle
import android.widget.Toast
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
    lateinit var receiver: BroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        if (WeatherSP.haveTheLastWeatherSP()) goToDetailsFragment() else goToCitiesListFragment()

        initBroadcast()
    }

    private fun goToDetailsFragment() {
        testAddToHistory(WeatherSP.getLastWeather()!!)
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

    private fun initBroadcast() {
//        TODO() удалить после сдачи дз
        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val noConnectivity =
                    intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false)
                if (noConnectivity) {
                    onConnectionLost()
                } else {
                    onConnectionFound()
                }
            }
        }
        registerReceiver(receiver, IntentFilter(CONNECTIVITY_ACTION))
    }

    fun onConnectionLost() {
        Toast.makeText(this, "Connection lost", Toast.LENGTH_LONG).show()
    }

    fun onConnectionFound() {
        Toast.makeText(this, "Connection found", Toast.LENGTH_LONG).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }

    private fun testAddToHistory(weather: Weather) {
        Thread {
            WeatherHistory.historyDao.insert(weather.toEntity())
        }.start()
    }

    private fun checkPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED -> {
                Snackbar.make(
                    this,
                    binding.root,
                    "Доступ к контактам на телефоне есть",
                    Snackbar.LENGTH_LONG
                ).show()
                //Доступ к контактам на телефоне есть
            }

            shouldShowRequestPermissionRationale(READ_CONTACTS) -> {
                AlertDialog.Builder(this)
                    .setTitle("Доступ к контактам")
                    .setMessage("Контакты нужны для ...")
                    .setPositiveButton(
                        "Разрешить"
                    ) { _, _ ->
                        Snackbar.make(
                            this,
                            binding.root,
                            "Доступ дапли",
                            Snackbar.LENGTH_LONG
                        ).show()
                        requestPermission()
                    }
                    .setNegativeButton("Нет"){
                        _,_ ->
                    }
            }
        }
    }
    private fun requestPermission() {
        requestPermissions(this, arrayOf(READ_CONTACTS), 42)
    }
}

private fun Weather.toEntity(): HistoryEntity =
    HistoryEntity(0, this.city.name, this.temperature, this.feelsLike, this.icon)
