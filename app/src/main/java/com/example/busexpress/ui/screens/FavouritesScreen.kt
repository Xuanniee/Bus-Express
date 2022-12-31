package com.example.busexpress.ui.screens

import android.annotation.SuppressLint
import android.util.Log
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
import com.example.busexpress.data.BusStopsInFavourites
import com.example.busexpress.data.FavouriteBusStop
import com.example.busexpress.data.FavouriteBusStopList
import com.example.busexpress.network.BusServicesRoute
import com.example.busexpress.network.BusStopValue
import com.example.busexpress.network.SingaporeBus
import com.example.busexpress.network.SingaporeBusServices
import com.example.busexpress.ui.component.BusStopComposable
import com.example.busexpress.ui.component.MenuSelection
import com.example.busexpress.ui.component.NestedMenuSelection
import com.example.busexpress.ui.favouriteBusStops.FavouriteBusStopViewModel
import kotlinx.coroutines.launch


@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun FavouritesScreen(
    favouriteBusStopViewModel: FavouriteBusStopViewModel,
    appViewModel: AppViewModel,
    busStopsInFavourites: BusStopsInFavourites,
    modifier: Modifier = Modifier,
) {
    // Variable to remember Tab Row
    var tapRowState by rememberSaveable { mutableStateOf(0) }
    val tapRowTitles = listOf("Going Out", "Coming Back")

    // Menu
    val menuSelection = remember { mutableStateOf(MenuSelection.NONE) }

    val singaporeBusGoingOutList = busStopsInFavourites.singaporeBusGoingOutList
    val singaporeBusComingBackList = busStopsInFavourites.singaporeBusComingBackList
    val busStopValueGoingOutList = busStopsInFavourites.busStopValueGoingOutList
    val busStopValueComingBackList = busStopsInFavourites.busStopValueComingBackList
    Log.d("Favourites", busStopsInFavourites.toString())

    val goingOutLength = singaporeBusGoingOutList.size - 1
    val comingBackLength = singaporeBusComingBackList.size - 1

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
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(all = 10.dp)
            ) {
                for (index in 0..goingOutLength) {
                    // UI Layer
                    Divider(thickness = 2.dp, modifier = modifier.padding(5.dp))

                    BusStopComposable(
                        busArrivalsJSON = singaporeBusGoingOutList[index],
                        busStopDetailsJSON = busStopValueGoingOutList[index],
                        busServiceBool = false,
                        modifier = modifier,
                        favouriteViewModel = favouriteBusStopViewModel,
                        appViewModel = appViewModel,
                        menuSelection = menuSelection
                    )

                    Divider(thickness = 2.dp, modifier = modifier.padding(5.dp))
                }
            }
        }
        // Coming Back [Favourites]
        else if (tapRowState == 1) {
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(all = 10.dp)
            ) {
                for (index in 0..comingBackLength) {
                    // UI Layer
                    Divider(thickness = 2.dp, modifier = modifier.padding(5.dp))

                    BusStopComposable(
                        busArrivalsJSON = singaporeBusComingBackList[index],
                        busStopDetailsJSON = busStopValueComingBackList[index],
                        busServiceBool = false,
                        modifier = modifier,
                        favouriteViewModel = favouriteBusStopViewModel,
                        appViewModel = appViewModel,
                        menuSelection = menuSelection
                    )

                    Divider(thickness = 2.dp, modifier = modifier.padding(5.dp))
                }
            }
        }
    }
}



