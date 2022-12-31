package com.example.busexpress.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Divider
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.busexpress.data.FavouriteBusStop
import com.example.busexpress.network.BusServicesRoute
import com.example.busexpress.ui.component.BusStopComposable
import com.example.busexpress.ui.favouriteBusStops.FavouriteBusStopViewModel


@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun FavouritesScreen(
    favouriteBusStopViewModel: FavouriteBusStopViewModel,
    viewModel: AppViewModel,
    busServicesRouteList: BusServicesRoute,
    modifier: Modifier = Modifier
) {
    // Variable to remember Tab Row
    var tapRowState by rememberSaveable { mutableStateOf(0) }
    val tapRowTitles = listOf("Going Out", "Coming Back")
    val goingOutFavouriteUiState by favouriteBusStopViewModel.goingOutUiState.collectAsState()

    Column() {
        // Navigation Bar for Going Out & Coming Back
        TabRow(
            selectedTabIndex = tapRowState,
            divider = {
                Divider(thickness = 3.dp)
            },
        ) {
            tapRowTitles.forEachIndexed { index, title ->
                Tab(
                    selected = (tapRowState == index),
                    onClick = {
                        tapRowState = index
                    },
                    text = { Text(title, fontWeight = FontWeight.SemiBold) },
                )
            }
        }

        if (tapRowState == 0) {
            // Call Function to get Favourites
            favouriteBusStopViewModel.retrieveFavouriteBusStops(true)
            }
        else if (tapRowState == 1) {
            favouriteBusStopViewModel.retrieveFavouriteBusStops(false)
        }
        // Retrieve Bus Stops
        val busStopList = goingOutFavouriteUiState.busStopList
        val busStopListLength = busStopList.size

        LazyColumn(
            modifier = modifier
                .fillMaxWidth()
                .padding(all = 10.dp)
        ) {
            items(busStopListLength) { index ->
                // Retrieve Current Bus Stop
                val currentBusStop = busStopList[index]?.favouriteBusStopCode
                // Use Bus Stop Code to get Bus Details and Stuff
                viewModel.determineUserQuery(currentBusStop.toString())

                // UI Layer
                Divider(thickness = 2.dp, modifier = modifier.padding(5.dp))

                BusStopComposable(
                    busArrivalsJSON = busServicesRouteList.busArrivalsJSONList[index],
                    busStopDetailsJSON = busServicesRouteList.busStopDetailsJSONList[index],
                    busServiceBool = false,
                    modifier = modifier,
                    favouriteViewModel = favouriteBusStopViewModel
                )

                Divider(thickness = 2.dp, modifier = modifier.padding(5.dp))
            }
        }
    }
}






