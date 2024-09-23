package com.dts.service

import android.location.Location
import kotlinx.coroutines.flow.Flow

interface LocationInterface {

    fun getLocationUpdates(interval: Long): Flow<Location>

}