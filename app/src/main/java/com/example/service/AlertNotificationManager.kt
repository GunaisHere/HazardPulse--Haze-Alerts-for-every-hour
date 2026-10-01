package com.example.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity
import com.example.data.local.HazardAlertEntity
import com.example.data.local.HazardType
import com.example.data.model.DndSettings
import com.example.data.model.VoiceSettings
import java.util.Calendar

class AlertNotificationManager(private val context: Context) {

    private val notificationManager: NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    val soundManager = AlertSoundManager(context)

    init {
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val hazeChannel = NotificationChannel(
                CHANNEL_HAZE,
                "Critical Haze & AQI Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Urgent notifications when air pollution or haze exceeds safety thresholds"
                enableVibration(true)
            }

            val rainChannel = NotificationChannel(
                CHANNEL_RAIN,
                "Critical Torrential Rain Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Flash flood, torrential downpour, and severe storm warnings"
                enableVibration(true)
            }

            val quakeChannel = NotificationChannel(
                CHANNEL_QUAKE,
                "Critical Earthquake Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Immediate warnings for severe earthquakes near your GPS coordinates"
                enableVibration(true)
            }

            val tsunamiChannel = NotificationChannel(
                CHANNEL_TSUNAMI,
                "Critical Tsunami Warnings",
                NotificationManager.IMPORTANCE_MAX
            ).apply {
                description = "Urgent coastal tsunami warnings"
                enableVibration(true)
            }

            val hourlyChannel = NotificationChannel(
                CHANNEL_HOURLY,
                "Hourly Monitoring Status",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Periodic hourly atmospheric and weather check summaries"
            }

            notificationManager.createNotificationChannels(
                listOf(hazeChannel, rainChannel, quakeChannel, tsunamiChannel, hourlyChannel)
            )
        }
    }

    fun postHazardNotification(
        alert: HazardAlertEntity,
        dndSettings: DndSettings,
        voiceSettings: VoiceSettings = VoiceSettings(),
        forceSoundVoice: Boolean = false
    ): Boolean {
        // Permission check on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }

        val hazardType = try {
            HazardType.valueOf(alert.type)
        } catch (e: Exception) {
            HazardType.HAZE
        }

        val isDndActive = dndSettings.isCurrentlyActive(Calendar.getInstance())
        val canBypassDnd = dndSettings.allowExtremeOverride &&
                (hazardType == HazardType.TSUNAMI || (hazardType == HazardType.EARTHQUAKE && alert.severity == "CRITICAL"))

        val shouldSilence = isDndActive && !canBypassDnd && !forceSoundVoice

        val channelId = when (hazardType) {
            HazardType.HAZE -> CHANNEL_HAZE
            HazardType.RAIN -> CHANNEL_RAIN
            HazardType.EARTHQUAKE -> CHANNEL_QUAKE
            HazardType.TSUNAMI -> CHANNEL_TSUNAMI
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("EXTRA_ALERT_ID", alert.id)
            putExtra("EXTRA_HAZARD_TYPE", alert.type)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            alert.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val titlePrefix = if (shouldSilence) "🌙 [Night DND] " else "⚠️ "
        val fullTitle = "$titlePrefix${alert.title}"

        val iconRes = android.R.drawable.ic_dialog_alert

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(iconRes)
            .setContentTitle(fullTitle)
            .setContentText(alert.description)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("${alert.description}\n\n📍 Location: ${alert.locationText}\n📊 Value: ${alert.primaryMetric}")
            )
            .setPriority(
                if (shouldSilence) NotificationCompat.PRIORITY_LOW
                else NotificationCompat.PRIORITY_MAX
            )
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setCategory(NotificationCompat.CATEGORY_ALARM)

        if (!shouldSilence) {
            builder.setVibrate(longArrayOf(0, 400, 200, 400))
        } else {
            builder.setSilent(true)
        }

        val notificationId = (alert.timestamp % 100000).toInt() + 100
        notificationManager.notify(notificationId, builder.build())

        // Trigger spoken voice announcement with Stark HUD chime if not silenced
        if (!shouldSilence) {
            soundManager.triggerAlertSoundAndVoice(
                hazardType = hazardType,
                voiceSettings = voiceSettings
            )
        }

        return !shouldSilence
    }

    companion object {
        const val CHANNEL_HAZE = "channel_critical_haze"
        const val CHANNEL_RAIN = "channel_critical_rain"
        const val CHANNEL_QUAKE = "channel_critical_quake"
        const val CHANNEL_TSUNAMI = "channel_critical_tsunami"
        const val CHANNEL_HOURLY = "channel_hourly_monitoring"
    }
}
