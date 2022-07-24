package ru.GB.weathergb

import android.content.*
import android.net.ConnectivityManager
import android.net.ConnectivityManager.CONNECTIVITY_ACTION
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.fragment.app.commit
import ru.GB.weathergb.databinding.ActivityMainBinding
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

        addMenuProvider(object : MenuProvider{
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_details, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return true
            }
        })

        if (WeatherSP.haveTheLastWeatherSP()) goToDetailsFragment () else goToCitiesListFragment()

        initBroadcast()
    }

    private fun goToDetailsFragment() {
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
}