package ru.GB.weathergb

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.GB.weathergb.databinding.ActivityMainBinding
import ru.GB.weathergb.view.DetailsFragment

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        supportFragmentManager.beginTransaction().add(R.id.container, DetailsFragment()).commit()
    }
}