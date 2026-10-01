package com.example.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log

object HourlyAlertScheduler {

    private const val REQUEST_CODE = 4099
    const val ACTION_HOURLY_CHECK = "com.example.hazardpulse.ACTION_HOURLY_CHECK"

    fun scheduleHourlyChecks(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, HourlyAlertReceiver::class.java).apply {
            action = ACTION_HOURLY_CHECK
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val triggerAtMillis = System.currentTimeMillis() + AlarmManager.INTERVAL_HOUR

        try {
            alarmManager.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                AlarmManager.INTERVAL_HOUR,
                pendingIntent
            )
            Log.d("HourlyAlertScheduler", "Hourly hazard check alarm scheduled successfully.")
        } catch (e: Exception) {
            Log.e("HourlyAlertScheduler", "Error scheduling hourly alarm", e)
        }
    }

    fun cancelHourlyChecks(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, HourlyAlertReceiver::class.java).apply {
            action = ACTION_HOURLY_CHECK
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }
    }
}
