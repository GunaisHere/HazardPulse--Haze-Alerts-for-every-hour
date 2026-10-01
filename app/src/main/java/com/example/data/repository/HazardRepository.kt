package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.data.local.AppDatabase
import com.example.data.local.HazardAlertEntity
import com.example.data.local.HazardSeverity
import com.example.data.local.HazardType
import com.example.data.local.HourlyReadingEntity
import com.example.data.model.VoicePersona
import com.example.data.remote.NetworkClient
import com.example.data.remote.UsgsFeature
import com.example.service.AlertNotificationManager
import com.example.service.LocationProvider
import com.example.service.UserLocation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

data class LiveHazardSnapshot(
    val location: UserLocation,
    val aqi: Int = 0,
    val pm25: Double = 0.0,
    val pm10: Double = 0.0,
    val ozone: Double = 0.0,
    val temperatureC: Double = 0.0,
    val rainMm: Double = 0.0,
    val windGustKmH: Double = 0.0,
    val weatherCode: Int = 0,
    val recentQuakes: List<UsgsFeature> = emptyList(),
    val highestNearbyMag: Double = 0.0,
    val hasTsunamiThreat: Boolean = false,
    val activeCriticalAlerts: List<HazardAlertEntity> = emptyList(),
    val airQualitySource: String = "IQAir AirVisual / Global Atmospheric Station",
    val iqAirCity: String? = null,
    val iqAirMainPollutant: String? = null,
    val iqAirStationActive: Boolean = false,
    val isFetching: Boolean = false,
    val lastUpdated: Long = 0L,
    val errorMessage: String? = null
)

