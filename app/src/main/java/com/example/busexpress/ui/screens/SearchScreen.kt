package com.example.busexpress.ui.screens

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.unit.sp
import com.example.busexpress.BusExpressScreen
import com.example.busexpress.R
import com.example.busexpress.network.BusRoutes
import com.example.busexpress.network.BusServicesRoute
import com.example.busexpress.network.BusStopValue
import com.example.busexpress.network.SingaporeBus
import com.example.busexpress.ui.component.BusStopComposable

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
) {
    // Mutable State for User Input
    val userInput = remember {
        mutableStateOf(TextFieldValue(""))
    }
    // Focus Manager
    val focusManager = LocalFocusManager.current

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
                // Provide Search Feature based on Bus Stop Code or Bus Service Number
                viewModel.determineUserQuery(userInput = userInput.value.text)

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
                    busRoutes = busRoutes
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
) {
    // Results of Search
    if (busServiceBool) {
        // Bus Services
        val busRouteArray = busRoutes.busRouteArray

        LazyColumn(
            modifier = modifier
                .fillMaxWidth()
                .padding(all = 10.dp)
        ) {
            items(busRouteArray.size) {index ->
                Divider(thickness = 2.dp, modifier = modifier.padding(5.dp))

                BusStopComposable(
                    busArrivalsJSON = busServicesRouteList.busArrivalsJSONList[index],
                    busStopDetailsJSON = busServicesRouteList.busStopDetailsJSONList[index],
                    busServiceBool = busServiceBool,
                    modifier = modifier
                )

                Divider(thickness = 2.dp, modifier = modifier.padding(5.dp))
            }
        }

    }
    else {
        // Bus Stop Code
        Divider(thickness = 2.dp, modifier = modifier.padding(5.dp))

        BusStopComposable(
            busArrivalsJSON = busArrivalsJSON,
            busStopDetailsJSON = busStopDetails,
            modifier = modifier,
            busServiceBool = busServiceBool
        )

        Divider(thickness = 2.dp, modifier = modifier.padding(5.dp))
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


