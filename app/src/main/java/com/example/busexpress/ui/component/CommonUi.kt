package com.example.busexpress.ui.component

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.busexpress.R
import com.example.busexpress.network.*
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.*


@Composable
fun BusStopComposable(
    busArrivalsJSON: SingaporeBus,
    //busStopsJSON: BusStop,
    modifier: Modifier = Modifier
) {
    // Bus Arrival Timing Details
    val currentBusStopCode: String = busArrivalsJSON.busStopCode
    val currentBusStopServices: List<SingaporeBusServices> = busArrivalsJSON.services

    // Bus Stop Details
//    val currentBusStop = busStopsJSON
  //  val currentBusStopDetails: List<BusStopValue?> = currentBusStop.value

    // Store the Variable State if Bus is Expanded
    var expanded by remember { mutableStateOf(false) }

    Column {
        Row(
            modifier = modifier
                .fillMaxWidth()
        ) {
            // Toggleable Button to show all the Buses at the BusStop
            BusComposableExpandButton(
                expanded = expanded,
                onClick = {
                    expanded = !expanded
                }
            )

            // Name of Bus Stop & Bus Stop Code
            Column() {
                // Description of Bus Stop
                Text(
                    text = "Aft Punggol Road",
                    style = MaterialTheme.typography.h6
                )

                // Bus Stop Road & Code
                Text(
                    text = "TPE ($currentBusStopCode)",
                    style = MaterialTheme.typography.body1
                )

            }

            Spacer(modifier = modifier.weight(2f))

            // Refresh Icon
            BusComposableRefreshButton(
                onClick = {
                    // TODO Refresh
                }
            )
        }

        // Expanded Bus Services
        if (expanded) {
            LazyColumn(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(all = 10.dp)
            ) {
                items(currentBusStopServices) { currentBusStopService ->
                    ExpandedBusStop(currentBusStopService = currentBusStopService)
                }
            }

        }
    }



}

@Composable
fun BusComposableRefreshButton(
    onClick: () -> Unit
) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = Icons.Filled.Refresh,
            tint = MaterialTheme.colors.secondary,
            contentDescription = stringResource(R.string.refresh_busCard_desc)
        )
    }

}

@Composable
fun BusComposableExpandButton(
    expanded: Boolean,
    onClick: () -> Unit
) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
            tint = MaterialTheme.colors.secondary,
            contentDescription = stringResource(R.string.bus_stop_expand_more_desc)
        )

    }
}


@Composable
fun ExpandedBusStop(
    modifier: Modifier = Modifier,
    currentBusStopService: SingaporeBusServices,

) {
    // Determine the Current Timestamp as LocalDateTime
    var currentTimestamp = LocalDateTime.now() //.format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss"))
    val datetimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ")
//    datetimeFormatter.setTimeZone(TimeZone.getTimeZone("GMT+14"))
    var nextBusEtaArray = Array<String>(3) { "" }
    var nextBusOccupancyArray = Array<String>(3) { "" }

    val currentBusService = currentBusStopService.busServiceNumber


    for (i: Int in 0..2) {
        // Determining the ETA of the Next 3 Buses in Minutes
        val nextBus = currentBusStopService.nextBus1
        val nextBusTimestampString = nextBus.estimatedArrival  // In the Event there are no longer any buses
        val nextBusTimestamp = LocalDateTime.parse(nextBusTimestampString, datetimeFormatter)
        val nextBusETA = Duration.between(currentTimestamp, nextBusTimestamp)
        // Round down to Nearest Minute and Convert into a String
        nextBusEtaArray[i] = nextBusETA.toString().toInt().toString()

        // Determining the Occupancy Rates of the Next 3 Buses
        val nextBusOccupancy = nextBus.busOccupancyLevels
        nextBusOccupancyArray[i] = nextBusOccupancy
    }


    Divider(thickness = 2.dp)
    Row {
        // Bus Service Number
        Text(
            text = currentBusService,
            style = MaterialTheme.typography.h5,
            modifier = modifier.weight(2f)
        )

        Spacer(modifier = modifier.weight(1f))

        // Waiting Time + Occupancy Rate for each Incoming Bus
        Column() {
            Text(
                text = nextBusEtaArray[0],
                style = MaterialTheme.typography.body2
            )

            // TODO Replace with Infographic
            Text(
                text = nextBusOccupancyArray[0],
                style = MaterialTheme.typography.body2
            )

        }

        Column() {
            Text(
                text = nextBusEtaArray[1],
                style = MaterialTheme.typography.body2
            )

            // TODO Replace with Infographic
            Text(
                text = nextBusOccupancyArray[0],
                style = MaterialTheme.typography.body2
            )
        }

        Column() {
            Text(
                text = nextBusEtaArray[2],
                style = MaterialTheme.typography.body2
            )

            // TODO Replace with Infographic
            Text(
                text = nextBusOccupancyArray[0],
                style = MaterialTheme.typography.body2
            )
        }
    }
    Divider(thickness = 2.dp)

}




