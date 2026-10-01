package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.DataSourceSettings
import com.example.data.model.DndSettings
import com.example.data.model.HazardThresholds
import com.example.data.model.VoicePersona
import com.example.data.model.VoiceSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PreferencesRepository(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        "hazard_pulse_preferences",
        Context.MODE_PRIVATE
    )

    private val _dndSettingsFlow = MutableStateFlow(getDndSettings())
    val dndSettingsFlow: StateFlow<DndSettings> = _dndSettingsFlow.asStateFlow()

    private val _thresholdsFlow = MutableStateFlow(getThresholds())
    val thresholdsFlow: StateFlow<HazardThresholds> = _thresholdsFlow.asStateFlow()

    private val _voiceSettingsFlow = MutableStateFlow(getVoiceSettings())
    val voiceSettingsFlow: StateFlow<VoiceSettings> = _voiceSettingsFlow.asStateFlow()

    private val _dataSourceSettingsFlow = MutableStateFlow(getDataSourceSettings())
    val dataSourceSettingsFlow: StateFlow<DataSourceSettings> = _dataSourceSettingsFlow.asStateFlow()

    private val _hourlyMonitoringEnabled = MutableStateFlow(
        prefs.getBoolean(KEY_HOURLY_MONITORING, true)
    )
    val hourlyMonitoringEnabled: StateFlow<Boolean> = _hourlyMonitoringEnabled.asStateFlow()

    fun getDndSettings(): DndSettings {
        return DndSettings(
            enabled = prefs.getBoolean(KEY_DND_ENABLED, true),
            startHour = prefs.getInt(KEY_DND_START_HOUR, 22),
            startMinute = prefs.getInt(KEY_DND_START_MIN, 0),
            endHour = prefs.getInt(KEY_DND_END_HOUR, 7),
            endMinute = prefs.getInt(KEY_DND_END_MIN, 0),
            allowExtremeOverride = prefs.getBoolean(KEY_DND_EXTREME_OVERRIDE, true)
        )
    }

    fun updateDndSettings(settings: DndSettings) {
        prefs.edit()
            .putBoolean(KEY_DND_ENABLED, settings.enabled)
            .putInt(KEY_DND_START_HOUR, settings.startHour)
            .putInt(KEY_DND_START_MIN, settings.startMinute)
            .putInt(KEY_DND_END_HOUR, settings.endHour)
            .putInt(KEY_DND_END_MIN, settings.endMinute)
            .putBoolean(KEY_DND_EXTREME_OVERRIDE, settings.allowExtremeOverride)
            .apply()
        _dndSettingsFlow.value = settings
    }

    fun getVoiceSettings(): VoiceSettings {
        val personaName = prefs.getString(KEY_VOICE_PERSONA, VoicePersona.JARVIS.name) ?: VoicePersona.JARVIS.name
        val persona = try {
            VoicePersona.valueOf(personaName)
        } catch (e: Exception) {
            VoicePersona.JARVIS
        }
        return VoiceSettings(
            persona = persona,
            userCallsign = prefs.getString(KEY_USER_CALLSIGN, persona.defaultCallsign) ?: persona.defaultCallsign,
            pitch = prefs.getFloat(KEY_VOICE_PITCH, persona.defaultPitch),
            speed = prefs.getFloat(KEY_VOICE_SPEED, persona.defaultSpeed),
            playHudChime = prefs.getBoolean(KEY_PLAY_HUD_CHIME, true)
        )
    }

    fun updateVoiceSettings(settings: VoiceSettings) {
        prefs.edit()
            .putString(KEY_VOICE_PERSONA, settings.persona.name)
            .putString(KEY_USER_CALLSIGN, settings.userCallsign)
            .putFloat(KEY_VOICE_PITCH, settings.pitch)
            .putFloat(KEY_VOICE_SPEED, settings.speed)
            .putBoolean(KEY_PLAY_HUD_CHIME, settings.playHudChime)
            .apply()
        _voiceSettingsFlow.value = settings
    }

    fun getDataSourceSettings(): DataSourceSettings {
        return DataSourceSettings(
            iqAirApiKey = prefs.getString(KEY_IQAIR_API_KEY, "") ?: "",
            preferIQAir = prefs.getBoolean(KEY_PREFER_IQAIR, true)
        )
    }

    fun updateDataSourceSettings(settings: DataSourceSettings) {
        prefs.edit()
            .putString(KEY_IQAIR_API_KEY, settings.iqAirApiKey.trim())
            .putBoolean(KEY_PREFER_IQAIR, settings.preferIQAir)
            .apply()
        _dataSourceSettingsFlow.value = settings
    }

    fun getThresholds(): HazardThresholds {
        return HazardThresholds(
            hazeCriticalAqi = prefs.getInt(KEY_HAZE_AQI_THRESH, 150),
            torrentialRainMm = prefs.getFloat(KEY_RAIN_THRESH, 15.0f).toDouble(),
            earthquakeMinMag = prefs.getFloat(KEY_QUAKE_THRESH, 4.5f).toDouble(),
            voiceEnabled = prefs.getBoolean(KEY_VOICE_ENABLED, true),
            vibrationEnabled = prefs.getBoolean(KEY_VIBRATION_ENABLED, true)
        )
    }

    fun updateThresholds(thresholds: HazardThresholds) {
        prefs.edit()
            .putInt(KEY_HAZE_AQI_THRESH, thresholds.hazeCriticalAqi)
            .putFloat(KEY_RAIN_THRESH, thresholds.torrentialRainMm.toFloat())
            .putFloat(KEY_QUAKE_THRESH, thresholds.earthquakeMinMag.toFloat())
            .putBoolean(KEY_VOICE_ENABLED, thresholds.voiceEnabled)
            .putBoolean(KEY_VIBRATION_ENABLED, thresholds.vibrationEnabled)
            .apply()
        _thresholdsFlow.value = thresholds
    }

    fun setHourlyMonitoringEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_HOURLY_MONITORING, enabled).apply()
        _hourlyMonitoringEnabled.value = enabled
    }

    fun getLastCheckedTime(): Long {
        return prefs.getLong(KEY_LAST_CHECKED, 0L)
    }

    fun setLastCheckedTime(timestamp: Long) {
        prefs.edit().putLong(KEY_LAST_CHECKED, timestamp).apply()
    }

    companion object {
        private const val KEY_DND_ENABLED = "key_dnd_enabled"
        private const val KEY_DND_START_HOUR = "key_dnd_start_hour"
        private const val KEY_DND_START_MIN = "key_dnd_start_min"
        private const val KEY_DND_END_HOUR = "key_dnd_end_hour"
        private const val KEY_DND_END_MIN = "key_dnd_end_min"
        private const val KEY_DND_EXTREME_OVERRIDE = "key_dnd_extreme_override"

        private const val KEY_HAZE_AQI_THRESH = "key_haze_aqi_thresh"
        private const val KEY_RAIN_THRESH = "key_rain_thresh"
        private const val KEY_QUAKE_THRESH = "key_quake_thresh"
        private const val KEY_VOICE_ENABLED = "key_voice_enabled"
        private const val KEY_VIBRATION_ENABLED = "key_vibration_enabled"

        private const val KEY_VOICE_PERSONA = "key_voice_persona"
        private const val KEY_USER_CALLSIGN = "key_user_callsign"
        private const val KEY_VOICE_PITCH = "key_voice_pitch"
        private const val KEY_VOICE_SPEED = "key_voice_speed"
        private const val KEY_PLAY_HUD_CHIME = "key_play_hud_chime"

        private const val KEY_IQAIR_API_KEY = "key_iqair_api_key"
        private const val KEY_PREFER_IQAIR = "key_prefer_iqair"

        private const val KEY_HOURLY_MONITORING = "key_hourly_monitoring"
        private const val KEY_LAST_CHECKED = "key_last_checked"
    }
}
