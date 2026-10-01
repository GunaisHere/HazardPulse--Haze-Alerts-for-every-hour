package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.HazardPulseApp
import com.example.data.local.HazardAlertEntity
import com.example.data.local.HazardSeverity
import com.example.data.local.HazardType
import com.example.data.local.HourlyReadingEntity
import com.example.data.model.DataSourceSettings
import com.example.data.model.DndSettings
import com.example.data.model.HazardThresholds
import com.example.data.model.VoicePersona
import com.example.data.model.VoiceSettings
import com.example.data.repository.HazardRepository
import com.example.data.repository.LiveHazardSnapshot
import com.example.data.repository.PreferencesRepository
import com.example.service.HourlyAlertScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

enum class HazardNavTab(val title: String) {
    DASHBOARD("Radar & Alerts"),
    HAZE_DETAIL("IQAir & Haze"),
    HOURLY("Hourly Monitor"),
    DND_SETTINGS("AI Voice & DND"),
    SIMULATE("Test Alerts")
}

class HazardViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as HazardPulseApp
    private val repository: HazardRepository = app.hazardRepository
    private val preferencesRepository: PreferencesRepository = app.preferencesRepository

    val liveSnapshot: StateFlow<LiveHazardSnapshot> = repository.liveSnapshot
    val allAlerts: StateFlow<List<HazardAlertEntity>> = repository.allAlerts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val hourlyReadings: StateFlow<List<HourlyReadingEntity>> = repository.hourlyReadings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val dndSettings: StateFlow<DndSettings> = preferencesRepository.dndSettingsFlow
    val thresholds: StateFlow<HazardThresholds> = preferencesRepository.thresholdsFlow
    val voiceSettings: StateFlow<VoiceSettings> = preferencesRepository.voiceSettingsFlow
    val dataSourceSettings: StateFlow<DataSourceSettings> = preferencesRepository.dataSourceSettingsFlow
    val hourlyMonitoringEnabled: StateFlow<Boolean> = preferencesRepository.hourlyMonitoringEnabled

    private val _currentTab = MutableStateFlow(HazardNavTab.DASHBOARD)
    val currentTab: StateFlow<HazardNavTab> = _currentTab.asStateFlow()

    private val _isDndActiveNow = MutableStateFlow(dndSettings.value.isCurrentlyActive(Calendar.getInstance()))
    val isDndActiveNow: StateFlow<Boolean> = _isDndActiveNow.asStateFlow()

    init {
        refreshHazards()
        updateDndActiveStatus()
    }

    fun selectTab(tab: HazardNavTab) {
        _currentTab.value = tab
    }

    fun refreshHazards() {
        viewModelScope.launch {
            repository.refreshAllHazards(isManualOrHourly = true)
            updateDndActiveStatus()
        }
    }

    fun updateDndActiveStatus() {
        _isDndActiveNow.value = dndSettings.value.isCurrentlyActive(Calendar.getInstance())
    }

    fun updateDndSettings(settings: DndSettings) {
        preferencesRepository.updateDndSettings(settings)
        updateDndActiveStatus()
    }

    fun updateVoiceSettings(settings: VoiceSettings) {
        preferencesRepository.updateVoiceSettings(settings)
    }

    fun updateDataSourceSettings(settings: DataSourceSettings) {
        preferencesRepository.updateDataSourceSettings(settings)
        // Refresh with new data source key immediately
        refreshHazards()
    }

    fun updateThresholds(newThresholds: HazardThresholds) {
        preferencesRepository.updateThresholds(newThresholds)
    }

    fun toggleHourlyMonitoring(enabled: Boolean) {
        preferencesRepository.setHourlyMonitoringEnabled(enabled)
        if (enabled) {
            HourlyAlertScheduler.scheduleHourlyChecks(getApplication())
        } else {
            HourlyAlertScheduler.cancelHourlyChecks(getApplication())
        }
    }

    fun testVoiceSound(type: HazardType, overridePersona: VoicePersona? = null) {
        val currentSettings = voiceSettings.value
        val effectiveSettings = if (overridePersona != null) {
            currentSettings.copy(
                persona = overridePersona,
                userCallsign = overridePersona.defaultCallsign,
                pitch = overridePersona.defaultPitch,
                speed = overridePersona.defaultSpeed
            )
        } else currentSettings

        repository.notificationManager.soundManager.triggerAlertSoundAndVoice(
            hazardType = type,
            voiceSettings = effectiveSettings,
            forceBypassDnd = true
        )
    }

    fun injectSimulation(type: HazardType, forceBypassDnd: Boolean = false) {
        viewModelScope.launch {
            repository.injectSimulatedCriticalAlert(
                type = type,
                customSeverity = HazardSeverity.CRITICAL,
                forceBypassDnd = forceBypassDnd
            )
            updateDndActiveStatus()
        }
    }

    fun dismissAlert(id: Long) {
        viewModelScope.launch {
            repository.dismissAlert(id)
        }
    }

    fun clearAllAlerts() {
        viewModelScope.launch {
            repository.clearAllAlerts()
        }
    }
}
