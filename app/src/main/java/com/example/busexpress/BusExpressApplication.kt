package com.example.busexpress

import android.app.Application
import com.example.busexpress.data.AppContainer
import com.example.busexpress.data.DefaultAppContainer
import com.example.busexpress.data.FavouriteBusStopRepository
import com.example.busexpress.ui.favouriteBusStops.FavouriteBusStopViewModel

class BusExpressApplication: Application() {
    lateinit var container: AppContainer
    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}