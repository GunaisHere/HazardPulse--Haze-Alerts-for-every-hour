package com.example.data.remote

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface AirQualityService {
    @GET("v1/air-quality")
    suspend fun getAirQuality(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current") current: String = "us_aqi,european_aqi,pm10,pm2_5,carbon_monoxide,nitrogen_dioxide,sulphur_dioxide,ozone,dust",
        @Query("forecast_days") forecastDays: Int = 1
    ): AirQualityResponse
}

interface IQAirService {
    @GET("v2/nearest_city")
    suspend fun getNearestCity(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("key") apiKey: String
    ): IQAirResponse
}

interface WeatherService {
    @GET("v1/forecast")
    suspend fun getWeather(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current") current: String = "temperature_2m,relative_humidity_2m,precipitation,rain,showers,weather_code,wind_speed_10m,wind_gusts_10m",
        @Query("forecast_days") forecastDays: Int = 1
    ): WeatherResponse
}

interface EarthquakeService {
    @GET("fdsnws/event/1/query")
    suspend fun getNearbyEarthquakes(
        @Query("format") format: String = "geojson",
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("maxradiuskm") maxRadiusKm: Double = 600.0,
        @Query("minmagnitude") minMagnitude: Double = 3.0,
        @Query("orderby") orderby: String = "time",
        @Query("limit") limit: Int = 10
    ): UsgsFeatureCollection
}

object NetworkClient {
    private val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(12, TimeUnit.SECONDS)
        .readTimeout(12, TimeUnit.SECONDS)
        .build()

    val airQualityService: AirQualityService = Retrofit.Builder()
        .baseUrl("https://air-quality-api.open-meteo.com/")
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()
        .create(AirQualityService::class.java)

    val iqAirService: IQAirService = Retrofit.Builder()
        .baseUrl("https://api.airvisual.com/")
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()
        .create(IQAirService::class.java)

    val weatherService: WeatherService = Retrofit.Builder()
        .baseUrl("https://api.open-meteo.com/")
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()
        .create(WeatherService::class.java)

    val earthquakeService: EarthquakeService = Retrofit.Builder()
        .baseUrl("https://earthquake.usgs.gov/")
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()
        .create(EarthquakeService::class.java)
}
