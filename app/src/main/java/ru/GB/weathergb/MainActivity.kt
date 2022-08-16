package ru.GB.weathergb

import android.Manifest
import android.Manifest.permission.CALL_PHONE
import android.Manifest.permission.READ_CONTACTS
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import ru.GB.weathergb.databinding.ActivityMainBinding
import ru.GB.weathergb.domain.Weather
import ru.GB.weathergb.model.contacts.Contacts
import ru.GB.weathergb.model.room.HistoryEntity
import ru.GB.weathergb.model.room.WeatherHistory
import ru.GB.weathergb.model.sharedPreferences.WeatherSP
import ru.GB.weathergb.utils.Permissions
import ru.GB.weathergb.view.fragments.CitiesListFragment
import ru.GB.weathergb.view.fragments.ContactsFragment
import ru.GB.weathergb.view.fragments.DetailsFragment

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private val REQUEST_CODE = 42
    val REQUIRED_PERMISSIONS =
        arrayOf(READ_CONTACTS, CALL_PHONE, Manifest.permission.ACCESS_FINE_LOCATION)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        if (WeatherSP.haveTheLastWeatherSP()) goToDetailsFragment() else goToCitiesListFragment()

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

        val currentFrag =
            supportFragmentManager.findFragmentById(R.id.container) as Fragment

        if (currentFrag is ContactsFragment) {

            val contactsImpl = Contacts(this, this)

            permissions.indexOf(READ_CONTACTS).also {
                if (it >= 0 && grantResults[it] == PackageManager.PERMISSION_DENIED) {
                    showAlertDialog(
                        "Не получены разрешения на контакты.",
                        "Зайдите в настройки приложения и добавьте разрешения."
                    )
                    return
                }
            }

            permissions.indexOf(CALL_PHONE).also {
                if (it >= 0 && grantResults[it] == PackageManager.PERMISSION_DENIED) {
                    showAlertDialog(
                        "Не получены разрешения на контакты.",
                        "Зайдите в настройки приложения и добавьте разрешения."
                    )
                    return
                }
            }

            currentFrag.addContacts(
                if (Permissions.permissionReceived(contactsImpl.requiredPermissions, this)
                ) contactsImpl.queryContacts()
                else emptyMap()
            )
        }

        if (grantResults.isNotEmpty() && !grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {

            permissions.indexOf(Manifest.permission.ACCESS_FINE_LOCATION).also {
                if (it >= 0 && grantResults[it] == PackageManager.PERMISSION_DENIED) {
                    showAlertDialog(
                        "Разрешения геолокации",
                        "Зайдите в настройки приложения и добавьте разрешения."
                    )
                    return
                }
            }
        }
    }

    private fun showAlertDialog(title: String, message: String) {
        this.let {
            AlertDialog.Builder(it)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton(getString(R.string.dialog_button_close)) { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
                .show()
        }
    }

    private fun logToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("@@@", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }
            val token = task.result
            Log.d("@@@", token)
        })
    }

    private fun Weather.toEntity(): HistoryEntity =
        HistoryEntity(0, this.city.name, this.temperature, this.feelsLike, this.icon)
}
