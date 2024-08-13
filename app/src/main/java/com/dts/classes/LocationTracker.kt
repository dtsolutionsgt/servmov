package com.dts.classes

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.CancellationSignal
import androidx.annotation.NonNull
import androidx.core.location.LocationManagerCompat
import androidx.core.util.Consumer
import com.google.android.gms.maps.model.LatLng
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean


open class LocationTracker {

    fun getCurrentLocation(@NonNull context: Context,
                           onLocationChangeListener: OnLocationChangeListener ) {
        val locationManager = context.getSystemService(LOCATION_SERVICE)
        val foundLocation = AtomicBoolean(false)
        val locationConsumer: Consumer<Location> = Consumer<Location> { location ->
            if (!foundLocation.get()) {
                foundLocation.set(true)
                if (location != null) {
                    onLocationChangeListener.onComplete(getLatLng(location))
                } else {
                    onLocationChangeListener.onComplete(LatLng(0.0, 0.0))
                }
            }
        }

        registerLocationCallbackListener(
            locationManager as LocationManager, LocationManager.GPS_PROVIDER,
            locationConsumer
        )

        registerLocationCallbackListener(
            locationManager as LocationManager, LocationManager.NETWORK_PROVIDER,
            locationConsumer
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            registerLocationCallbackListener(
                locationManager as LocationManager, LocationManager.FUSED_PROVIDER,
                locationConsumer
            )
        }
    }

    @SuppressLint("MissingPermission")
    fun registerLocationCallbackListener(locationManager: LocationManager,
                                         networkProvider: String, locationConsumer: Consumer<Location> ) {
        LocationManagerCompat.getCurrentLocation(
            locationManager, networkProvider,
            CancellationSignal(), Executors.newSingleThreadExecutor(), locationConsumer
        )
    }

    fun getLatLng(@NonNull location: Location): LatLng? {
        return LatLng(location.latitude, location.longitude)
    }

    interface OnLocationChangeListener {
        fun onComplete(latLng: LatLng?)
    }


}