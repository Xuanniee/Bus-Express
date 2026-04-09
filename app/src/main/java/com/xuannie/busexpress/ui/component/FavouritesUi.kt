package com.xuannie.busexpress.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Route
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.xuannie.busexpress.ui.viewmodels.FavouriteBusStopViewModel
import kotlinx.coroutines.launch

/**
 * Composable Functions for User to edit the favourite bus stop
 */
//@Composable
//fun EditFavouriteBusStop() {
//
//}

enum class MenuSelection {
    FAVOURITIESOUT,
    FAVOURITIESBACK,
    NONE,
}

enum class NestedMenuSelection {
    FIRST,
    SECOND,
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

    // Favourite Button Logic Details
    val coroutineScope = rememberCoroutineScope()

    // Three Dot icon
    Box(
        modifier = Modifier
            .wrapContentSize(Alignment.TopEnd)
    ) {
        IconButton(
            onClick = {
                // Expand the main menu on three dots icon click
                // and hide the nested menu.
                expandedMain.value = true
//                expandedNested.value = false
            },
            colors = IconButtonDefaults.iconButtonColors(
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        ) {
            Icon(
                imageVector = Icons.Filled.MoreVert,
                contentDescription = "More Menu"
            )
        }

        /**
         * Composable for a small pop-up menu for Settings usually
         */
        DropdownMenu(
            expanded = expandedMain.value,
            onDismissRequest = { expandedMain.value = false },
//            containerColor = MaterialTheme.colorScheme.surfaceContainer,
//            tonalElevation = 6.dp,
//            shadowElevation = 8.dp
        ) {
            /**
             * For use with Nested Menu
             */
//            DropdownMenuItem(
//                onClick = {
//                    expandedMain.value = false // hide main menu
//                    expandedNested.value = true // show nested menu
//                    menuSelection.value = MenuSelection.NESTED
//                },
//                text = {
//                    Text("Nested Options \u25B6")
//                },
//                leadingIcon = {
//                    Icon(
//                        imageVector = Icons.Filled.KeyboardArrowRight,
//                        contentDescription = null
//                    )
//                }
//            )

            DropdownMenuItem(
                onClick = {
                    // Update the favouriteUiState to hold the current BusStopCode
                    favouriteViewModel.updateFavouriteUiState(
                        favouriteBusStopCode = currentBusStopCode,
                        goingOut = 0
                    )
                    coroutineScope.launch {
                        // Save it in the Database
                        favouriteViewModel.saveBusStop()
                    }

                    // Close Menu after Clicking
                    expandedMain.value = false
                    menuSelection.value = MenuSelection.FAVOURITIESOUT
                },
                text = {
                    Text(
                        text = "Add to Favourites [Going Out]",
                        style = MaterialTheme.typography.bodyLarge
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Route,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            )

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
            )

            DropdownMenuItem(
                onClick = {
                    // Update the favouriteUiState to hold the current BusStopCode
                    favouriteViewModel.updateFavouriteUiState(
                        favouriteBusStopCode = currentBusStopCode,
                        goingOut = 1
                    )
                    coroutineScope.launch {
                        // Save it in the Database
                        favouriteViewModel.saveBusStop()
                    }

                    // Close Menu after Clicking
                    expandedMain.value = false
                    menuSelection.value = MenuSelection.FAVOURITIESBACK
                },
                text = {
                    Text(
                        text = "Add to Favourites [Coming Back]",
                        style = MaterialTheme.typography.bodyLarge
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.DirectionsBus,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            )
        }
    }

//    NestedMenu(
//        expandedNested = expandedNested,
//        nestedMenuSelection = nestedMenuSelection
//    )
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
        onDismissRequest = { expandedNested.value = false },
//        containerColor = MaterialTheme.colorScheme.surfaceContainer,
//        tonalElevation = 6.dp,
//        shadowElevation = 8.dp
    ) {
        DropdownMenuItem(
            onClick = {
                // close nested menu
                expandedNested.value = false
                nestedMenuSelection.value = NestedMenuSelection.FIRST
            },
            text = {
                Text(
                    text = "First",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        )
        DropdownMenuItem(
            onClick = {
                // close nested menu
                expandedNested.value = false
                nestedMenuSelection.value = NestedMenuSelection.SECOND
            },
            text = {
                Text(
                    text = "Second",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        )
    }
}