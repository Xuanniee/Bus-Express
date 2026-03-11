package com.xuannie.busexpress.ui.screens

import android.content.Context
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
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
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
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
import com.xuannie.busexpress.ui.component.TransferSuggestionCard
import com.xuannie.busexpress.ui.viewmodels.LiveTripViewModel
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker

@Composable
fun LiveTripMapScreen(
    viewModel: LiveTripViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        initOsm(context)
    }

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

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
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
                elevation = 6.dp,
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Live Trip Planner",
                                style = MaterialTheme.typography.h6
                            )
                        }

                        if (uiState.activeTrip != null && uiState.activeTrip!!.isActive) {
                            Column {
                                Text(
                                    text = "Origin: ${uiState.activeTrip?.originStopCode ?: "-"}",
                                    style = MaterialTheme.typography.caption
                                )
                                Text(
                                    text = "Destination: PBRI",
                                    style = MaterialTheme.typography.caption
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
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        OutlinedTextField(
                            value = uiState.inputTimestamp,
                            onValueChange = { viewModel.updateInputTimestamp(it) },
                            label = { Text("Timestamp (ISO)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Destination: Pasir Ris Bus Interchange",
                            style = MaterialTheme.typography.body2
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
                            style = MaterialTheme.typography.body1
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Current Stop: ${uiState.tripProgress?.currentStopName ?: uiState.tripProgress?.currentStopCode ?: "-"}",
                            style = MaterialTheme.typography.body2
                        )

                        Text(
                            text = "Next Stop: ${uiState.tripProgress?.nextStopName ?: uiState.tripProgress?.nextStopCode ?: "N.A."}",
                            style = MaterialTheme.typography.body2
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
                elevation = 8.dp,
                shape = RoundedCornerShape(12.dp)
            ) {
                SmoothOsmMapView(
                    latitude = uiState.currentLatitude,
                    longitude = uiState.currentLongitude,
                    title = uiState.tripProgress?.currentStopName ?: "Current Stop",
                    modifier = Modifier.fillMaxSize()
                )
            }

            if (uiState.isLoading) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    CircularProgressIndicator()
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

            Text(
                text = "© OpenStreetMap contributors",
                style = MaterialTheme.typography.caption,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun SmoothOsmMapView(
    latitude: Double,
    longitude: Double,
    title: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val animatedLat = remember { Animatable(latitude.toFloat()) }
    val animatedLng = remember { Animatable(longitude.toFloat()) }

    LaunchedEffect(latitude, longitude) {
        launch { animatedLat.animateTo(latitude.toFloat(), animationSpec = tween(900)) }
        launch { animatedLng.animateTo(longitude.toFloat(), animationSpec = tween(900)) }
    }

    val mapView = remember {
        initOsm(context)
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            zoomController.setVisibility(CustomZoomButtonsController.Visibility.ALWAYS)
            controller.setZoom(15.0)
        }
    }

    DisposableEffect(Unit) {
        onDispose { mapView.onDetach() }
    }

    AndroidView(
        factory = { mapView },
        update = { view ->
            view.overlays.clear()

            val point = GeoPoint(animatedLat.value.toDouble(), animatedLng.value.toDouble())
            view.controller.animateTo(point)

            val marker = Marker(view).apply {
                position = point
                this.title = title
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            }
            view.overlays.add(marker)

            val mapEventsOverlay = MapEventsOverlay(object : MapEventsReceiver {
                override fun singleTapConfirmedHelper(p: GeoPoint?) = false
                override fun longPressHelper(p: GeoPoint?) = false
            })
            view.overlays.add(mapEventsOverlay)

            view.invalidate()
        },
        modifier = modifier
    )
}

private fun initOsm(context: Context) {
    Configuration.getInstance().load(
        context,
        context.getSharedPreferences("osm_prefs", Context.MODE_PRIVATE)
    )
    Configuration.getInstance().userAgentValue = context.packageName
}