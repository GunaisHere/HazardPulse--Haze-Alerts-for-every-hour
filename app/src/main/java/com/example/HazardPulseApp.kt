package com.example

import android.app.Application
import com.example.data.repository.HazardRepository
import com.example.data.repository.PreferencesRepository
import com.example.service.HourlyAlertScheduler

class HazardPulseApp : Application() {

    lateinit var preferencesRepository: PreferencesRepository
        private set

    lateinit var hazardRepository: HazardRepository
        private set

    override fun onCreate() {
        super.onCreate()
        preferencesRepository = PreferencesRepository(this)
        hazardRepository = HazardRepository(this, preferencesRepository)

        if (preferencesRepository.hourlyMonitoringEnabled.value) {
            HourlyAlertScheduler.scheduleHourlyChecks(this)
        }
    }
}
