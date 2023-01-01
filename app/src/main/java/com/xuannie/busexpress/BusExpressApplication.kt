package com.xuannie.busexpress

import android.app.Application
import com.xuannie.busexpress.data.AppContainer
import com.xuannie.busexpress.data.DefaultAppContainer

class BusExpressApplication: Application() {
    lateinit var container: AppContainer
    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}