package ru.GB.weathergb.view.testReceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

const val TEXT_KEY = "TEXT_KEY"

class BroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.getStringExtra(TEXT_KEY)?.also { Log.d("Пришел интент", it) }
    }
}