package com.xuannie.busexpress

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TransferWithinAStation
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.SettingsBrightness
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
import com.xuannie.busexpress.ui.config.ThemeMode
import com.xuannie.busexpress.ui.screens.*
import com.xuannie.busexpress.ui.viewmodels.AppViewModel
import com.xuannie.busexpress.ui.viewmodels.FavouriteBusStopViewModel
import com.xuannie.busexpress.ui.viewmodels.LiveTripViewModel
import kotlinx.coroutines.launch

// Enum Class for App Routes
enum class BusExpressScreen(@StringRes val title: Int) {
    Default(title = R.string.app_name),
    Favourites(title = R.string.favourites),
    Nearby(title = R.string.nearby),
    Search(title = R.string.search),
    LiveTrip(title = R.string.live_trip)
}


/**
 * Composable that displays the topBar and displays a navigation menu
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusExpressAppTopBar(
    currentScreen: BusExpressScreen,
    onMenuClick: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = "BusPal",
                style = MaterialTheme.typography.titleLarge
            )
        },
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Open navigation drawer"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}

@Composable
fun BusExpressNavigationDrawer(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    currentScreen: BusExpressScreen,
    onCloseDrawer: () -> Unit,
    favouriteBusStopViewModel: FavouriteBusStopViewModel,
    goingOutFavouriteUiState: FavouriteBusStopList,
    selectedTheme: ThemeMode,
    onThemeSelected: (ThemeMode) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 28.dp, vertical = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "BusPal",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(16.dp))

        HorizontalDivider(
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Navigation",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        NavigationDrawerItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = currentScreen == BusExpressScreen.Default,
            onClick = {
                navController.navigate(BusExpressScreen.Default.name) {
                    launchSingleTop = true
                }
                onCloseDrawer()
            },
            shape = RoundedCornerShape(18.dp),
            colors = NavigationDrawerItemDefaults.colors(
                selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.18f),
                unselectedContainerColor = Color.Transparent,
                selectedIconColor = MaterialTheme.colorScheme.primary,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )

//        NavigationDrawerItem(
//            icon = { Icon(Icons.Default.Search, contentDescription = "Search") },
//            label = { Text("Search") },
//            selected = currentScreen == BusExpressScreen.Search,
//            onClick = {
//                navController.navigate(BusExpressScreen.Search.name) {
//                    launchSingleTop = true
//                }
//                onCloseDrawer()
//            },
//            shape = RoundedCornerShape(18.dp),
//            colors = NavigationDrawerItemDefaults.colors(
//                selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.18f),
//                unselectedContainerColor = Color.Transparent,
//                selectedIconColor = MaterialTheme.colorScheme.primary,
//                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
//                selectedTextColor = MaterialTheme.colorScheme.primary,
//                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
//            )
//        )

        NavigationDrawerItem(
            icon = { Icon(Icons.Default.TransferWithinAStation, contentDescription = "Live Trip") },
            label = { Text("Live Trip") },
            selected = currentScreen == BusExpressScreen.LiveTrip,
            onClick = {
                navController.navigate(BusExpressScreen.LiveTrip.name) {
                    launchSingleTop = true
                }
                onCloseDrawer()
            },
            shape = RoundedCornerShape(18.dp),
            colors = NavigationDrawerItemDefaults.colors(
                selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.18f),
                unselectedContainerColor = Color.Transparent,
                selectedIconColor = MaterialTheme.colorScheme.primary,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )

        NavigationDrawerItem(
            icon = { Icon(Icons.Default.Favorite, contentDescription = "Favourites") },
            label = { Text("Favourites") },
            selected = currentScreen == BusExpressScreen.Favourites,
            onClick = {
                favouriteBusStopViewModel.determineOutAndBack(
                    goingOutFavouriteUiState = goingOutFavouriteUiState
                )
                navController.navigate(BusExpressScreen.Favourites.name) {
                    launchSingleTop = true
                }
                onCloseDrawer()
            },
            shape = RoundedCornerShape(18.dp),
            colors = NavigationDrawerItemDefaults.colors(
                selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.18f),
                unselectedContainerColor = Color.Transparent,
                selectedIconColor = MaterialTheme.colorScheme.primary,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        HorizontalDivider(
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Theme",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(10.dp))

        ThemeSegmentedRow(
            selectedTheme = selectedTheme,
            onThemeSelected = onThemeSelected
        )
    }
}

@Composable
private fun DrawerNavItem(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    selectedItemColor: Color,
    selectedContentColor: Color,
    unselectedContentColor: Color
) {
    val backgroundColor = if (selected) selectedItemColor else Color.Transparent
    val contentColor = if (selected) selectedContentColor else unselectedContentColor

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(18.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = contentColor
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
            color = contentColor
        )
    }
}

@Composable
private fun ThemeSegmentedRow(
    selectedTheme: ThemeMode,
    onThemeSelected: (ThemeMode) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(18.dp)
            )
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        ThemeChip(
            title = "Light",
            icon = Icons.Outlined.LightMode,
            selected = selectedTheme == ThemeMode.LIGHT,
            onClick = { onThemeSelected(ThemeMode.LIGHT) },
            modifier = Modifier.weight(1f)
        )

        ThemeChip(
            title = "Dark",
            icon = Icons.Outlined.DarkMode,
            selected = selectedTheme == ThemeMode.DARK,
            onClick = { onThemeSelected(ThemeMode.DARK) },
            modifier = Modifier.weight(1f)
        )

        ThemeChip(
            title = "System",
            icon = Icons.Outlined.SettingsBrightness,
            selected = selectedTheme == ThemeMode.SYSTEM,
            onClick = { onThemeSelected(ThemeMode.SYSTEM) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun ThemeChip(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor =
        if (selected) MaterialTheme.colorScheme.primary
        else Color.Transparent

    val contentColor =
        if (selected) MaterialTheme.colorScheme.onPrimary
        else MaterialTheme.colorScheme.onSurfaceVariant

    Column(
        modifier = modifier
            .background(backgroundColor, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(vertical = 10.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = contentColor
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            color = contentColor
        )
    }
}

//@Composable
//fun BusExpressNavigationDrawer(
//    modifier: Modifier = Modifier,
//    scope: CoroutineScope,
//    navController: NavHostController,
//    scaffoldState: ScaffoldState,
//    favouriteBusStopViewModel: FavouriteBusStopViewModel,
//    goingOutFavouriteUiState: FavouriteBusStopList,
//) {
//    Column(
//        modifier = modifier
//            .fillMaxWidth()
//
//    ) {
//        // Headline Description
//        // TODO Change with App Logo
//        Row(
//            horizontalArrangement = Arrangement.Start,
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Image(
//                imageVector = Icons.TwoTone.Email,
//                contentDescription = null,
//                modifier = modifier
//                    .weight(1f)
//            )
//
//            Spacer(modifier = modifier.weight(5f))
//
//            // Icon to close the Navigation Drawer
//            IconButton(
//                onClick = {
//                scope.launch { scaffoldState.drawerState.close() }
//                },
//                modifier = modifier.weight(1f)
//            ) {
//                Icon(
//                    imageVector = Icons.Filled.Close,
//                    contentDescription = stringResource(R.string.navigation_drawer_close_flavor_text)
//                )
//
//            }
//        }
//
//        Spacer(modifier = Modifier.height(10.dp))
//
//        Text(
//            text = stringResource(id = R.string.app_name),
//            style = MaterialTheme.typography.h6,
//            modifier = modifier.padding(
//                start = 5.dp,
//                end = 5.dp,
//                top = 5.dp,
//                bottom = 1.dp
//            )
//        )
//
////        Text(
////            text = stringResource(R.string.navigation_flavor_text),
//            style = MaterialTheme.typography.body2,
//            modifier = modifier.padding(5.dp)
//        )
//
//        Spacer(modifier = Modifier.height(5.dp))
//
//        Divider(
//            thickness = 2.dp,
//            color = Grey900,
//            modifier = modifier.padding(2.dp)
//        )
//
//        /**
//         *  Navigation Options / Buttons
//         */
//        // Home
//        Button(
//            onClick = {
//                // Navigate to the Desired Route
//                navController.navigate(BusExpressScreen.Default.name)
//                // Close the App Drawer
//                scope.launch { scaffoldState.drawerState.close() }
//            },
//            modifier = modifier
//                .fillMaxWidth()
//                .padding(all = 5.dp)
//        ) {
//            Image(
//                imageVector = Icons.Filled.Home,
//                contentDescription = null,
//
//            )
//            Text(stringResource(R.string.home_navigation_desc))
//        }
//        // Search
//        Button(
//            onClick = {
//                navController.navigate(BusExpressScreen.Search.name)
//                scope.launch { scaffoldState.drawerState.close() }
//            },
//            modifier = modifier
//                .fillMaxWidth()
//                .padding(all = 5.dp)
//        ) {
//            Image(
//                imageVector = Icons.Filled.Search,
//                contentDescription = stringResource(R.string.search_nav_desc)
//            )
//            Text(stringResource(id = R.string.search_nav_desc))
//        }
//
//        // Live Trip Planner
//        Button(
//            onClick = {
//                navController.navigate(BusExpressScreen.LiveTrip.name)
//                scope.launch { scaffoldState.drawerState.close() }
//            },
//            modifier = modifier
//                .fillMaxWidth()
//                .padding(all = 5.dp)
//        ) {
//            Image(
//                imageVector = Icons.Filled.TransferWithinAStation,
//                contentDescription = stringResource(R.string.live_trip_planner_desc)
//            )
//            Text(stringResource(id = R.string.live_trip))
//        }
//
//        // Favourites
//        Button(
//            onClick = {
//                favouriteBusStopViewModel.determineOutAndBack(
//                    goingOutFavouriteUiState = goingOutFavouriteUiState,
//                )
//                navController.navigate(BusExpressScreen.Favourites.name)
//                scope.launch { scaffoldState.drawerState.close() }
//            },
//            modifier = modifier
//                .fillMaxWidth()
//                .padding(all = 5.dp)
//        ) {
//            Image(
//                imageVector = Icons.Filled.Favorite,
//                contentDescription = null
//            )
//            Text(stringResource(R.string.favourites_navigation_desc))
//        }
//
//        /**
//         * Nearby is hidden as not built yet so can release the app
//         */
////        Button(
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
//
//        // Easter Egg Button
////        Button(
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
//
//    }
//
//}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusExpressApp(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    appViewModel: AppViewModel,
    favouriteBusStopViewModel: FavouriteBusStopViewModel,
    liveTripViewModel: LiveTripViewModel,
) {
    // Save Current Back Stack Entry
    val backStackEntry by navController.currentBackStackEntryAsState()
    // Name of Current Screen as a Variable
    val currentScreen = BusExpressScreen.valueOf(
        backStackEntry?.destination?.route ?: BusExpressScreen.Default.name
    )
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    // Holding the API Call Data here is better so I can pass it to multiple screens
    val busServiceUiState by appViewModel.busServiceUiState.collectAsState()
    val busStopNameUiState by appViewModel.busStopNameUiState.collectAsState()
    val busRouteUiState by appViewModel.busRouteUiState.collectAsState()
    val multipleBusUiState by appViewModel.multipleBusUiState.collectAsState()
    val busStopsInFavourites by favouriteBusStopViewModel.busStopsInFavUiState.collectAsState()
    val allFavouritesUiState by favouriteBusStopViewModel.allFavouritesUiState.collectAsState()

    val scope = rememberCoroutineScope()

    // Decide on Theme
    var selectedTheme by remember { mutableStateOf(ThemeMode.SYSTEM) }

    // Top Navigation Bar
    ModalNavigationDrawer(
        modifier = modifier,
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = MaterialTheme.colorScheme.surface
            ) {
                BusExpressNavigationDrawer(
                    navController = navController,
                    currentScreen = currentScreen,
                    onCloseDrawer = {
                        scope.launch { drawerState.close() }
                    },
                    favouriteBusStopViewModel = favouriteBusStopViewModel,
                    goingOutFavouriteUiState = allFavouritesUiState,
                    selectedTheme = selectedTheme,
                    onThemeSelected = { selectedTheme = it }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                BusExpressAppTopBar(
                    currentScreen = currentScreen,
                    onMenuClick = {
                        scope.launch {
                            if (drawerState.isClosed) {
                                drawerState.open()
                            } else {
                                drawerState.close()
                            }
                        }
                    }
                )
            }
        ) { innerPadding ->
            // For Small Menu Popup
            val menuSelection = remember { mutableStateOf(MenuSelection.NONE) }

            // State Variables
            val busServiceBoolUiState = appViewModel.busServiceBoolUiState

            // NavHost Composable for Navigating between Screens
            NavHost(
                navController = navController,
                startDestination = BusExpressScreen.Default.name,
                modifier = Modifier.padding(innerPadding)
            ) {
                // Routes for Every Screen in the App
                // 1. Default Screen - Should be Nearby + Search
                composable(route = BusExpressScreen.Default.name) {
                    SearchScreen(
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
                        favouriteBusStopViewModel = favouriteBusStopViewModel,
                        menuSelection = menuSelection,
                    )
                }

                // 2. Live Trip Planner
                composable(route = BusExpressScreen.LiveTrip.name) {
                    LiveTripMapScreen(
                        viewModel = liveTripViewModel
                    )
                }

                // 3. Favourites Screen
                composable(route = BusExpressScreen.Favourites.name) {
                    FavouritesScreen(
                        favouriteBusStopViewModel = favouriteBusStopViewModel,
                        busStopsInFavourites = busStopsInFavourites,
                        appViewModel = appViewModel
                    )
                }
            }
        }
    }
}

// Helper Functions
/**
 *  Determine the type of User Input
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