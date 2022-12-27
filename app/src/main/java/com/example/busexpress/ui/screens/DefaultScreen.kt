package com.example.busexpress.ui.screens

import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.busexpress.BusExpressApp
import com.example.busexpress.BusExpressScreen
import com.example.busexpress.R
import com.example.busexpress.network.SingaporeBus
import com.example.busexpress.ui.component.BusStopComposable

@Composable
fun DefaultScreen(
    busUiState: BusUiState,
    modifier: Modifier = Modifier,
    appViewModel: AppViewModel = viewModel(),
    busArrivalsJson: SingaporeBus,
//    navController: NavController
) {
    // Mutable State for User Input
    var userInput = remember {
        mutableStateOf(TextFieldValue(""))
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
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
                appViewModel.getBusTimings(userInput.value.text)
                // Close the Onscreen Keyboard


            }
        )
//        val busArrivalsJson = appViewModel.getBusTimings(userInput = userInput.value.text)

        when(busUiState) {
            is BusUiState.Success -> ResultScreen(busUiState = busUiState, busArrivalsJSON = busArrivalsJson)
            is BusUiState.Loading -> LoadingScreen()
            is BusUiState.Error -> ErrorScreen()
            else -> ErrorScreen()
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
        Image(
            modifier = Modifier.size(200.dp),
            painter = painterResource(id = R.drawable.loading_img),
            contentDescription = stringResource(R.string.loading_flavor_text)
        )
    }
}

@Composable
fun ErrorScreen(modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.fillMaxSize()
    ) {
        Text(text = stringResource(R.string.loading_failed_flavor_text))
    }
}

/**
 * The home screen displaying result of fetching photos.
 */
@Composable
fun ResultScreen(
    busUiState: BusUiState,
    busArrivalsJSON: SingaporeBus,
    modifier: Modifier = Modifier,
) {

    Divider(thickness = 2.dp, modifier = modifier.padding(5.dp))
    // Results of Search
    BusStopComposable(
        busArrivalsJSON = busArrivalsJSON,
        modifier = modifier
    )
    Divider(thickness = 2.dp, modifier = modifier.padding(5.dp))
//    Box(
//        contentAlignment = Alignment.Center,
//        modifier = modifier.fillMaxSize()
//    ) {
//        Text(busUiState.toString())
//    }
}


@Composable
fun SearchView(
    @StringRes label: Int,
    state: MutableState<TextFieldValue>,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions,
    onKeyboardSearch: () -> Unit,
) {
    Column() {
        TextField(
            value = state.value,
            onValueChange = {value ->
                state.value = value
            },
            label = {
                if (state.value == TextFieldValue("")) {
                    Text(
                        stringResource(id = label),
                        modifier = Modifier
                            .fillMaxWidth(),
                        style = MaterialTheme.typography.body1,
                        color = MaterialTheme.colors.onBackground,
                        maxLines = 1
                    )
                }
            },
            modifier = modifier
                .fillMaxWidth(),
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
            keyboardActions = KeyboardActions(
                onSearch = { onKeyboardSearch() }
            ),
            keyboardOptions = keyboardOptions,
            shape = RoundedCornerShape(25)
        )

        Row() {
            Spacer(modifier = modifier.weight(3f))
            // Button for User to Click to begin Search
            Button(
                onClick = {
                    // TODO Pass the User Query to the Search Function
                    onKeyboardSearch()
                },
                modifier = modifier
                    .align(Alignment.CenterVertically)
                    .padding(2.dp)
                    .weight(1f),
            ) {
                Text(text = stringResource(R.string.search_button_flavor_text))
            }
        }
    }

}


