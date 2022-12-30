package com.example.busexpress

import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.twotone.Email
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.busexpress.network.*
import com.example.busexpress.ui.screens.*
import com.example.busexpress.ui.theme.Grey900
import com.example.busexpress.ui.theme.NavigationDrawer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

// Enum Class for App Routes
enum class BusExpressScreen(@StringRes val title: Int) {
    Default(title = R.string.app_name),
    FavouritesAway(title = R.string.favourites_away),
    FavouritesBack(title = R.string.favourites_back),
    Nearby(title = R.string.nearby),
    Search(title = R.string.search)
}


/**
 * Composable that displays the topBar and displays a navigation menu
 */
@Composable
fun BusExpressAppTopBar(
    modifier: Modifier = Modifier,
    currentScreen: BusExpressScreen,
    scope: CoroutineScope,
    scaffoldState: ScaffoldState
) {
    // TODO Add a Logo Next Time
    TopAppBar(
        title = { Text(stringResource(id = currentScreen.title)) },
        modifier = modifier,
        navigationIcon =  {
            IconButton(onClick = {
                scope.launch { scaffoldState.drawerState.open() }
            }) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = stringResource(R.string.navbar_description)
                )
            }
        },
        actions = {
            // Already in a RowScope, so will be placed Horizontally
            IconButton(onClick = { /*TODO DARK MODE*/ }) {
                Icon(
                    imageVector = Icons.Filled.DarkMode,
                    contentDescription = stringResource(R.string.dark_mode_description)
                )

            }
        },
        elevation = 20.dp
    )
}

@Composable
fun BusExpressNavigationDrawer(
    modifier: Modifier = Modifier,
    scope: CoroutineScope,
    navController: NavHostController,
    scaffoldState: ScaffoldState
) {
    Column(
        modifier = modifier
            .fillMaxWidth()

    ) {
        // Headline Description
        // TODO Change with App Logo
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                imageVector = Icons.TwoTone.Email,
                contentDescription = null,
                modifier = modifier
                    .weight(1f)
            )

            Spacer(modifier = modifier.weight(5f))

            // Icon to close the Navigation Drawer
            IconButton(
                onClick = {
                scope.launch { scaffoldState.drawerState.close() }
                },
                modifier = modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = stringResource(R.string.navigation_drawer_close_flavor_text)
                )

            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = stringResource(id = R.string.app_name),
            style = MaterialTheme.typography.h6,
            modifier = modifier.padding(
                start = 5.dp,
                end = 5.dp,
                top = 5.dp,
                bottom = 1.dp
            )
        )

        Text(
            text = stringResource(R.string.navigation_flavor_text),
            style = MaterialTheme.typography.body2,
            modifier = modifier.padding(5.dp)
        )

        Spacer(modifier = Modifier.height(5.dp))

        Divider(
            thickness = 2.dp,
            color = Grey900,
            modifier = modifier.padding(2.dp)
        )

        /**
         *  Navigation Options / Buttons
         */
        // Home
        Button(
            onClick = {
                // Navigate to the Desired Route
                navController.navigate(BusExpressScreen.Default.name)
                // Close the App Drawer
                scope.launch { scaffoldState.drawerState.close() }
            },
            modifier = modifier
                .fillMaxWidth()
                .padding(all = 5.dp)
        ) {
            Image(
                imageVector = Icons.Filled.Home,
                contentDescription = null
            )
            Text(stringResource(R.string.home_navigation_desc))
        }
        // Search
        Button(
            onClick = {
                navController.navigate(BusExpressScreen.Search.name)
                scope.launch { scaffoldState.drawerState.close() }
            },
            modifier = modifier
                .fillMaxWidth()
                .padding(all = 5.dp)
        ) {
            Image(
                imageVector = Icons.Filled.Search,
                contentDescription = stringResource(R.string.search_nav_desc)
            )
            Text(stringResource(id = R.string.search_nav_desc))
        }

        // Favourites
        Button(
            onClick = {
                navController.navigate(BusExpressScreen.FavouritesAway.name)
                scope.launch { scaffoldState.drawerState.close() }
            },
            modifier = modifier
                .fillMaxWidth()
                .padding(all = 5.dp)
        ) {
            Image(
                imageVector = Icons.Filled.Favorite,
                contentDescription = null
            )
            Text(stringResource(R.string.favourites_navigation_desc))
        }

        Button(
            onClick = {
                navController.navigate(BusExpressScreen.Nearby.name)
                scope.launch { scaffoldState.drawerState.close() }
            },
            modifier = modifier
                .fillMaxWidth()
                .padding(all = 5.dp)
        ) {
            Image(
                imageVector = Icons.Filled.LocationOn,
                contentDescription = null
            )
            Text(text = stringResource(R.string.nearby_navigation_desc))
        }

        // Easter Egg Button
