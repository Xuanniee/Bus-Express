package com.example.busexpress

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.busexpress.ui.favouriteBusStops.FavouriteBusStopViewModel
import com.example.busexpress.ui.screens.AppViewModel
import com.example.busexpress.ui.theme.BusExpressTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BusExpressTheme {
                val viewModel: AppViewModel =
                     viewModel(factory = AppViewModel.Factory)
                val favViewModel: FavouriteBusStopViewModel =
                    viewModel(factory = FavouriteBusStopViewModel.Factory)
                BusExpressApp(
                    appViewModel = viewModel,
                    favouriteBusStopViewModel = favViewModel
                )
            }
        }
    }
}