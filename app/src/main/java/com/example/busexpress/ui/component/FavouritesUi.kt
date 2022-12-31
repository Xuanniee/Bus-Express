package com.example.busexpress.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.busexpress.ui.favouriteBusStops.FavouriteBusStopViewModel
import kotlinx.coroutines.launch

/**
 * Composable Functions for User to edit the favourite bus stop
 */
@Composable
fun EditFavouriteBusStop() {

}

enum class MenuSelection {
    ABOUT,
    SETTINGS,
    FAVOURITESOUT,
    FAVOURITESBACK,
    NESTED,
    NONE,
}

enum class NestedMenuSelection {
    FIRST,
    SECOND,
    DEFAULT
}

@Composable
fun BusComposableDropDownMenu(
    menuSelection: MutableState<MenuSelection>,
    favouriteViewModel: FavouriteBusStopViewModel,
    currentBusStopCode: String,
//    nestedMenuSelection: MutableState<NestedMenuSelection>
) {

    // Keep track if menu is open; By default, won't be open
    val expandedMain = remember { mutableStateOf(false) }
//    val expandedNested = remember { mutableStateOf(false) }

    // Three Dot icon
    Box(
        Modifier
            .wrapContentSize(Alignment.TopEnd)
    ) {
        IconButton(
            onClick = {
                // Expand the main menu on three dots icon click
                // and hide the nested menu.
                expandedMain.value = true
//                expandedNested.value = false
            }
        ) {
            Icon(
                Icons.Filled.MoreVert,
                contentDescription = "More Menu"
            )
        }
    }

    MainMenu(
        menuSelection = menuSelection,
        expandedMain = expandedMain,
        favouriteViewModel = favouriteViewModel,
        currentBusStopCode = currentBusStopCode,
//        expandedNested = expandedNested
    )
//    NestedMenu(
//        expandedNested = expandedNested,
//        nestedMenuSelection = nestedMenuSelection
//    )

}

/**
 * Composable for a small pop-up menu for Settings usually
 */
@Composable
fun MainMenu(
    menuSelection: MutableState<MenuSelection>,
    expandedMain: MutableState<Boolean>,
    favouriteViewModel: FavouriteBusStopViewModel,
    currentBusStopCode: String,
//    expandedNested: MutableState<Boolean>
) {
    // Favourite Button Logic Details
    val coroutineScope = rememberCoroutineScope()

    DropdownMenu(
        expanded = expandedMain.value,
        onDismissRequest = { expandedMain.value = false },
    ) {
        /**
         * For use with Nested Menu
         */
//        DropdownMenuItem(
//            onClick = {
//                expandedMain.value = false // hide main menu
//                expandedNested.value = true // show nested menu
//                menuSelection.value = MenuSelection.NESTED
//            }
//        ) {
//            Text("Nested Options \u25B6")
//        }

        Divider()

        DropdownMenuItem(
            onClick = {
                // Update the favouriteUiState to hold the current BusStopCode
                favouriteViewModel.updateFavouriteUiState(favouriteBusStopCode = currentBusStopCode, goingOut = 0)
                coroutineScope.launch {
                    // Save it in the Database
                    favouriteViewModel.saveBusStop()
                }

                // Close Menu after Clicking
                expandedMain.value = false
                menuSelection.value = MenuSelection.FAVOURITESOUT
            }
        ) {
            Text("Add to Favourites [Going Out]")
        }

        Divider()

        DropdownMenuItem(
            onClick = {
                // Update the favouriteUiState to hold the current BusStopCode
                favouriteViewModel.updateFavouriteUiState(favouriteBusStopCode = currentBusStopCode, goingOut = 1)
                coroutineScope.launch {
                    // Save it in the Database
                    favouriteViewModel.saveBusStop()
                }

                // Close Menu after Clicking
                expandedMain.value = false
                menuSelection.value = MenuSelection.FAVOURITESBACK
            }
        ) {
            Text("Add to Favourites [Coming Back]")
        }
    }
}

/**
 * Second Layer of Menu if Menu Composable on top is insufficient
 */
@Composable
fun NestedMenu(
    expandedNested: MutableState<Boolean>,
    nestedMenuSelection: MutableState<NestedMenuSelection>
) {
    DropdownMenu(
        expanded = expandedNested.value,
        onDismissRequest = { expandedNested.value = false }
    ) {
        DropdownMenuItem(
            onClick = {
                // close nested menu
                expandedNested.value = false
                nestedMenuSelection.value = NestedMenuSelection.FIRST
            }
        ) {
            Text("First")
        }
        DropdownMenuItem(
            onClick = {
                // close nested menu
                expandedNested.value = false
                nestedMenuSelection.value = NestedMenuSelection.SECOND
            }
        ) {
            Text("Second")
        }
    }
}

