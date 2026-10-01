package com.example.data.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AirQualityResponse(
    val latitude: Double? = null,
    val longitude: Double? = null,
    val current: AirQualityCurrent? = null
)

@JsonClass(generateAdapter = true)
data class AirQualityCurrent(
    val time: String? = null,
    @Json(name = "us_aqi") val usAqi: Double? = null,
    @Json(name = "european_aqi") val europeanAqi: Double? = null,
    @Json(name = "pm10") val pm10: Double? = null,
    @Json(name = "pm2_5") val pm25: Double? = null,
    @Json(name = "carbon_monoxide") val carbonMonoxide: Double? = null,
    @Json(name = "nitrogen_dioxide") val nitrogenDioxide: Double? = null,
    @Json(name = "sulphur_dioxide") val sulphurDioxide: Double? = null,
    val ozone: Double? = null,
    val dust: Double? = null
)
