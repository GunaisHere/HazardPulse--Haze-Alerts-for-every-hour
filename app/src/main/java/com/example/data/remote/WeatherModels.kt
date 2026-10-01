package com.example.data.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WeatherResponse(
    val latitude: Double? = null,
    val longitude: Double? = null,
    val current: WeatherCurrent? = null
)

@JsonClass(generateAdapter = true)
data class WeatherCurrent(
    val time: String? = null,
    @Json(name = "temperature_2m") val temperature: Double? = null,
    @Json(name = "relative_humidity_2m") val humidity: Double? = null,
    val precipitation: Double? = null,
    val rain: Double? = null,
    val showers: Double? = null,
    val snowfall: Double? = null,
    @Json(name = "weather_code") val weatherCode: Int? = null,
    @Json(name = "wind_speed_10m") val windSpeed: Double? = null,
    @Json(name = "wind_gusts_10m") val windGusts: Double? = null
)
