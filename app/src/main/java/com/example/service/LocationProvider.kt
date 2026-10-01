package com.example.service

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Locale
import kotlin.coroutines.resume

data class UserLocation(
    val latitude: Double,
    val longitude: Double,
    val placeName: String,
    val isDefaultFallback: Boolean = false
)

class LocationProvider(private val context: Context) {

    private val fusedClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }

    fun hasLocationPermission(): Boolean {
        val fine = ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarse = ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        return fine || coarse
    }

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): UserLocation {
        if (!hasLocationPermission()) {
            return getDefaultLocation("Permission Required (Using Default)")
        }

        try {
            // First try high accuracy current location from FusedClient
            val cancellationTokenSource = CancellationTokenSource()
            val location: Location? = suspendCancellableCoroutine { continuation ->
                fusedClient.getCurrentLocation(
                    Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                    cancellationTokenSource.token
                ).addOnSuccessListener { loc ->
                    continuation.resume(loc)
                }.addOnFailureListener {
                    continuation.resume(null)
                }
            }

            if (location != null) {
                val place = getAddressFromCoordinates(location.latitude, location.longitude)
                return UserLocation(location.latitude, location.longitude, place, false)
            }

            // Fallback to last known location
            val lastLocation: Location? = suspendCancellableCoroutine { continuation ->
                fusedClient.lastLocation.addOnSuccessListener { loc ->
                    continuation.resume(loc)
                }.addOnFailureListener {
                    continuation.resume(null)
                }
            }

            if (lastLocation != null) {
                val place = getAddressFromCoordinates(lastLocation.latitude, lastLocation.longitude)
                return UserLocation(lastLocation.latitude, lastLocation.longitude, place, false)
            }

            // Fallback to Android standard LocationManager
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
            val providers = locationManager?.getProviders(true) ?: emptyList()
            var bestLoc: Location? = null
            for (provider in providers) {
                val l = locationManager?.getLastKnownLocation(provider) ?: continue
                if (bestLoc == null || l.accuracy < bestLoc.accuracy) {
                    bestLoc = l
                }
            }

            if (bestLoc != null) {
                val place = getAddressFromCoordinates(bestLoc.latitude, bestLoc.longitude)
                return UserLocation(bestLoc.latitude, bestLoc.longitude, place, false)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return getDefaultLocation("Current Location (Fallback)")
    }

    private fun getDefaultLocation(label: String): UserLocation {
        // Default to a recognized coordinate with real atmospheric data
        return UserLocation(
            latitude = 37.7749,
            longitude = -122.4194,
            placeName = "San Francisco, CA (Demo/Default GPS)",
            isDefaultFallback = true
        )
    }

    private fun getAddressFromCoordinates(lat: Double, lon: Double): String {
        return try {
            val geocoder = Geocoder(context, Locale.getDefault())
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val addresses = geocoder.getFromLocation(lat, lon, 1)
                if (!addresses.isNullOrEmpty()) {
                    val a = addresses[0]
                    val city = a.locality ?: a.subAdminArea ?: a.adminArea ?: "Unknown Area"
                    val country = a.countryName ?: ""
                    return "$city, $country"
                }
            } else {
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocation(lat, lon, 1)
                if (!addresses.isNullOrEmpty()) {
                    val a = addresses[0]
                    val city = a.locality ?: a.subAdminArea ?: a.adminArea ?: "Unknown Area"
                    val country = a.countryName ?: ""
                    return "$city, $country"
                }
            }
            String.format(Locale.US, "Lat: %.3f, Lon: %.3f", lat, lon)
        } catch (e: Exception) {
            String.format(Locale.US, "Lat: %.3f, Lon: %.3f", lat, lon)
        }
    }
}
