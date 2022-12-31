package com.example.busexpress.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.busexpress.BusExpressApplication
import com.example.busexpress.ui.favouriteBusStops.FavouriteBusStopViewModel
import com.example.busexpress.ui.screens.AppViewModel

/**
 * Provides Factory to create instance of ViewModel for the entire Bus app
 */
object AppViewModelProvider {
    val Factory: ViewModelProvider.Factory = viewModelFactory {
        initializer {
            val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as BusExpressApplication)
            val singaporeBusRepository = application.container.singaporeBusRepository
            AppViewModel(singaporeBusRepository = singaporeBusRepository)
        }

        /**
         * Initializer for FavBusStopViewModel
         */

        /**
         * Initializer for FavBusStopViewModel
         */
        initializer {
            FavouriteBusStopViewModel(BusExpressApplication().container.favouriteBusStopRepository)
        }
    }
}


