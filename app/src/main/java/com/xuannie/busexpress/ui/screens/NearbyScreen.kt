package com.xuannie.busexpress.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.xuannie.busexpress.network.BusStopValue
import com.xuannie.busexpress.network.SingaporeBus
import com.xuannie.busexpress.ui.component.BusStopComposable
import com.xuannie.busexpress.ui.component.MenuSelection
import com.xuannie.busexpress.ui.viewmodels.AppViewModel
import com.xuannie.busexpress.ui.viewmodels.FavouriteBusStopViewModel


@Composable
fun NearbyStopsScreen(
    nearbyStops: List<BusStopValue>,
    nearbyArrivals: List<SingaporeBus>,
    favouriteBusStopViewModel: FavouriteBusStopViewModel,
    menuSelection: MutableState<MenuSelection>,
    appViewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .padding(10.dp)
    ) {
        items(nearbyStops.size) { index ->
            HorizontalDivider(
                thickness = 2.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.18f),
                modifier = modifier.padding(5.dp)
            )

            BusStopComposable(
                busArrivalsJSON = nearbyArrivals[index],
                busStopDetailsJSON = nearbyStops[index],
                busServiceBool = false,
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