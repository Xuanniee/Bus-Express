package com.example.busexpress.ui.screens

import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.busexpress.BusExpressScreen
import com.example.busexpress.R
import com.example.busexpress.network.*
import com.example.busexpress.ui.component.BusStopComposable
import com.example.busexpress.ui.component.MenuSelection
import com.example.busexpress.ui.favouriteBusStops.FavouriteBusStopViewModel
import kotlinx.coroutines.launch

@Composable
fun SearchScreen(
    busUiState: BusUiState,
    modifier: Modifier = Modifier,
    busArrivalsJson: SingaporeBus,
    busRoutes: BusRoutes,
    busServiceBool: Boolean,
    viewModel: AppViewModel,
    busStopDetails: BusStopValue,
    busServicesRouteList: BusServicesRoute,
    currentScreen: BusExpressScreen,
    favouriteBusStopViewModel: FavouriteBusStopViewModel,
    menuSelection: MutableState<MenuSelection>,
) {
    // Mutable State for User Input
    val userInput = remember {
        mutableStateOf(TextFieldValue(""))
    }
    // Focus Manager
    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(5.dp),
    ) {
        // Search Field for Bus Stop or Bus Numbers
        SearchView(
            label = R.string.search_field_instructions,
            state = userInput,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Search
            ),
            onKeyboardSearch = {
                coroutineScope.launch {
                    // Provide Search Feature based on Bus Stop Code or Bus Service Number
                    viewModel.determineUserQuery(userInput = userInput.value.text)
                }

                // Close the Onscreen Keyboard
                focusManager.clearFocus(true)
            },
            currentScreen = currentScreen
        )

        when(busUiState) {
            is BusUiState.Success -> {
                ResultScreen(
                    busStopDetails = busStopDetails,
                    busArrivalsJSON = busArrivalsJson,
                    busServiceBool = busServiceBool,
                    busServicesRouteList = busServicesRouteList,
                    busRoutes = busRoutes,
                    favouriteBusStopViewModel = favouriteBusStopViewModel,
                    appViewModel = viewModel,
                    menuSelection = menuSelection
                )
            }
            is BusUiState.Loading -> {
                LoadingScreen()
            }
            is BusUiState.Error -> {
                ErrorScreen()
            }
        }



    }

}

