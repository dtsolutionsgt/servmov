package com.dts.service

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

class LocationServiceHandler (val context: Context, val client: FusedLocationProviderClient ) : LocationInterface {

    @SuppressLint("MissingPermission")
    override fun getLocationUpdates(interval: Long): Flow<Location> {

        return callbackFlow {

            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

            if (!isGpsEnabled && !isNetworkEnabled) {
                throw Exception("GPS deshabilitado")
            }

            val request = LocationRequest.Builder(interval)
                .setMinUpdateDistanceMeters(0f)
                .setIntervalMillis(5000L)
                .setWaitForAccurateLocation(true)
                .build()

            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(location: LocationResult) {
                    super.onLocationResult(location)
                    location.locations.lastOrNull()?.let {
                        launch { send(it) }
                    }
                }
            }

            client.requestLocationUpdates(request,locationCallback,Looper.getMainLooper())

            awaitClose {
                client.removeLocationUpdates(locationCallback)
            }

        }
    }

}