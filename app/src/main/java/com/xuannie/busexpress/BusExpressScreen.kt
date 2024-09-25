// Package Declarations
package com.xuannie.busexpress

import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.twotone.Email
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.xuannie.busexpress.data.FavouriteBusStopList
import com.xuannie.busexpress.network.BusRoutes
import com.xuannie.busexpress.network.BusStopValue
import com.xuannie.busexpress.network.SingaporeBus
import com.xuannie.busexpress.network.UserInputResult
import com.xuannie.busexpress.ui.component.MenuSelection
import com.xuannie.busexpress.ui.favouriteBusStops.FavouriteBusStopViewModel
import com.xuannie.busexpress.ui.screens.*
import com.xuannie.busexpress.ui.theme.Grey900
import com.xuannie.busexpress.ui.theme.NavigationDrawer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Enum Class for App Routes - Defining different screens in the app with their own title resource ID
 */
enum class BusExpressScreen(@StringRes val title: Int) {
    // Title Resource IDs are references to string res defined in res/values/string.xml
    Default(title = R.string.app_name),
    Favourites(title = R.string.favourites),
    Nearby(title = R.string.nearby),    // Currently not implemented
    Search(title = R.string.search)
}


/**
 * Displays the top app bar, which includes the screen title and a navigation icon (menu).
 * When the menu button is clicked, it opens the navigation drawer.
 */
@Composable
fun BusExpressAppTopBar(
    modifier: Modifier = Modifier,
    currentScreen: BusExpressScreen,    // Current Screen that determines the title
    scope: CoroutineScope,              // Coroutine scope to handle side-effects like drawer operations
    scaffoldState: ScaffoldState        // The state of the scaffold, used to control the drawer
) {
    // TODO Add a Logo Next Time
    TopAppBar(
        // Screen Title
        title = { Text(stringResource(id = currentScreen.title)) },
        modifier = modifier,
        // Menu Button for opening Nav Drawer
        navigationIcon =  {
            IconButton(onClick = {
                // Launch a coroutine to open the drawers
                scope.launch { scaffoldState.drawerState.open() }
            }) {
                // Menu Icon with Accessibility Description
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = stringResource(R.string.navbar_description)
                )
            }
        },
        // TODO Implementing the Dark Mode Button
//        actions = {
//            // Already in a RowScope, so will be placed Horizontally
//            IconButton(onClick = { /*TODO DARK MODE*/ }) {
//                Icon(
//                    imageVector = Icons.Filled.DarkMode,
//                    contentDescription = stringResource(R.string.dark_mode_description)
//                )
//
//            }
//        },
        elevation = 20.dp
    )
}

/**
 * Composable of the Nav Drawer. Includes options to go Home, Search or Favourites
 */
