package com.example.busexpress

import android.app.Application
import com.example.busexpress.data.AppContainer
import com.example.busexpress.data.DefaultAppContainer

class BusExpressApplication: Application() {
    lateinit var container: AppContainer
    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer()
    }
}