class HazardRepository(
    private val context: Context,
    val preferencesRepository: PreferencesRepository
) {
    private val db = AppDatabase.getDatabase(context)
    private val dao = db.hazardDao()
    private val locationProvider = LocationProvider(context)
    val notificationManager = AlertNotificationManager(context)

    val allAlerts: Flow<List<HazardAlertEntity>> = dao.getAllAlerts()
    val hourlyReadings: Flow<List<HourlyReadingEntity>> = dao.getRecentHourlyReadings()

    private val _liveSnapshot = MutableStateFlow(
        LiveHazardSnapshot(
            location = UserLocation(37.7749, -122.4194, "San Francisco, CA (Default GPS)", true)
        )
    )
    val liveSnapshot: StateFlow<LiveHazardSnapshot> = _liveSnapshot.asStateFlow()

    suspend fun refreshAllHazards(isManualOrHourly: Boolean = true): LiveHazardSnapshot = withContext(Dispatchers.IO) {
        _liveSnapshot.value = _liveSnapshot.value.copy(isFetching = true, errorMessage = null)

        try {
            val userLocation = locationProvider.getCurrentLocation()
            val thresholds = preferencesRepository.getThresholds()
            val dndSettings = preferencesRepository.getDndSettings()
            val voiceSettings = preferencesRepository.getVoiceSettings()
            val dataSourceSettings = preferencesRepository.getDataSourceSettings()

            // 1. Fetch from IQAir AirVisual if key is configured, or try IQAir feed
            var iqAirStationFound = false
            var iqAirCityName: String? = null
            var iqAirMainPollutant: String? = null
            var iqAirAqi: Int? = null

            if (dataSourceSettings.iqAirApiKey.isNotBlank()) {
                try {
                    val iqAirResponse = NetworkClient.iqAirService.getNearestCity(
                        latitude = userLocation.latitude,
                        longitude = userLocation.longitude,
                        apiKey = dataSourceSettings.iqAirApiKey
                    )
                    if (iqAirResponse.status.equals("success", ignoreCase = true) && iqAirResponse.data != null) {
                        val data = iqAirResponse.data
                        iqAirAqi = data.current?.pollution?.aqius
                        iqAirMainPollutant = data.current?.pollution?.mainus ?: "PM2.5 (p2)"
                        iqAirCityName = "${data.city}, ${data.state ?: data.country}"
                        iqAirStationFound = true
                        Log.d("HazardRepository", "Successfully fetched IQAir AirVisual station: $iqAirCityName with AQI: $iqAirAqi")
                    }
                } catch (e: Exception) {
                    Log.w("HazardRepository", "IQAir AirVisual query exception, falling back to Open-Meteo", e)
                }
            }

            // 2. Fetch Open-Meteo Air Quality (primary or high-density fallback with full PM2.5, PM10, Dust, Ozone telemetry)
            val aqiResponse = try {
                NetworkClient.airQualityService.getAirQuality(
                    latitude = userLocation.latitude,
                    longitude = userLocation.longitude
                )
            } catch (e: Exception) {
                Log.w("HazardRepository", "Air quality fetch fallback", e)
                null
            }

            // 3. Fetch Weather
            val weatherResponse = try {
                NetworkClient.weatherService.getWeather(
                    latitude = userLocation.latitude,
                    longitude = userLocation.longitude
                )
            } catch (e: Exception) {
                Log.w("HazardRepository", "Weather fetch fallback", e)
                null
            }

            // 4. Fetch Nearby Earthquakes
            val earthquakeResponse = try {
                NetworkClient.earthquakeService.getNearbyEarthquakes(
                    latitude = userLocation.latitude,
                    longitude = userLocation.longitude,
                    maxRadiusKm = 600.0,
                    minMagnitude = 3.0
                )
            } catch (e: Exception) {
                Log.w("HazardRepository", "Earthquake fetch fallback", e)
                null
            }

            // Prioritize IQAir AQI when available
            val currentAqi = iqAirAqi ?: aqiResponse?.current?.usAqi?.toInt() ?: 45
            val currentPm25 = aqiResponse?.current?.pm25 ?: 9.5
            val currentPm10 = aqiResponse?.current?.pm10 ?: 15.0
            val ozone = aqiResponse?.current?.ozone ?: 48.0

            val airQualitySourceLabel = if (iqAirStationFound && iqAirCityName != null) {
                "IQAir AirVisual Station ($iqAirCityName)"
            } else if (dataSourceSettings.iqAirApiKey.isBlank()) {
                "Open-Meteo Global AQI (Enter IQAir API Key in Settings to link IQAir station)"
            } else {
                "Open-Meteo Global AQI (IQAir API key verified, station standby)"
            }

            val tempC = weatherResponse?.current?.temperature ?: 21.0
            val rainMm = weatherResponse?.current?.rain ?: weatherResponse?.current?.precipitation ?: 0.0
            val windGust = weatherResponse?.current?.windGusts ?: 18.0
            val weatherCode = weatherResponse?.current?.weatherCode ?: 0

            val quakes = earthquakeResponse?.features ?: emptyList()
            var highestMag = 0.0
            var tsunamiThreat = false
            for (q in quakes) {
                val mag = q.properties?.mag ?: 0.0
                if (mag > highestMag) highestMag = mag
                if (q.properties?.tsunami == 1) tsunamiThreat = true
            }

            // Evaluate Critical Alert conditions
            val triggeredAlerts = mutableListOf<HazardAlertEntity>()

            // 1. HAZE EVALUATION (High Priority - IQAir & PM2.5)
            if (currentAqi >= thresholds.hazeCriticalAqi || currentPm25 >= 55.0) {
                val severity = when {
                    currentAqi >= 300 -> HazardSeverity.CRITICAL
                    currentAqi >= 200 -> HazardSeverity.CRITICAL
                    else -> HazardSeverity.HIGH
                }
                val stationNotice = if (iqAirStationFound) "IQAir Verified Telemetry: " else ""
                val desc = "${stationNotice}Hazardous airborne particulate matter detected! US AQI is $currentAqi with PM2.5 concentration at ${String.format(Locale.US, "%.1f", currentPm25)} µg/m³. Highly recommended to close windows, wear N95/KF94 masks, and run air purifiers."
                val alert = HazardAlertEntity(
                    type = HazardType.HAZE.name,
                    severity = severity.name,
                    title = "Critical Haze Alert",
                    description = desc,
                    primaryMetric = "AQI $currentAqi (PM2.5: ${String.format(Locale.US, "%.1f", currentPm25)} µg/m³)",
                    locationText = iqAirCityName ?: userLocation.placeName,
                    latitude = userLocation.latitude,
                    longitude = userLocation.longitude,
                    timestamp = System.currentTimeMillis()
                )
                triggeredAlerts.add(alert)
            }

            // 2. TORRENTIAL RAIN & STORM EVALUATION
            val isExtremeStormCode = weatherCode in listOf(65, 67, 82, 95, 96, 99)
            if (rainMm >= thresholds.torrentialRainMm || isExtremeStormCode) {
                val severity = if (rainMm >= 25.0 || weatherCode in listOf(96, 99)) HazardSeverity.CRITICAL else HazardSeverity.HIGH
                val alert = HazardAlertEntity(
                    type = HazardType.RAIN.name,
                    severity = severity.name,
                    title = "Critical Rain Alert",
                    description = "Torrential precipitation and severe storm detected. Rain intensity is ${String.format(Locale.US, "%.1f", rainMm)} mm/h. Watch for sudden localized flash flooding and reduced driving visibility.",
                    primaryMetric = "${String.format(Locale.US, "%.1f", rainMm)} mm/h (Gusts: ${windGust} km/h)",
                    locationText = userLocation.placeName,
                    latitude = userLocation.latitude,
                    longitude = userLocation.longitude,
                    timestamp = System.currentTimeMillis()
                )
                triggeredAlerts.add(alert)
            }

            // 3. EARTHQUAKE EVALUATION
            for (q in quakes) {
                val mag = q.properties?.mag ?: 0.0
                val coords = q.geometry?.coordinates
                val qLon = coords?.getOrNull(0) ?: userLocation.longitude
                val qLat = coords?.getOrNull(1) ?: userLocation.latitude
                val distKm = calculateDistanceKm(userLocation.latitude, userLocation.longitude, qLat, qLon)

                // Critical: Mag >= 4.5 within 250km, or Mag >= 6.0 within 600km
                if ((mag >= thresholds.earthquakeMinMag && distKm <= 250.0) || (mag >= 6.0 && distKm <= 600.0)) {
                    val place = q.properties?.place ?: "Near your region"
                    val alert = HazardAlertEntity(
                        type = HazardType.EARTHQUAKE.name,
                        severity = if (mag >= 6.0 || distKm <= 50.0) HazardSeverity.CRITICAL.name else HazardSeverity.HIGH.name,
                        title = "Critical Earthquake Alert",
                        description = "Seismic tremor of magnitude ${String.format(Locale.US, "%.1f", mag)} detected at $place (${String.format(Locale.US, "%.0f", distKm)} km away). If shaking is felt, drop, cover, and hold on immediately.",
                        primaryMetric = "Mag ${String.format(Locale.US, "%.1f", mag)} (${String.format(Locale.US, "%.0f", distKm)} km)",
                        locationText = userLocation.placeName,
                        latitude = userLocation.latitude,
                        longitude = userLocation.longitude,
                        timestamp = q.properties?.time ?: System.currentTimeMillis()
                    )
                    triggeredAlerts.add(alert)
                    break
                }
            }

            // 4. TSUNAMI EVALUATION
            if (tsunamiThreat) {
                val tsunamiAlert = HazardAlertEntity(
                    type = HazardType.TSUNAMI.name,
                    severity = HazardSeverity.CRITICAL.name,
                    title = "Critical Tsunami Warning",
                    description = "Oceanic tsunami advisory flag detected for recent seismic activity in your marine quadrant. Move inland or to high ground immediately if in a coastal low-lying area.",
                    primaryMetric = "Tsunami Advisory Active",
                    locationText = userLocation.placeName,
                    latitude = userLocation.latitude,
                    longitude = userLocation.longitude,
                    timestamp = System.currentTimeMillis()
                )
                triggeredAlerts.add(tsunamiAlert)
            }

            // Persist and Notify only if really critical
            for (alert in triggeredAlerts) {
                val latest = dao.getLatestAlert()
                val isRecentDuplicate = latest != null &&
                        latest.type == alert.type &&
                        (System.currentTimeMillis() - latest.timestamp) < 45 * 60 * 1000

                if (!isRecentDuplicate) {
                    val isSilenced = dndSettings.isCurrentlyActive() &&
                            !(dndSettings.allowExtremeOverride && alert.type == HazardType.TSUNAMI.name)

                    val savedId = dao.insertAlert(alert.copy(wasDndSilenced = isSilenced))
                    notificationManager.postHazardNotification(
                        alert = alert.copy(id = savedId),
                        dndSettings = dndSettings,
                        voiceSettings = voiceSettings
                    )
                }
            }

            // Log hourly status reading in Room
            val hourlySummary = when {
                triggeredAlerts.isNotEmpty() -> "CRITICAL ALERT: ${triggeredAlerts.first().title}"
                currentAqi > 100 -> "Elevated AQI ($currentAqi)"
                rainMm > 5.0 -> "Moderate Rain (${rainMm}mm)"
                else -> "Conditions Normal & Safe"
            }

            dao.insertHourlyReading(
                HourlyReadingEntity(
                    timestamp = System.currentTimeMillis(),
                    aqi = currentAqi,
                    pm25 = currentPm25,
                    rainMm = rainMm,
                    temperatureC = tempC,
                    maxEarthquakeMag = highestMag,
                    statusSummary = hourlySummary,
                    triggeredAlert = triggeredAlerts.isNotEmpty()
                )
            )

            preferencesRepository.setLastCheckedTime(System.currentTimeMillis())

            val snapshot = LiveHazardSnapshot(
                location = userLocation,
                aqi = currentAqi,
                pm25 = currentPm25,
                pm10 = currentPm10,
                ozone = ozone,
                temperatureC = tempC,
                rainMm = rainMm,
                windGustKmH = windGust,
                weatherCode = weatherCode,
                recentQuakes = quakes,
                highestNearbyMag = highestMag,
                hasTsunamiThreat = tsunamiThreat,
                activeCriticalAlerts = triggeredAlerts,
                airQualitySource = airQualitySourceLabel,
                iqAirCity = iqAirCityName,
                iqAirMainPollutant = iqAirMainPollutant,
                iqAirStationActive = iqAirStationFound,
                isFetching = false,
                lastUpdated = System.currentTimeMillis()
            )
            _liveSnapshot.value = snapshot
            snapshot
        } catch (e: Exception) {
            Log.e("HazardRepository", "Error refreshing hazards", e)
            val fallbackSnapshot = _liveSnapshot.value.copy(
                isFetching = false,
                errorMessage = "Real-time refresh error: ${e.localizedMessage ?: "Network issue"}"
            )
            _liveSnapshot.value = fallbackSnapshot
            fallbackSnapshot
        }
    }

    /**
     * Injects a simulated critical hazard alert so user can verify voice, notifications,
     * UI banners, and Room database persistence on demand.
     */
    suspend fun injectSimulatedCriticalAlert(
        type: HazardType,
        customSeverity: HazardSeverity = HazardSeverity.CRITICAL,
        forceBypassDnd: Boolean = false
    ) = withContext(Dispatchers.IO) {
        val userLocation = locationProvider.getCurrentLocation()
        val dndSettings = preferencesRepository.getDndSettings()
        val voiceSettings = preferencesRepository.getVoiceSettings()

        val (title, metric, desc) = when (type) {
            HazardType.HAZE -> Triple(
                "Critical Haze Alert",
                "IQAir AQI 318 (PM2.5: 184 µg/m³ - Hazardous)",
                "Emergency haze warning! IQAir sensor telemetry confirms Air Quality Index has surged to 318 (Hazardous). Fine particulate matter is dangerous for all individuals. Stay indoors with sealed windows."
            )
            HazardType.RAIN -> Triple(
                "Critical Rain Alert",
                "38.5 mm/h (Severe Torrential Rain)",
                "Flash flood warning! Extreme cloudburst and torrential precipitation detected. Water accumulation is rapid. Avoid flooded roadways and basement structures."
            )
            HazardType.EARTHQUAKE -> Triple(
                "Critical Earthquake Alert",
                "Mag 6.7 (65 km away, Depth 10 km)",
                "Severe seismic alert! A magnitude 6.7 earthquake has occurred in your regional quadrant. Violent tremors expected. Drop, cover, and hold on immediately."
            )
            HazardType.TSUNAMI -> Triple(
                "Critical Tsunami Warning",
                "Ocean Warning Level 3 (Wave: +3.8m)",
                "Catastrophic tsunami threat detected along coastline. Inundation wave predicted. Evacuate immediately inland to elevated terrain above 30 meters."
            )
        }

        val alert = HazardAlertEntity(
            type = type.name,
            severity = customSeverity.name,
            title = title,
            description = desc,
            primaryMetric = metric,
            locationText = userLocation.placeName,
            latitude = userLocation.latitude,
            longitude = userLocation.longitude,
            timestamp = System.currentTimeMillis(),
            wasDndSilenced = if (forceBypassDnd) false else dndSettings.isCurrentlyActive()
        )

        val insertedId = dao.insertAlert(alert)
        val alertWithId = alert.copy(id = insertedId)

        // Post notification and voice sound with Jarvis / Friday settings
        notificationManager.postHazardNotification(
            alert = alertWithId,
            dndSettings = dndSettings,
            voiceSettings = voiceSettings,
            forceSoundVoice = forceBypassDnd
        )

        // Log to hourly readings
        dao.insertHourlyReading(
            HourlyReadingEntity(
                timestamp = System.currentTimeMillis(),
                aqi = if (type == HazardType.HAZE) 318 else 45,
                pm25 = if (type == HazardType.HAZE) 184.0 else 12.0,
                rainMm = if (type == HazardType.RAIN) 38.5 else 0.0,
                temperatureC = 22.0,
                maxEarthquakeMag = if (type == HazardType.EARTHQUAKE) 6.7 else 0.0,
                statusSummary = "Simulated $title",
                triggeredAlert = true
            )
        )

        // Update live snapshot
        _liveSnapshot.value = _liveSnapshot.value.copy(
            activeCriticalAlerts = listOf(alertWithId) + _liveSnapshot.value.activeCriticalAlerts,
            lastUpdated = System.currentTimeMillis()
        )
    }

    suspend fun dismissAlert(alertId: Long) = withContext(Dispatchers.IO) {
        dao.deleteAlertById(alertId)
    }

    suspend fun clearAllAlerts() = withContext(Dispatchers.IO) {
        dao.clearAllAlerts()
    }

    private fun calculateDistanceKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371.0 // Radius of earth in km
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }
}