@Composable
fun BusExpressNavigationDrawer(
    modifier: Modifier = Modifier,
    // Scope to handle drawer open/close via coroutines
    scope: CoroutineScope,
    navController: NavHostController,
    // State of the scaffold (drawer state)
    scaffoldState: ScaffoldState,
    // ViewModel for managing favourite bus stops
    favouriteBusStopViewModel: FavouriteBusStopViewModel,
    // Data state for favourite bus stops
    goingOutFavouriteUiState: FavouriteBusStopList,
) {
    Column(
        // Drawer to fill full width of screen
        modifier = modifier
            .fillMaxWidth()

    ) {
        // Row to display the email and close button horizontally
        // TODO Change with App Logo
        Row(
            // Align items to the left
            horizontalArrangement = Arrangement.Start,
            // Center items vertically
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Email Icon Button
            Image(
                imageVector = Icons.TwoTone.Email,
                contentDescription = null,
                // Occupy 1/7 of the Row-Width with 1f weight
                modifier = modifier
                    .weight(1f)
            )

            // Occupy 5/7 of the Row Width to push close button to the right
            Spacer(modifier = modifier.weight(5f))

            // Icon to close the Navigation Drawer
            IconButton(
                onClick = {
                    scope.launch { scaffoldState.drawerState.close() }
                },
                // Takes up the last 1/7 width
                modifier = modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = stringResource(R.string.navigation_drawer_close_flavor_text)
                )

            }
        }

        // Vertical Padding between the Row and Text below
        Spacer(modifier = Modifier.height(10.dp))

        // Display App Name as a Headline
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

//        Text(
//            text = stringResource(R.string.navigation_flavor_text),
//            style = MaterialTheme.typography.body2,
//            modifier = modifier.padding(5.dp)
//        )

        Spacer(modifier = Modifier.height(5.dp))

        // Include divider to separate the header above from the nav options below
        Divider(
            thickness = 2.dp,
            color = Grey900,
            modifier = modifier.padding(2.dp)
        )

        /**
         *  Navigation Options / Buttons
         */
        // Home Screen Button
        Button(
            onClick = {
                // Navigate to back to Home screen
                navController.navigate(BusExpressScreen.Default.name)
                // Close the App Drawer after reaching Home screen
                scope.launch { scaffoldState.drawerState.close() }
            },
            modifier = modifier
                .fillMaxWidth()
                .padding(all = 5.dp)
        ) {
            Image(
                imageVector = Icons.Filled.Home,
                contentDescription = null,

            )
            Text(stringResource(R.string.home_navigation_desc))
        }

        // Search Screen Buton
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

        // Favourites Screen Button
        Button(
            onClick = {
                favouriteBusStopViewModel.determineOutAndBack(
                    goingOutFavouriteUiState = goingOutFavouriteUiState,
                )
                navController.navigate(BusExpressScreen.Favourites.name)
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

        /**
         * Nearby is hidden as not built yet so can release the app
         */
//        Button(
//            onClick = {
//                navController.navigate(BusExpressScreen.Nearby.name)
//                scope.launch { scaffoldState.drawerState.close() }
//            },
//            modifier = modifier
//                .fillMaxWidth()
//                .padding(all = 5.dp)
//        ) {
//            Image(
//                imageVector = Icons.Filled.LocationOn,
//                contentDescription = null
//            )
//            Text(text = stringResource(R.string.nearby_navigation_desc))
//        }

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

/**
 * Main Composable for the App
 *
 * - Manages navigation between different screens
 * - Includes state variables to manage API responses and Nav
 * - Holds the scaffold for the top menu bar and nav drawer
 */
@Composable
fun BusExpressApp(
    modifier: Modifier = Modifier,
    // Nav Controller to manage Screen Transitions
    navController: NavHostController = rememberNavController(),
    // Viewmodel for managing bus related data
    appViewModel: AppViewModel = viewModel(),
    favouriteBusStopViewModel: FavouriteBusStopViewModel = viewModel(),
) {
    // Get the current back stack entry to determine the active screen
    val backStackEntry by navController.currentBackStackEntryAsState()
    // Name of Current Screen as a Variable
    val currentScreen = BusExpressScreen.valueOf(
        backStackEntry?.destination?.route ?: BusExpressScreen.Default.name
    )
    // Scaffold state to manage drawer and the top bar
    val scaffoldState = rememberScaffoldState()

    // State variables for API data from ViewModel (bus service, stop name, route, etc)
    // Holding the API Call Data here is better so I can pass it to multiple screens
    val busServiceUiState by appViewModel.busServiceUiState.collectAsState()
    val busStopNameUiState by appViewModel.busStopNameUiState.collectAsState()
    val busRouteUiState by appViewModel.busRouteUiState.collectAsState()
    val multipleBusUiState by appViewModel.multipleBusUiState.collectAsState()
    val busStopsInFavourites by favouriteBusStopViewModel.busStopsInFavUiState.collectAsState()
    val allFavouritesUiState by favouriteBusStopViewModel.allFavouritesUiState.collectAsState()

    // Coroutine scope to handle side effects (like opening/closing the drawer)
    val scope = rememberCoroutineScope()

    // The Scaffold is used to define the overall structure of the app's layout (top bar, drawer, main content)
    // Top Navigation Bar
    Scaffold(
        modifier = modifier,
        scaffoldState = scaffoldState,
        drawerContent = {
            // Defines the content of the Nav Drawer
            // TODO Add a Composable for Navigation
            BusExpressNavigationDrawer(
                navController = navController,
                scope = scope,
                scaffoldState = scaffoldState,
                favouriteBusStopViewModel = favouriteBusStopViewModel,
                goingOutFavouriteUiState = allFavouritesUiState
            )
        },
        drawerElevation = 20.dp,
        drawerShape = NavigationDrawer,
        // Allow the drawer to be opened by swiping
        drawerGesturesEnabled = true,
        topBar = {
            BusExpressAppTopBar(
                currentScreen = currentScreen,
                scaffoldState = scaffoldState,
                scope = scope
            )
        }
    ) { innerPadding ->
        // Inner padding ensures that the content doesn't overlap with the top bar or drawer

        // Menu selection (for small popup menus, possibly for filtering options)
        val menuSelection = remember { mutableStateOf(MenuSelection.NONE) }

        // State Variable that determines if user is looking via bus stop or bus service number
        val busServiceBoolUiState = appViewModel.busServiceBoolUiState

        // NavHost Composable for Navigating between Screens
        NavHost(
            navController = navController,
            // Set the starting screen as the Default screen
            startDestination = BusExpressScreen.Default.name,
            modifier = modifier.padding(innerPadding)
        ) {
            // Routes for Every Screen in the App

            // 1. Default Screen (For Nearby Bus-stops)
            composable(route = BusExpressScreen.Default.name) {
                DefaultScreen(navController = navController)
            }

            // 2. Search Screen
            composable(route = BusExpressScreen.Search.name) {
                SearchScreen(
                    // Pass Bus Service Data from ViewModel
                    busUiState = appViewModel.busUiState,
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
                    viewModel = appViewModel,
                    busServicesRouteList = multipleBusUiState,
                    currentScreen = currentScreen,
                    // ViewModel for Favourites
                    favouriteBusStopViewModel = favouriteBusStopViewModel,
                    menuSelection = menuSelection,
                )
            }

            // 3. Nearby Screen
            composable(route = BusExpressScreen.Nearby.name) {
                NearbyScreen(
                )
            }

            // 3. Favourites [Going Out]
            composable(route = BusExpressScreen.Favourites.name) {
                FavouritesScreen(
                    favouriteBusStopViewModel = favouriteBusStopViewModel,
                    busStopsInFavourites = busStopsInFavourites,
                    appViewModel = appViewModel
                )
            }

//            // 4. Favourites [Coming Back]
//            composable(route = BusExpressScreen.Favourites.name) {
//                FavouritesComingBackScreen()
//            }

            // 5. Search Results


        }


    }
}

// Helper Functions
/**
 *  Determine the type of User Input (Bus Stop or Service)
 */
fun determineBusServiceorStop(userInput: String?): UserInputResult {
    // Determine if UserInput is a BusStopCode
    val busStopCode: String?
    val busServiceNumber: String?
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

