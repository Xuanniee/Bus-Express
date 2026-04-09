package com.xuannie.busexpress.ui.screens

import android.os.Bundle
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import org.maplibre.android.annotations.IconFactory
import org.maplibre.android.annotations.MarkerOptions
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.Style
import com.xuannie.busexpress.ui.component.TransferSuggestionCard
import com.xuannie.busexpress.ui.config.MapConfig
import com.xuannie.busexpress.ui.viewmodels.LiveTripViewModel

@Composable
fun LiveTripMapScreen(
    viewModel: LiveTripViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.snackbarMessage) {
        val msg = uiState.snackbarMessage
        if (!msg.isNullOrBlank()) {
            snackbarHostState.showSnackbar(
                message = msg,
                actionLabel = "Dismiss",
                duration = SnackbarDuration.Short
            )
            viewModel.clearSnackbar()
        }
    }

    LaunchedEffect(uiState.activeLegPath, uiState.activeLegStepIndex) {
        if (uiState.activeLegPath.isNotEmpty()) {
            while (true) {
                val state = viewModel.uiState.value
                if (state.activeLegPath.isEmpty()) break

                kotlinx.coroutines.delay(120)
                viewModel.advanceAlongActiveLeg()
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 6.dp
                )
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Live Trip Planner",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        if (uiState.activeTrip != null && uiState.activeTrip!!.isActive) {
                            Column {
                                Text(
                                    text = "Origin: ${uiState.activeTrip?.originStopCode ?: "-"}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "Destination: PBRI",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    if (uiState.activeTrip == null || !uiState.activeTrip!!.isActive) {
                        OutlinedTextField(
                            value = uiState.originStopCode,
                            onValueChange = { viewModel.updateOriginStopCode(it) },
                            label = { Text("Origin Bus Stop Code") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                focusedLabelColor = MaterialTheme.colorScheme.primary,
                                unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                cursorColor = MaterialTheme.colorScheme.primary
                            )
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        OutlinedTextField(
                            value = uiState.inputTimestamp,
                            onValueChange = { viewModel.updateInputTimestamp(it) },
                            label = { Text("Timestamp (ISO)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                focusedLabelColor = MaterialTheme.colorScheme.primary,
                                unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                cursorColor = MaterialTheme.colorScheme.primary
                            )
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Destination: Pasir Ris Bus Interchange",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = { viewModel.startTrip() },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Start Trip")
                        }
                    } else {
                        Text(
                            text = "Simulated Time: ${uiState.simulatedTimeDisplay}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Current Stop: ${uiState.tripProgress?.currentStopName ?: uiState.tripProgress?.currentStopCode ?: "-"}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Text(
                            text = "Next Stop: ${uiState.tripProgress?.nextStopName ?: uiState.tripProgress?.nextStopCode ?: "N.A."}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { viewModel.simulateTrip() },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Simulate Trip")
                            }

                            Button(
                                onClick = { viewModel.stopTrip() },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Stop Trip")
                            }
                        }
                    }
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 8.dp
                )
            ) {
                SmoothMapLibreMapView(
                    latitude = uiState.currentLatitude,
                    longitude = uiState.currentLongitude,
                    title = uiState.tripProgress?.currentStopName ?: "Current Stop",
                    modifier = Modifier.fillMaxSize()
                )
            }

            if (uiState.isLoading) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            uiState.suggestion?.let { suggestion ->
                if (viewModel.shouldShowTransferChoice()) {
                    TransferSuggestionCard(
                        suggestion = suggestion,
                        baselineArrivalTime = viewModel.getBaselineArrivalDisplay(),
                        transferArrivalTime = viewModel.getTransferArrivalDisplay(),
                        onUseBaseline = { viewModel.useBaselineJourney() },
                        onUseTransfer = { viewModel.useTransferJourney() }
                    )
                }
            }

            if (uiState.showArrivalDialog) {
                AlertDialog(
                    onDismissRequest = { viewModel.dismissArrivalDialog() },
                    title = {
                        Text(text = uiState.arrivalTitle)
                    },
                    text = {
                        Text(text = uiState.arrivalMessage)
                    },
                    confirmButton = {
                        Button(onClick = { viewModel.dismissArrivalDialog() }) {
                            Text("Dismiss")
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun SmoothMapLibreMapView(
    latitude: Double,
    longitude: Double,
    title: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val mapView = remember {
        MapView(context).apply {
            onCreate(Bundle())
        }
    }

    val mapHolder = remember { arrayOfNulls<org.maplibre.android.maps.MapLibreMap>(1) }
    val markerHolder = remember { arrayOfNulls<org.maplibre.android.annotations.Marker>(1) }

    DisposableEffect(Unit) {
        mapView.getMapAsync { map ->
            mapHolder[0] = map

            map.setStyle(Style.Builder().fromUri(MapConfig.STYLE_URL)) {
                val startTarget = LatLng(latitude, longitude)

                map.moveCamera(
                    CameraUpdateFactory.newCameraPosition(
                        CameraPosition.Builder()
                            .target(startTarget)
                            .zoom(17.5)
                            .build()
                    )
                )

                val icon = IconFactory.getInstance(context).defaultMarker()
                val marker = map.addMarker(
                    MarkerOptions()
                        .position(startTarget)
                        .title(title)
                        .icon(icon)
                )
                markerHolder[0] = marker
            }
        }

        onDispose {
            mapView.onStop()
            mapView.onDestroy()
        }
    }

    AndroidView(
        factory = { mapView },
        update = {
            val map = mapHolder[0] ?: return@AndroidView
            val marker = markerHolder[0] ?: return@AndroidView

            val target = LatLng(latitude, longitude)

            marker.position = target
            marker.title = title

            map.easeCamera(
                CameraUpdateFactory.newCameraPosition(
                    CameraPosition.Builder()
                        .target(target)
                        .zoom(17.5)
                        .build()
                ),
                120
            )
        },
        modifier = modifier
    )
}



//package com.xuannie.busexpress.ui.screens
//
//import android.os.Bundle
//import androidx.compose.animation.core.Animatable
//import androidx.compose.animation.core.tween
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.foundation.text.KeyboardOptions
//import androidx.compose.material.Button
//import androidx.compose.material.Card
//import androidx.compose.material.CircularProgressIndicator
//import androidx.compose.material.MaterialTheme
//import androidx.compose.material.OutlinedTextField
//import androidx.compose.material.Scaffold
//import androidx.compose.material.SnackbarDuration
//import androidx.compose.material.SnackbarHost
//import androidx.compose.material.SnackbarHostState
//import androidx.compose.material.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.DisposableEffect
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.remember
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.text.input.KeyboardType
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.viewinterop.AndroidView
//import org.maplibre.android.MapLibre
//import org.maplibre.android.annotations.IconFactory
//import org.maplibre.android.annotations.MarkerOptions
//import org.maplibre.android.camera.CameraPosition
//import org.maplibre.android.camera.CameraUpdateFactory
//import org.maplibre.android.geometry.LatLng
//import org.maplibre.android.maps.MapView
//import org.maplibre.android.maps.Style
//import com.xuannie.busexpress.ui.component.TransferSuggestionCard
//import com.xuannie.busexpress.ui.config.MapConfig
//import com.xuannie.busexpress.ui.viewmodels.LiveTripViewModel
//import kotlinx.coroutines.launch
//
//@Composable
//fun LiveTripMapScreen(
//    viewModel: LiveTripViewModel,
//    modifier: Modifier = Modifier
//) {
//    val uiState by viewModel.uiState
//    val context = LocalContext.current
//    val snackbarHostState = remember { SnackbarHostState() }
//
//    LaunchedEffect(uiState.snackbarMessage) {
//        val msg = uiState.snackbarMessage
//        if (!msg.isNullOrBlank()) {
//            snackbarHostState.showSnackbar(
//                message = msg,
//                actionLabel = "Dismiss",
//                duration = SnackbarDuration.Short
//            )
//            viewModel.clearSnackbar()
//        }
//    }
//
//    LaunchedEffect(uiState.activeLegPath, uiState.activeLegStepIndex) {
//        if (uiState.activeLegPath.isNotEmpty()) {
//            while (true) {
//                val state = viewModel.uiState.value
//                if (state.activeLegPath.isEmpty()) break
//
//                kotlinx.coroutines.delay(120)
//                viewModel.advanceAlongActiveLeg()
//            }
//        }
//    }
//
//    Scaffold(
//        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
//    ) { innerPadding ->
//        Column(
//            modifier = modifier
//                .fillMaxSize()
//                .padding(innerPadding)
//                .padding(12.dp),
//            verticalArrangement = Arrangement.spacedBy(8.dp)
//        ) {
//            Card(
//                modifier = Modifier.fillMaxWidth(),
//                elevation = 6.dp,
//                shape = RoundedCornerShape(12.dp)
//            ) {
//                Column(modifier = Modifier.padding(10.dp)) {
//                    Row(modifier = Modifier.fillMaxWidth()) {
//                        Column(modifier = Modifier.weight(1f)) {
//                            Text(
//                                text = "Live Trip Planner",
//                                style = MaterialTheme.typography.h6
//                            )
//                        }
//
//                        if (uiState.activeTrip != null && uiState.activeTrip!!.isActive) {
//                            Column {
//                                Text(
//                                    text = "Origin: ${uiState.activeTrip?.originStopCode ?: "-"}",
//                                    style = MaterialTheme.typography.caption
//                                )
//                                Text(
//                                    text = "Destination: PBRI",
//                                    style = MaterialTheme.typography.caption
//                                )
//                            }
//                        }
//                    }
//
//                    Spacer(modifier = Modifier.height(6.dp))
//
//                    if (uiState.activeTrip == null || !uiState.activeTrip!!.isActive) {
//                        OutlinedTextField(
//                            value = uiState.originStopCode,
//                            onValueChange = { viewModel.updateOriginStopCode(it) },
//                            label = { Text("Origin Bus Stop Code") },
//                            singleLine = true,
//                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
//                            modifier = Modifier.fillMaxWidth()
//                        )
//
//                        Spacer(modifier = Modifier.height(6.dp))
//
//                        OutlinedTextField(
//                            value = uiState.inputTimestamp,
//                            onValueChange = { viewModel.updateInputTimestamp(it) },
//                            label = { Text("Timestamp (ISO)") },
//                            singleLine = true,
//                            modifier = Modifier.fillMaxWidth()
//                        )
//
//                        Spacer(modifier = Modifier.height(6.dp))
//
//                        Text(
//                            text = "Destination: Pasir Ris Bus Interchange",
//                            style = MaterialTheme.typography.body2
//                        )
//
//                        Spacer(modifier = Modifier.height(8.dp))
//
//                        Button(
//                            onClick = { viewModel.startTrip() },
//                            modifier = Modifier.fillMaxWidth()
//                        ) {
//                            Text("Start Trip")
//                        }
//                    } else {
//                        Text(
//                            text = "Simulated Time: ${uiState.simulatedTimeDisplay}",
//                            style = MaterialTheme.typography.body1
//                        )
//
//                        Spacer(modifier = Modifier.height(4.dp))
//
//                        Text(
//                            text = "Current Stop: ${uiState.tripProgress?.currentStopName ?: uiState.tripProgress?.currentStopCode ?: "-"}",
//                            style = MaterialTheme.typography.body2
//                        )
//
//                        Text(
//                            text = "Next Stop: ${uiState.tripProgress?.nextStopName ?: uiState.tripProgress?.nextStopCode ?: "N.A."}",
//                            style = MaterialTheme.typography.body2
//                        )
//
//                        Spacer(modifier = Modifier.height(8.dp))
//
//                        Row(
//                            modifier = Modifier.fillMaxWidth(),
//                            horizontalArrangement = Arrangement.spacedBy(8.dp)
//                        ) {
//                            Button(
//                                onClick = { viewModel.simulateTrip() },
//                                modifier = Modifier.weight(1f)
//                            ) {
//                                Text("Simulate Trip")
//                            }
//
//                            Button(
//                                onClick = { viewModel.stopTrip() },
//                                modifier = Modifier.weight(1f)
//                            ) {
//                                Text("Stop Trip")
//                            }
//                        }
//                    }
//                }
//            }
//
//            Card(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .weight(1f),
//                elevation = 8.dp,
//                shape = RoundedCornerShape(12.dp)
//            ) {
//                SmoothMapLibreMapView(
//                    latitude = uiState.currentLatitude,
//                    longitude = uiState.currentLongitude,
//                    title = uiState.tripProgress?.currentStopName ?: "Current Stop",
//                    modifier = Modifier.fillMaxSize()
//                )
//            }
//
//            if (uiState.isLoading) {
//                Row(modifier = Modifier.fillMaxWidth()) {
//                    CircularProgressIndicator()
//                }
//            }
//
//            uiState.suggestion?.let { suggestion ->
//                if (viewModel.shouldShowTransferChoice()) {
//                    TransferSuggestionCard(
//                        suggestion = suggestion,
//                        baselineArrivalTime = viewModel.getBaselineArrivalDisplay(),
//                        transferArrivalTime = viewModel.getTransferArrivalDisplay(),
//                        onUseBaseline = { viewModel.useBaselineJourney() },
//                        onUseTransfer = { viewModel.useTransferJourney() }
//                    )
//                }
//            }
//
//            if (uiState.showArrivalDialog) {
//                androidx.compose.material.AlertDialog(
//                    onDismissRequest = { viewModel.dismissArrivalDialog() },
//                    title = {
//                        Text(text = uiState.arrivalTitle)
//                    },
//                    text = {
//                        Text(text = uiState.arrivalMessage)
//                    },
//                    confirmButton = {
//                        Button(onClick = { viewModel.dismissArrivalDialog() }) {
//                            Text("Dismiss")
//                        }
//                    }
//                )
//            }
//        }
//    }
//}
//
//@Composable
//private fun SmoothMapLibreMapView(
//    latitude: Double,
//    longitude: Double,
//    title: String,
//    modifier: Modifier = Modifier
//) {
//    val context = LocalContext.current
//
//    val mapView = remember {
//        MapView(context).apply {
//            onCreate(Bundle())
//        }
//    }
//
//    val mapHolder = remember { arrayOfNulls<org.maplibre.android.maps.MapLibreMap>(1) }
//    val markerHolder = remember { arrayOfNulls<org.maplibre.android.annotations.Marker>(1) }
//
//    DisposableEffect(Unit) {
//        mapView.getMapAsync { map ->
//            mapHolder[0] = map
//
//            map.setStyle(Style.Builder().fromUri(MapConfig.STYLE_URL)) {
//                val startTarget = LatLng(latitude, longitude)
//
//                map.moveCamera(
//                    CameraUpdateFactory.newCameraPosition(
//                        CameraPosition.Builder()
//                            .target(startTarget)
//                            .zoom(17.5)
//                            .build()
//                    )
//                )
//
//                val icon = IconFactory.getInstance(context).defaultMarker()
//                val marker = map.addMarker(
//                    MarkerOptions()
//                        .position(startTarget)
//                        .title(title)
//                        .icon(icon)
//                )
//                markerHolder[0] = marker
//            }
//        }
//
//        onDispose {
//            mapView.onStop()
//            mapView.onDestroy()
//        }
//    }
//
//    AndroidView(
//        factory = { mapView },
//        update = {
//            val map = mapHolder[0] ?: return@AndroidView
//            val marker = markerHolder[0] ?: return@AndroidView
//
//            val target = LatLng(latitude, longitude)
//
//            marker.position = target
//            marker.title = title
//
//            map.easeCamera(
//                CameraUpdateFactory.newCameraPosition(
//                    CameraPosition.Builder()
//                        .target(target)
//                        .zoom(17.5)
//                        .build()
//                ),
//                120
//            )
//        },
//        modifier = modifier
//    )
//}