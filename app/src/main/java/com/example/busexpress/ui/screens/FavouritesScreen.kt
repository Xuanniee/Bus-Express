package com.example.busexpress.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import com.example.busexpress.ui.component.BusStopComposable
import com.example.busexpress.ui.component.MenuSelection
import com.example.busexpress.ui.favouriteBusStops.FavouriteBusStopViewModel


@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun FavouritesScreen(
    favouriteBusStopViewModel: FavouriteBusStopViewModel,
    busStopsInFavourites: BusStopsInFavourites,
    appViewModel: AppViewModel,
    modifier: Modifier = Modifier,
) {
    // StateFlow Var
//    val favDetailsUiState = favouriteBusStopViewModel.favDetailUiState.collectAsState()

    // Variable to remember Tab Row
    var tapRowState by rememberSaveable { mutableStateOf(0) }
    val tapRowTitles = listOf("Going Out", "Coming Back")

    // Menu
    val menuSelection = remember { mutableStateOf(MenuSelection.NONE) }

    val singaporeBusGoingOutList = busStopsInFavourites.singaporeBusGoingOutList
    val singaporeBusComingBackList = busStopsInFavourites.singaporeBusComingBackList
    val busStopValueGoingOutList = busStopsInFavourites.busStopValueGoingOutList
    val busStopValueComingBackList = busStopsInFavourites.busStopValueComingBackList

    val goingOutLength = singaporeBusGoingOutList.size - 1
    val comingBackLength = singaporeBusComingBackList.size - 1

    Column {
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
                        menuSelection = menuSelection,
                        appViewModel = appViewModel
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
                        menuSelection = menuSelection,
                        appViewModel = appViewModel
                    )

                    Divider(thickness = 2.dp, modifier = modifier.padding(5.dp))
                }
            }
        }
    }
}



