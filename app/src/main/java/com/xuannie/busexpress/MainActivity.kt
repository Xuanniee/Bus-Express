package com.xuannie.busexpress

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.xuannie.busexpress.ui.viewmodels.FavouriteBusStopViewModel
import com.xuannie.busexpress.ui.viewmodels.AppViewModel
import com.xuannie.busexpress.ui.theme.BusExpressTheme
import com.xuannie.busexpress.ui.viewmodels.LiveTripViewModel
import org.maplibre.android.MapLibre

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize MapLibre before creating any view
        MapLibre.getInstance(this)
        setContent {
            BusExpressTheme {
                val viewModel: AppViewModel =
                     viewModel(factory = AppViewModel.Factory)

                val favViewModel: FavouriteBusStopViewModel =
                    viewModel(factory = FavouriteBusStopViewModel.Factory)

                val liveTripViewModel: LiveTripViewModel =
                    viewModel(factory = LiveTripViewModel.Factory)

                BusExpressApp(
                    appViewModel = viewModel,
                    favouriteBusStopViewModel = favViewModel,
                    liveTripViewModel = liveTripViewModel
                )
            }
        }
    }
}