package com.example.data.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class IQAirResponse(
    val status: String? = null,
    val data: IQAirData? = null
)

@JsonClass(generateAdapter = true)
data class IQAirData(
    val city: String? = null,
    val state: String? = null,
    val country: String? = null,
    val location: IQAirLocation? = null,
    val current: IQAirCurrent? = null
)

@JsonClass(generateAdapter = true)
data class IQAirLocation(
    val type: String? = null,
    val coordinates: List<Double>? = null
)

@JsonClass(generateAdapter = true)
data class IQAirCurrent(
    val pollution: IQAirPollution? = null,
    val weather: IQAirWeather? = null
)

@JsonClass(generateAdapter = true)
data class IQAirPollution(
    val ts: String? = null,
    val aqius: Int? = null,    // US AQI (0 - 500)
    val mainus: String? = null, // Main pollutant: p2 (PM2.5), p1 (PM10), o3 (Ozone), n2 (NO2), s2 (SO2), co (CO)
    val aqicn: Int? = null,    // China AQI
    val maincn: String? = null
)

@JsonClass(generateAdapter = true)
data class IQAirWeather(
    val ts: String? = null,
    val tp: Double? = null,    // Temperature in Celsius
    val pr: Double? = null,    // Pressure in hPa
    val hu: Double? = null,    // Humidity %
    val ws: Double? = null,    // Wind speed m/s
    val wd: Double? = null,    // Wind direction
    val ic: String? = null     // Weather icon code
)
