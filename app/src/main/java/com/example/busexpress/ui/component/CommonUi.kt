package com.example.busexpress.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.busexpress.R
import com.example.busexpress.network.BusStop
import com.example.busexpress.network.BusStopValue
import com.example.busexpress.network.SingaporeBus
import com.example.busexpress.network.SingaporeBusServices



@Composable
fun BusStopComposable(
    busArrivalsJSON: SingaporeBus,
    busStopsJSON: BusStop,
    modifier: Modifier = Modifier
) {
    val currentBusStopCode = busArrivalsJSON.busStopCode
    val currentBusStopServices: List<SingaporeBusServices?> = busArrivalsJSON.services

    // Bus Stop Details
    val currentBusStop = busStopsJSON
    val currentBusStopDetails: List<BusStopValue?> = currentBusStop.value


    Row() {
        // Toggable Button to show all the Buses at the BusStop
        IconButton(onClick = {
            // TODO Insert Logic to expand
        }) {
            Icon(imageVector = Icons.Filled.MoreVert, contentDescription = stringResource(R.string.bus_stop_expand_more_desc))

        }

        // Name of Bus Stop & Bus Stop Code
        Column() {
            // Description of Bus Stop
            Text(
                text = ,
            )

            // Bus Stop Road & Code


        }

        // Refresh Icon
        IconButton(onClick = {
            // TODO Insert Logic to Refresh
        }) {
            Icon(imageVector = Icons.Filled.Refresh, contentDescription = "Refresh for updated bus timings")

        }
    }

}



