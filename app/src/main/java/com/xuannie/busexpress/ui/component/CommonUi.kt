package com.xuannie.busexpress.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xuannie.busexpress.R
import com.xuannie.busexpress.network.*
import com.xuannie.busexpress.ui.viewmodels.FavouriteBusStopViewModel
import com.xuannie.busexpress.ui.viewmodels.AppViewModel
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun BusStopComposable(
    busArrivalsJSON: SingaporeBus,
    busStopDetailsJSON: BusStopValue,
    busServiceBool: Boolean,
    favouriteViewModel: FavouriteBusStopViewModel,
    menuSelection: MutableState<MenuSelection>,
    appViewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    // Bus Arrival Timing Details
    val currentBusStopCode: String = busArrivalsJSON.busStopCode
    val currentBusStopServices: List<SingaporeBusServices> = busArrivalsJSON.services

    // Store the Variable State if Bus is Expanded
    var expanded by rememberSaveable { mutableStateOf(false) }

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
                    text = busStopDetailsJSON.busStopDescription,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )

                // Bus Stop Road & Code
                Text(
                    text = "${busStopDetailsJSON.busStopRoadName} ($currentBusStopCode)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )

            }

            Spacer(modifier = modifier.weight(2f))

            // Button to add Bus Stop to Favourites
            if (!expanded) {
                BusComposableDropDownMenu(
                    menuSelection = menuSelection,
                    favouriteViewModel = favouriteViewModel,
                    currentBusStopCode = currentBusStopCode
                )
            }
            // Refresh Icon
            else {
                // Appears only iif not Expanded
                BusComposableRefreshButton(
                    onClick = {
                        // Refresh for the particular Bus Stop
                        appViewModel.determineUserQuery(currentBusStopCode)

                        // TODO Open the Bus Stop again for User

                    }
                )
            }
        }

        // Expanded Bus Services
        if (expanded) {
            if (busServiceBool) {
                // All Bus Stops in Route
                val currentBusStopServicesLength = currentBusStopServices.size - 1
                Column(
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(all = 10.dp)
                ) {
                    for (index in 0..currentBusStopServicesLength) {
                        ExpandedBusStop(currentBusStopService = currentBusStopServices[index], modifier = Modifier.padding(3.dp))
                    }

                }
            }
            else {
                // Only 1 Bus Stop
                Column(
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(all = 10.dp)
                ) {
                    currentBusStopServices.forEach { currentBusStopService ->
                        ExpandedBusStop(
                            currentBusStopService = currentBusStopService,
                            modifier = Modifier.padding(3.dp)
                        )
                    }
                }
//                LazyColumn(
//                    modifier = modifier
//                        .fillMaxWidth()
//                        .padding(all = 10.dp)
//                ) {
//                    // Shows Arrival Timing when User provides Bus Stop Code
//                    items(currentBusStopServices) { currentBusStopService ->
//                        ExpandedBusStop(
//                            currentBusStopService = currentBusStopService,
//                            modifier = Modifier.padding(3.dp)
//                        )
//                    }
//                }
            }
        }
    }
}

//@Composable
//fun BusComposableMenuButton(
//    onClick: () -> Unit
//) {
//    IconButton(onClick = onClick) {
//        Icon(
//            imageVector = Icons.Filled.Favorite,
//            tint = MaterialTheme.colors.secondary,
//            contentDescription = stringResource(R.string.bus_composable_menu_desc)
//        )
//    }
//
//}

@Composable
fun BusComposableRefreshButton(
    onClick: () -> Unit
) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = Icons.Filled.Refresh,
            tint = MaterialTheme.colorScheme.primary,
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
            tint = MaterialTheme.colorScheme.primary,
            contentDescription = stringResource(R.string.bus_stop_expand_more_desc)
        )

    }
}

@Composable
fun ArrivalColumn(
    eta: String,
    occupancyImage: Int,
    occupancyDesc: Int,
    busType: Int,
    wheelchairIcon: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(1.dp)
    ) {
        Text(
            text = eta,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold
        )

        if (occupancyImage != 0) {
            Image(
                painter = painterResource(id = occupancyImage),
                contentDescription = stringResource(id = occupancyDesc),
                modifier = Modifier
                    .width(28.dp)
                    .height(8.dp)
            )
        } else {
            Spacer(
                modifier = Modifier
                    .width(28.dp)
                    .height(8.dp)
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (busType != 0) {
                Text(
                    text = stringResource(id = busType),
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 7.sp,
                    maxLines = 1
                )
            }

            if (wheelchairIcon != 0) {
                Spacer(modifier = Modifier.width(2.dp))
                Image(
                    painter = painterResource(id = wheelchairIcon),
                    contentDescription = "Bus is wheelchair accessible.",
                    modifier = Modifier.size(12.dp)
                )
            }
        }
    }
}

