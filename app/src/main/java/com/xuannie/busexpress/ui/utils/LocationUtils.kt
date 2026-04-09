package com.xuannie.busexpress.ui.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

@SuppressLint("MissingPermission")
fun getCurrentUserLocation(
    context: Context,
    onLocationResult: (Location?) -> Unit
) {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    val request = CurrentLocationRequest.Builder()
        .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
        .build()

    fusedLocationClient
        .getCurrentLocation(request, null)
        .addOnSuccessListener { location ->
            onLocationResult(location)
        }
        .addOnFailureListener {
            onLocationResult(null)
        }
}