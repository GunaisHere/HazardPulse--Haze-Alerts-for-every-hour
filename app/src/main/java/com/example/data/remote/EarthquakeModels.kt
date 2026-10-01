package com.example.data.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UsgsFeatureCollection(
    val type: String? = null,
    val features: List<UsgsFeature>? = null
)

@JsonClass(generateAdapter = true)
data class UsgsFeature(
    val id: String? = null,
    val properties: UsgsProperties? = null,
    val geometry: UsgsGeometry? = null
)

@JsonClass(generateAdapter = true)
data class UsgsProperties(
    val mag: Double? = null,
    val place: String? = null,
    val time: Long? = null,
    val tsunami: Int? = null, // 1 if alert issued, 0 otherwise
    val alert: String? = null, // "green", "yellow", "orange", "red"
    val title: String? = null
)

@JsonClass(generateAdapter = true)
data class UsgsGeometry(
    val type: String? = null,
    val coordinates: List<Double>? = null // [lon, lat, depth]
)