//        Button(
//            onClick = {
//
//            },
//            modifier = modifier
//        ) {
//            Image(
//                imageVector = Icons.Filled,
//                contentDescription =
//            )
//
//        }

    }

}

@Composable
fun BusExpressApp(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    viewModel: AppViewModel = viewModel()
) {
    // Save Current Back Stack Entry
    val backStackEntry by navController.currentBackStackEntryAsState()
    // Name of Current Screen as a Variable
    val currentScreen = BusExpressScreen.valueOf(
        backStackEntry?.destination?.route ?: BusExpressScreen.Default.name
    )
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()

    // Top Navigation Bar
    Scaffold(
        modifier = modifier,
        scaffoldState = scaffoldState,
        drawerContent = {
            // TODO Add a Composable for Navigation
            BusExpressNavigationDrawer(
                navController = navController,
                scope = scope,
                scaffoldState = scaffoldState
            )
        },
        drawerElevation = 20.dp,
        drawerShape = NavigationDrawer,
        drawerGesturesEnabled = true,
        topBar = {
            BusExpressAppTopBar(
                currentScreen = currentScreen,
                scaffoldState = scaffoldState,
                scope = scope
            )
        }
    ) { innerPadding ->
        // Holding the API Call Data here is better so I can pass it to multiple screens
        val busServiceUiState by viewModel.busServiceUiState.collectAsState()
        val busStopNameUiState by viewModel.busStopNameUiState.collectAsState()
        val busRouteUiState by viewModel.busRouteUiState.collectAsState()
        val multipleBusUiState by viewModel.multipleBusUiState.collectAsState()
        val multipleBusStopNameUiState by viewModel.multipleBusStopNameUiState.collectAsState()

        // State Variables
        val busServiceBoolUiState = viewModel.busServiceBoolUiState
        Log.d("debug1", "$busServiceBoolUiState")

        // NavHost Composable for Navigating between Screens
        NavHost(
            navController = navController,
            startDestination = BusExpressScreen.Search.name,
            modifier = modifier.padding(innerPadding)
        ) {
            // Routes for Every Screen in the App

            // 1. Default Screen (For Nearby Bus-stops)
            composable(route = BusExpressScreen.Default.name) {
                DefaultScreen()
            }

            // 2. Search Screen
            composable(route = BusExpressScreen.Search.name) {
                SearchScreen(
                    busUiState = viewModel.busUiState,
                    busArrivalsJson = SingaporeBus(
                        metaData = busServiceUiState.metaData,
                        busStopCode = busServiceUiState.busStopCode,
                        services = busServiceUiState.services
                    ),
                    busStopDetails = BusStopValue(
                        busStopCode = busStopNameUiState.busStopCode,
                        busStopRoadName = busStopNameUiState.busStopRoadName,
                        busStopDescription = busStopNameUiState.busStopDescription,
                        latitude = busStopNameUiState.latitude,
                        longitude = busStopNameUiState.longitude
                    ),
                    busRoutes = BusRoutes(
                        metaData = busRouteUiState.metaData,
                        busRouteArray = busRouteUiState.busRouteArray
                    ),
                    busServiceBool = busServiceBoolUiState,
                    viewModel = viewModel,
                    busServicesRouteList = multipleBusUiState,
                    currentScreen = currentScreen
                )
            }

            // 3. Nearby Screen
            composable(route = BusExpressScreen.Nearby.name) {
                NearbyScreen()
            }

            // 3. Favourites [Going Out]
            composable(route = BusExpressScreen.FavouritesAway.name) {
                FavouritesScreen()
            }

            // 4. Favourites [Coming Back]
            composable(route = BusExpressScreen.FavouritesBack.name) {
                FavouritesComingBackScreen()
            }

            // 5. Search Results


        }


    }
}

// Helper Functions
/**
 *  Determine the type of User Input
 */
fun determineBusServiceorStop(userInput: String?): UserInputResult {
    // Determine if UserInput is a BusStopCode
    var busStopCode: String?
    var busServiceNumber: String?
    var busStopCodeBool = false
    val userInputLength = userInput?.length

    if (userInputLength == 5) {
        // Bus Stop Code
        busStopCode = userInput
        busServiceNumber = null
        busStopCodeBool = true
    }
    else {
        // Bus Service Number
        busStopCode = null
        busServiceNumber = userInput
    }

    return UserInputResult(
        busServiceBool = !busStopCodeBool,
        busStopCodeBool = busStopCodeBool,
        busStopCode = busStopCode,
        busServiceNo = busServiceNumber
    )
}