@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxSize()
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .fillMaxWidth()
        ) {
            Image(
                imageVector = Icons.Filled.Search,
                contentDescription = null,
                colorFilter = ColorFilter.tint(color = MaterialTheme.colors.surface),
                modifier = modifier.size(100.dp)
            )
            Text(
                text = stringResource(R.string.search_loading_screen_desc),
                modifier = modifier,
                style = MaterialTheme.typography.body1,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ErrorScreen(modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .fillMaxWidth()
        ) {
            Image(
                imageVector = Icons.Filled.Error,
                contentDescription = null,
                colorFilter = ColorFilter.tint(color = MaterialTheme.colors.surface),
                modifier = modifier.size(100.dp)
            )
            Text(
                text = stringResource(R.string.loading_failed_flavor_text),
                modifier = modifier,
                style = MaterialTheme.typography.body1,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * The home screen displaying result of fetching photos.
 */
@Composable
fun ResultScreen(
    busStopDetails: BusStopValue,
    busArrivalsJSON: SingaporeBus,
    busRoutes: BusRoutes,
    busServiceBool: Boolean,
    busServicesRouteList: BusServicesRoute,
    modifier: Modifier = Modifier,
    favouriteBusStopViewModel: FavouriteBusStopViewModel,
    appViewModel: AppViewModel,
    menuSelection: MutableState<MenuSelection>,
) = // Results of Search
    if (busServiceBool) {
        // Bus Services
        val busRouteArray = busRoutes.busRouteArray
        val busRouteArrayLength = busRouteArray.size
        val busRouteArrayMaxIndex = busRouteArrayLength - 1
        Log.d("debugTag", "Bus Route Array Size is $busRouteArrayLength")

        // Store the Routes in 2 Arrays
        val busRouteArray1 = mutableListOf<BusStopInRoute>()
        val busRouteArray2 = mutableListOf<BusStopInRoute>()
        for (i in 0..busRouteArrayMaxIndex) {
            if (busRouteArray[i].routeDirection == 1) {
                busRouteArray1.add(busRouteArray[i])
            }
            else {
                busRouteArray2.add(busRouteArray[i])
            }
        }

        val busRouteArray1Length = busRouteArray1.size
        val busRouteArray2Length = busRouteArray2.size
        // Mutable State to keep track of which Tab we are at
        var tapRowState by rememberSaveable { mutableStateOf(0) }
        val tapRowTitles = mutableListOf<String>(
            busServicesRouteList.busStopDetailsJSONList[busRouteArray1Length - 1].busStopDescription,
            busServicesRouteList.busStopDetailsJSONList.last().busStopDescription
        )

        // Ensure only 1 Tab if it is a Loop bus, i.e. the Start and End Bus Stop are the same
        if (tapRowTitles[0] == tapRowTitles[1]) {
            // Remove the Second Tab
            tapRowTitles.removeAt(1)
        }

        Column(
            modifier = modifier.fillMaxWidth()
        ) {
            // Navigation Bar for Going Out & Coming Back
            TabRow(
                selectedTabIndex = tapRowState,
                backgroundColor = MaterialTheme.colors.background,
                contentColor = Color.Black
            ) {
                tapRowTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = (tapRowState == index),
                        onClick = {
                            tapRowState = index
                        },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (tapRowState == index) FontWeight.ExtraBold else FontWeight.SemiBold
                            )
                        },
//                        selectedContentColor = MaterialTheme.colors.primary,
//                        unselectedContentColor = MaterialTheme.colors.secondary
                    )
                }
            }
            if (tapRowState == 0) {
                LazyColumn(
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(all = 10.dp)
                ) {
                    items(busRouteArray1Length) {index ->
                        Divider(thickness = 2.dp, modifier = modifier.padding(5.dp))

                        BusStopComposable(
                            busArrivalsJSON = busServicesRouteList.busArrivalsJSONList[index],
                            busStopDetailsJSON = busServicesRouteList.busStopDetailsJSONList[index],
                            busServiceBool = busServiceBool,
                            modifier = modifier,
                            favouriteViewModel = favouriteBusStopViewModel,
                            appViewModel = appViewModel,
                            menuSelection = menuSelection
                        )

                        Divider(thickness = 2.dp, modifier = modifier.padding(5.dp))
                    }
                }
            }
            else if (tapRowState == 1) {
                LazyColumn(
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(all = 10.dp)
                ) {
                    items(busRouteArray2Length) {index ->
                        Divider(thickness = 2.dp, modifier = modifier.padding(5.dp))

                        BusStopComposable(
                            busArrivalsJSON = busServicesRouteList.busArrivalsJSONList[index+busRouteArray1Length],
                            busStopDetailsJSON = busServicesRouteList.busStopDetailsJSONList[index+busRouteArray1Length],
                            busServiceBool = busServiceBool,
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
//        LazyColumn(
//            modifier = modifier
//                .fillMaxWidth()
//                .padding(all = 10.dp)
//        ) {
//            items(busRouteArray1Length) {index ->
//                Divider(thickness = 2.dp, modifier = modifier.padding(5.dp))
//
//                BusStopComposable(
//                    busArrivalsJSON = busServicesRouteList.busArrivalsJSONList[index],
//                    busStopDetailsJSON = busServicesRouteList.busStopDetailsJSONList[index],
//                    busServiceBool = busServiceBool,
//                    modifier = modifier
//                )
//
//                Divider(thickness = 2.dp, modifier = modifier.padding(5.dp))
//            }
//        }

    }
    else {
        // Bus Stop Code
        Divider(thickness = 2.dp, modifier = modifier.padding(5.dp))

        BusStopComposable(
            busArrivalsJSON = busArrivalsJSON,
            busStopDetailsJSON = busStopDetails,
            modifier = modifier,
            busServiceBool = busServiceBool,
            favouriteViewModel = favouriteBusStopViewModel,
            appViewModel = appViewModel,
            menuSelection = menuSelection
        )

        Divider(thickness = 2.dp, modifier = modifier.padding(5.dp))
    }


@Composable
fun SearchView(
    @StringRes label: Int,
    currentScreen: BusExpressScreen,
    state: MutableState<TextFieldValue>,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions,
    onKeyboardSearch: () -> Unit,
) {
    Column{
        Surface(
            modifier = modifier
                .padding(3.dp),
            shape = RoundedCornerShape(15),
            color =  MaterialTheme.colors.surface,
            elevation = 20.dp
        ) {
            TextField(
                value = state.value,
                onValueChange = {value ->
                    state.value = value
                },
                enabled = currentScreen == BusExpressScreen.Search,
                placeholder = {
                    if (state.value == TextFieldValue("")) {
                        Text(
                            stringResource(id = label),
                            modifier = Modifier
                                .fillMaxWidth(),
                            style = MaterialTheme.typography.body1,
                            color = MaterialTheme.colors.onPrimary,
                            maxLines = 1,
                        )
                    }
                },
                modifier = modifier
                    .fillMaxWidth()
                    .height(56.dp),
                singleLine = true,
                // Search Icon at the Start for Aesthetics
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = null,
                        modifier = Modifier.padding(10.dp)
                    )
                },
                // Cancel Button to delete all Input
                trailingIcon = {
                    // Icon appears iif the Search Field is not Empty
                    if (state.value != TextFieldValue("")) {
                        IconButton(onClick = {
                            // Clear the Search Field
                            state.value = TextFieldValue("")
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Delete all User Input",
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }
                },
                colors = TextFieldDefaults.textFieldColors(
                    cursorColor = MaterialTheme.colors.onPrimary,
                    focusedIndicatorColor = Color.Transparent,      // Refers to the Bottom Line in TextField
                    unfocusedIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    backgroundColor = MaterialTheme.colors.surface,
                    leadingIconColor = MaterialTheme.colors.onPrimary,
                    trailingIconColor = MaterialTheme.colors.onPrimary,
                    placeholderColor = MaterialTheme.colors.onPrimary
                ),
                keyboardActions = KeyboardActions(
                    onSearch = { onKeyboardSearch() }
                ),
                keyboardOptions = keyboardOptions,
                shape = RoundedCornerShape(25)
            )
        }

//        Row{
//            Spacer(modifier = modifier.weight(3f))
//            // Button for User to Click to begin Search
//            Button(
//                onClick = {
//                    // TODO Pass the User Query to the Search Function
//                    onKeyboardSearch()
//                },
//                modifier = modifier
//                    .align(Alignment.CenterVertically)
//                    .padding(2.dp)
//                    .weight(1f)
//                    .height(30.dp),
//            ) {
//                Text(
//                    text = stringResource(R.string.search_button_flavor_text),
//                    fontSize = 11.sp
//                )
//            }
//        }
    }

}


