package com.example.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.HazardPulseApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HourlyAlertReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("HourlyAlertReceiver", "Hourly hazard check triggered: ${intent.action}")

        val app = context.applicationContext as? HazardPulseApp ?: return
        val prefs = app.preferencesRepository
        if (!prefs.hourlyMonitoringEnabled.value) {
            Log.d("HourlyAlertReceiver", "Hourly monitoring disabled by user. Skipping.")
            return
        }

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                app.hazardRepository.refreshAllHazards(isManualOrHourly = true)
                Log.d("HourlyAlertReceiver", "Hourly hazard check completed successfully.")
            } catch (e: Exception) {
                Log.e("HourlyAlertReceiver", "Error during hourly hazard check", e)
            } finally {
                pendingResult.finish()
            }
        }
    }
}