@Composable
fun ExpandedBusStop(
    modifier: Modifier = Modifier,
    currentBusStopService: SingaporeBusServices,
) {
    val nextBusArray = listOf(
        currentBusStopService.nextBus1,
        currentBusStopService.nextBus2,
        currentBusStopService.nextBus3
    )

    val nextBusEtaArray = determineTimeArrival(nextBusArray)
    val occupancyResults = determineOccupancyBus(nextBusArray)
    val nextBusOccupancyArray = occupancyResults.nextBusOccupancyArray
    val nextBusOccupancyDescArray = occupancyResults.nextBusOccupancyDescArray
    val nextBusWheelchairArray = determineWheelchairAccessibility(nextBusArray)
    val nextBusTypeArray = determineTypeBus(nextBusArray)

    HorizontalDivider(
        thickness = 1.dp,
        modifier = Modifier.padding(vertical = 1.dp),
        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = currentBusStopService.busServiceNumber,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1.2f),
            maxLines = 1
        )

        ArrivalColumn(
            eta = nextBusEtaArray[0],
            occupancyImage = nextBusOccupancyArray[0],
            occupancyDesc = nextBusOccupancyDescArray[0],
            busType = nextBusTypeArray[0],
            wheelchairIcon = nextBusWheelchairArray[0],
            modifier = Modifier.weight(1f)
        )

        ArrivalColumn(
            eta = nextBusEtaArray[1],
            occupancyImage = nextBusOccupancyArray[1],
            occupancyDesc = nextBusOccupancyDescArray[1],
            busType = nextBusTypeArray[1],
            wheelchairIcon = nextBusWheelchairArray[1],
            modifier = Modifier.weight(1f)
        )

        ArrivalColumn(
            eta = nextBusEtaArray[2],
            occupancyImage = nextBusOccupancyArray[2],
            occupancyDesc = nextBusOccupancyDescArray[2],
            busType = nextBusTypeArray[2],
            wheelchairIcon = nextBusWheelchairArray[2],
            modifier = Modifier.weight(1f)
        )
    }

    HorizontalDivider(
        thickness = 1.dp,
        modifier = Modifier.padding(vertical = 1.dp),
        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
    )
}

/**
 * Helper Function to help determine various features of Bus Arrivals provided by LTA Datamall API
 */
// Determine Time Arriving for Buses
fun determineTimeArrival(
    nextBusArray: List<NextBusTiming>
): Array<String> {
    // Determine the Current Timestamp as LocalDateTime
    val currentTimestamp = LocalDateTime.now()
    val datetimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZZZZZ")
    val nextBusEtaArray = Array(3) { "" }

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
    }
    return nextBusEtaArray
}

// Determine Occupancy Rate of Buses
fun determineOccupancyBus(
    nextBusArray: List<NextBusTiming>
): BusOccupancyResults {
    val nextBusOccupancyArray = Array(3) { 0 }
    val nextBusOccupancyDescArray = Array(3) { 0 }

    // For Loop for the Next 3 Buses
    for (i: Int in 0..2) {
        // Determining the ETA of the Next 3 Buses in Minutes
        val nextBus = nextBusArray[i]
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
    }

    return BusOccupancyResults(nextBusOccupancyArray = nextBusOccupancyArray, nextBusOccupancyDescArray = nextBusOccupancyDescArray)
}

// Determine Type of Bus
fun determineTypeBus(
    nextBusArray: List<NextBusTiming>
): Array<Int> {
    val nextBusTypeArray = Array(3) { 0 }

    // For Loop for the Next 3 Buses
    for (i: Int in 0..2) {
        // Determining the ETA of the Next 3 Buses in Minutes
        val nextBus = nextBusArray[i]

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
    }
    return nextBusTypeArray
}

// Determine Wheelchair Accessibility
fun determineWheelchairAccessibility(
    nextBusArray: List<NextBusTiming>
): Array<Int> {
    val nextBusWheelchairArray = Array(3) { 0 }

    for (i: Int in 0..2) {
        // Determining the ETA of the Next 3 Buses in Minutes
        val nextBus = nextBusArray[i]

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
    return nextBusWheelchairArray
}


