package com.example.busexpress.ui.screens

import android.util.DisplayMetrics
import android.view.Display
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.busexpress.BusExpressScreen
import com.example.busexpress.R
import com.example.busexpress.ui.favouriteBusStops.FavouriteBusStopUiState
import com.example.busexpress.ui.favouriteBusStops.FavouriteBusStopViewModel


/**
 * The first screen users will see when they open the application
 */
@Composable
fun DefaultScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxWidth()
    ) {
        IconButton(
            onClick = {
            // Bring User to the Search Screen
            navController.navigate(BusExpressScreen.Search.name)
            // Click the Search Bar for User
            },
            modifier = modifier
                .padding(5.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.search_bar_icon),
                contentDescription = stringResource(R.string.searchbar_redirect_content_desc),
                tint = MaterialTheme.colors.primary
            )
        }

        // App Name
        Text(
            text = stringResource(id = R.string.app_name),
            style = MaterialTheme.typography.h4,
            fontWeight = FontWeight.SemiBold,
            modifier = modifier
                .fillMaxWidth()
                .padding(10.dp),
            textAlign = TextAlign.Center
        )

        // Tutorial
        Text(
            text = stringResource(R.string.tutorial_default_desc),
            style = MaterialTheme.typography.body2,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Center,
            maxLines = 3,
            modifier = modifier.padding(5.dp)
        )

    }

}

//@Composable
//fun MessageList(messages: List<Message>) {
//    Column {
//        messages.forEach { message ->
//            MessageRow(message)
//        }
//    }
//}
