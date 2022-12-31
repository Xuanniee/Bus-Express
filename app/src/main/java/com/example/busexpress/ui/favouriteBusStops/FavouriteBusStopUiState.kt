package com.example.busexpress.ui.favouriteBusStops

import com.example.busexpress.data.FavouriteBusStop

/**
 * Represents the UiState for a FavouriteBusStop
 */
data class FavouriteBusStopUiState(
    val id: Int = 0,
    val favouriteBusStopCode: String = "",
    val goingOutBusStop: Int = 0,
    val actionEnabled: Boolean = false,
)

/**
 * Extension Function to convert [FavouriteBusStopUiState] to [FavouriteBusStop]
 */
fun FavouriteBusStopUiState.toFavouriteBusStop(goingOutBusStop: Int = 0): FavouriteBusStop = FavouriteBusStop(
    favouriteBusStopCode = favouriteBusStopCode,
    goingOutBusStop = goingOutBusStop
)

/**
 * Extension Function to convert [FavouriteBusStop] to [FavouriteBusStopUiState]
 */
// The Ints are as good as Bools
fun FavouriteBusStop.toFavouriteBusStopUiState(actionEnabled: Boolean = false, goingOutBusStop: Int = 0): FavouriteBusStopUiState = FavouriteBusStopUiState(
    id = id,
    favouriteBusStopCode = favouriteBusStopCode,
    goingOutBusStop = goingOutBusStop,
    actionEnabled = actionEnabled
)

/**
 * Extension Function to ensure UiState is Valid
 */
fun FavouriteBusStopUiState.isValid(): Boolean {
    return favouriteBusStopCode.isNotBlank()
}

