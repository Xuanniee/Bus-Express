package com.xuannie.busexpress.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.xuannie.busexpress.BusExpressScreen
import com.xuannie.busexpress.R
import com.xuannie.busexpress.network.*
import com.xuannie.busexpress.ui.component.BusStopComposable
import com.xuannie.busexpress.ui.component.MenuSelection
import com.xuannie.busexpress.ui.component.NearbyHeaderBar
import com.xuannie.busexpress.ui.utils.getCurrentUserLocation
import com.xuannie.busexpress.ui.viewmodels.AppViewModel
import com.xuannie.busexpress.ui.viewmodels.BusUiState
import com.xuannie.busexpress.ui.viewmodels.FavouriteBusStopViewModel
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
    val context = LocalContext.current

    fun loadNearbyUsingRealLocation() {
        getCurrentUserLocation(context) { location ->
            if (location != null) {
                viewModel.loadNearbyStops(
                    userLatitude = location.latitude,
                    userLongitude = location.longitude
                )
            } else {
                // fallback if location fails
                viewModel.loadNearbyStops(1.4053, 103.9023)
            }
        }
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (fineGranted || coarseGranted) {
            loadNearbyUsingRealLocation()
        } else {
            // fallback if user denies permission
            viewModel.loadNearbyStops(1.4053, 103.9023)
        }
    }

    LaunchedEffect(Unit) {
        if (userInput.value.text.isBlank()) {
            val fineGranted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

            val coarseGranted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

            if (fineGranted || coarseGranted) {
                loadNearbyUsingRealLocation()
            } else {
                locationPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(5.dp),
    ) {
        NearbyHeaderBar()

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
            currentScreen = currentScreen,
            onClearSearch = {
                loadNearbyUsingRealLocation()
            }
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
                    menuSelection = menuSelection,
                )
            }
            is BusUiState.NearbySuccess -> {
                NearbyStopsScreen(
                    nearbyStops = busUiState.nearbyStops,
                    nearbyArrivals = busUiState.nearbyArrivals,
                    favouriteBusStopViewModel = favouriteBusStopViewModel,
                    menuSelection = menuSelection,
                    appViewModel = viewModel
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
                colorFilter = ColorFilter.tint(
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
                ),
                modifier = modifier.size(100.dp)
            )
            Text(
                text = stringResource(R.string.search_loading_screen_desc),
                modifier = modifier,
                style = MaterialTheme.typography.bodyLarge,
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
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.error.copy(alpha = 0.75f)),
                modifier = modifier.size(100.dp)
            )
            Text(
                text = stringResource(R.string.loading_failed_flavor_text),
                modifier = modifier,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * The home screen displaying result of fetching photos.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    busStopDetails: BusStopValue,
    busArrivalsJSON: SingaporeBus,
    busRoutes: BusRoutes,
    busServiceBool: Boolean,
    busServicesRouteList: BusServicesRoute,
    modifier: Modifier = Modifier,
    favouriteBusStopViewModel: FavouriteBusStopViewModel,
    menuSelection: MutableState<MenuSelection>,
    appViewModel: AppViewModel
) {
    // Results of Search
    if (busServiceBool) {
        // Bus Services
        val busRouteArray = busRoutes.busRouteArray
        val busRouteArrayLength = busRouteArray.size
        val busRouteArrayMaxIndex = busRouteArrayLength - 1

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
            PrimaryTabRow(
                selectedTabIndex = tapRowState,
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.primary,
                divider = {
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f),
                        thickness = 1.dp
                    )
                },
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
                        HorizontalDivider(
                            thickness = 2.dp,
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.18f),
                            modifier = modifier.padding(5.dp)
                        )

                        BusStopComposable(
                            busArrivalsJSON = busServicesRouteList.busArrivalsJSONList[index],
                            busStopDetailsJSON = busServicesRouteList.busStopDetailsJSONList[index],
                            busServiceBool = busServiceBool,
                            modifier = modifier,
                            favouriteViewModel = favouriteBusStopViewModel,
                            menuSelection = menuSelection,
                            appViewModel = appViewModel
                        )

                        HorizontalDivider(
                            thickness = 2.dp,
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.18f),
                            modifier = modifier.padding(5.dp)
                        )
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
                        HorizontalDivider(
                            thickness = 2.dp,
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.18f),
                            modifier = modifier.padding(5.dp)
                        )

                        BusStopComposable(
                            busArrivalsJSON = busServicesRouteList.busArrivalsJSONList[index+busRouteArray1Length],
                            busStopDetailsJSON = busServicesRouteList.busStopDetailsJSONList[index+busRouteArray1Length],
                            busServiceBool = busServiceBool,
                            modifier = modifier,
                            favouriteViewModel = favouriteBusStopViewModel,
                            menuSelection = menuSelection,
                            appViewModel = appViewModel
                        )

                        HorizontalDivider(
                            thickness = 2.dp,
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.18f),
                            modifier = modifier.padding(5.dp)
                        )
                    }
                }
            }
        }

    }
    else {
        // Bus Stop Code
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp)
        ) {
            item {
                HorizontalDivider(
                    thickness = 2.dp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.18f),
                    modifier = Modifier.padding(5.dp)
                )

                BusStopComposable(
                    busArrivalsJSON = busArrivalsJSON,
                    busStopDetailsJSON = busStopDetails,
                    busServiceBool = busServiceBool,
                    favouriteViewModel = favouriteBusStopViewModel,
                    menuSelection = menuSelection,
                    appViewModel = appViewModel
                )

                HorizontalDivider(
                    thickness = 2.dp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.18f),
                    modifier = Modifier.padding(5.dp)
                )
            }
        }
    }
}


@Composable
fun SearchView(
    @StringRes label: Int,
    currentScreen: BusExpressScreen,
    state: MutableState<TextFieldValue>,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions,
    onKeyboardSearch: () -> Unit,
    onClearSearch: () -> Unit,
) {
    Column{
        Surface(
            modifier = modifier
                .padding(3.dp),
            shape = RoundedCornerShape(15),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            shadowElevation = 6.dp
        ) {
            TextField(
                value = state.value,
                onValueChange = {value ->
                    state.value = value
                },
                enabled = currentScreen == BusExpressScreen.Default,
                placeholder = {
                    if (state.value == TextFieldValue("")) {
                        Text(
                            stringResource(id = label),
                            modifier = Modifier
                                .fillMaxWidth(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
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
                            onClearSearch()
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Delete all User Input",
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }
                },
                colors = TextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    cursorColor = MaterialTheme.colorScheme.primary,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                    unfocusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                    disabledIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                    focusedLeadingIconColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurface,
                    disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    focusedTrailingIconColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTrailingIconColor = MaterialTheme.colorScheme.onSurface,
                    disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    focusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                keyboardActions = KeyboardActions(
                    onSearch = { onKeyboardSearch() }
                ),
                keyboardOptions = keyboardOptions,
                shape = RoundedCornerShape(25)
            )
        }
    }

}


