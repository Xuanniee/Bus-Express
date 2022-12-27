package com.example.busexpress.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.busexpress.R
import com.example.busexpress.network.SingaporeBus
import com.example.busexpress.network.SingaporeBusServices
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@Composable
fun BusStopComposable(
    busArrivalsJSON: SingaporeBus,
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
            Column {
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
            if (!expanded) {
                BusComposableMenuButton (
                    onClick = {
                        // TODO Menu Button
                    }
                )
            }
            else {
                // Appears only iif not Expanded
                BusComposableRefreshButton(
                    onClick = {
                        // TODO Refresh
                    }
                )
            }
        }

        // Expanded Bus Services
        if (expanded) {
            LazyColumn(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(all = 10.dp)
            ) {
                items(currentBusStopServices) { currentBusStopService ->
                    ExpandedBusStop(currentBusStopService = currentBusStopService, modifier = Modifier.padding(3.dp))
                }
            }

        }
    }



}

@Composable
fun BusComposableMenuButton(
    onClick: () -> Unit
) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = Icons.Filled.MoreVert,
            tint = MaterialTheme.colors.secondary,
            contentDescription = stringResource(R.string.bus_composable_menu_desc)
        )
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
    val currentTimestamp = LocalDateTime.now()
    val datetimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZZZZZ")
    val nextBusEtaArray = Array(3) { "" }
    val nextBusOccupancyArray = Array(3) { 0 }
    val nextBusOccupancyDescArray = Array(3) { 0 }
    val nextBusWheelchairArray = Array(3) { 0 }
    val nextBusTypeArray = Array(3) { 0 }

    // Array holding the Next 3 Bus Objects
    val nextBusArray = arrayListOf(
        currentBusStopService.nextBus1,
        currentBusStopService.nextBus2,
        currentBusStopService.nextBus3
    )

    val currentBusService = currentBusStopService.busServiceNumber

    // For Loop for the Next 3 Buses
    for (i: Int in 0..2) {
        // Determining the ETA of the Next 3 Buses in Minutes
        val nextBus = nextBusArray[i]
        val nextBusTimestampString = nextBus.estimatedArrival  // In the Event there are no longer any buses

        // Check if Bus Services are still available
        if (nextBusTimestampString == "") {
            // Unavailable Bus Service
            nextBusEtaArray[i] = "NA"
        }
        else {
            // Round down to Nearest Minute and Convert into a String
            val nextBusTimestamp = LocalDateTime.parse(nextBusTimestampString, datetimeFormatter)
            val nextBusETA = Duration.between(currentTimestamp, nextBusTimestamp).toMinutes()
            // If less than a minute, change to Arriving
            if (nextBusETA < 1) {
                nextBusEtaArray[i] = "Arr"
            }
            else {
                nextBusEtaArray[i] = nextBusETA.toString()
            }

        }

        // Determining the Occupancy Rates of the Next 3 Buses
        when (nextBus.busOccupancyLevels) {
            "SEA" -> {
                nextBusOccupancyArray[i] = R.drawable.seats_available_img
                nextBusOccupancyDescArray[i] = R.string.seats_avail_content_desc
            }
            "SDA" -> {
                nextBusOccupancyArray[i] = R.drawable.standing_available_img
                nextBusOccupancyDescArray[i] = R.string.standing_avail_content_desc
            }
            "LSD" -> {
                nextBusOccupancyArray[i] = R.drawable.limited_standing_img
                nextBusOccupancyDescArray[i] = R.string.limited_standing_content_desc
            }
            // No Bus Services
            else -> {
                nextBusOccupancyArray[i] = 0
                nextBusOccupancyDescArray[i] = 0
            }
        }

        // Determine Type of Bus
        when (nextBus.vehicleType) {
            "SD" -> {
                nextBusTypeArray[i] = R.string.single_deck_bus_type
            }
            "DD" -> {
                nextBusTypeArray[i] = R.string.double_deck_bus_type
            }
            "BD" -> {
                nextBusTypeArray[i] = R.string.bendy_bus_type
            }
            // No Bus Service incoming
            else -> {
                nextBusTypeArray[i] = 0
            }
        }

        // Determining if Wheelchair Accessible
        val nextBusWheelchair = nextBus.wheelchairAccessible
        if (nextBusWheelchair == "WAB") {
            // Supports Wheelchair
            nextBusWheelchairArray[i] = R.drawable.wheelchair_accessible_bus
        }
        else {
            nextBusWheelchairArray[i] = 0
        }

    }


    Divider(
        thickness = 2.dp,
        modifier = modifier.padding(1.dp)
    )

    Row {
        // Bus Service Number
        Text(
            text = currentBusService,
            style = MaterialTheme.typography.h5,
            modifier = modifier.weight(2f),
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = modifier.weight(1f))

        // Waiting Time + Occupancy Rate for each Incoming Bus
        Column(
            modifier = modifier.weight(1f)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = nextBusEtaArray[0],
                    style = MaterialTheme.typography.body2,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // TODO Replace with Infographic
            if (nextBusOccupancyArray[0] != 0) {
                Image(
                    painter = painterResource(id = nextBusOccupancyArray[0]),
                    contentDescription = stringResource(id = nextBusOccupancyDescArray[0])
                )
            }

            // Wheelchair + Bus Type
            Row {
                // Bus Type
                if (nextBusTypeArray[0] != 0) {
                    Text(
                        text = stringResource(id = nextBusTypeArray[0]),
                        style = MaterialTheme.typography.body2,
                        modifier = modifier.weight(2f),
                        maxLines = 1,
                        fontSize = 8.sp
                    )
                }

                if (nextBusWheelchairArray[0] != 0) {
                    // Wheelchair Exists
                    Image(
                        painter = painterResource(id = nextBusWheelchairArray[0]),
                        contentDescription = "Bus is wheelchair accessible.",
                        modifier = modifier.weight(1f)
                    )
                }
            }

        }

        Column(
            modifier = modifier.weight(1f)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = nextBusEtaArray[1],
                    style = MaterialTheme.typography.body2,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // TODO Replace with Infographic
            if (nextBusOccupancyArray[1] != 0) {
                Image(
                    painter = painterResource(id = nextBusOccupancyArray[1]),
                    contentDescription = stringResource(id = nextBusOccupancyDescArray[1])
                )
            }

            // Wheelchair + Bus Type
            Row {
                // Bus Type
                if (nextBusTypeArray[1] != 0) {
                    Text(
                        text = stringResource(id = nextBusTypeArray[1]),
                        style = MaterialTheme.typography.body2,
                        modifier = modifier.weight(2f),
                        maxLines = 1,
                        fontSize = 8.sp
                    )
                }

                if (nextBusWheelchairArray[1] != 0) {
                    // Wheelchair Exists
                    Image(
                        painter = painterResource(id = nextBusWheelchairArray[1]),
                        contentDescription = "Bus is wheelchair accessible.",
                        modifier = modifier.weight(1f)
                    )
                }
            }
        }

        Column(
            modifier = modifier.weight(1f)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = nextBusEtaArray[2],
                    style = MaterialTheme.typography.body2,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // TODO Replace with Infographic
            if (nextBusOccupancyArray[2] != 0) {
                Image(
                    painter = painterResource(id = nextBusOccupancyArray[2]),
                    contentDescription = stringResource(id = nextBusOccupancyDescArray[2])
                )
            }

            // Wheelchair + Bus Type
            Row(
            ) {
                // Bus Type
                if (nextBusTypeArray[2] != 0) {
                    Text(
                        text = stringResource(id = nextBusTypeArray[2]),
                        style = MaterialTheme.typography.body2,
                        modifier = modifier.weight(2f),
                        maxLines = 1,
                        fontSize = 8.sp
                    )
                }

                if (nextBusWheelchairArray[2] != 0) {
                    // Wheelchair Exists
                    Image(
                        painter = painterResource(id = nextBusWheelchairArray[2]),
                        contentDescription = "Bus is wheelchair accessible.",
                        modifier = modifier.weight(1f)
                    )
                }
            }
        }
    }

    Divider(
        thickness = 2.dp,
        modifier = modifier.padding(1.dp)
    )
}




