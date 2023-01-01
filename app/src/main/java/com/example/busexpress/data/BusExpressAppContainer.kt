package com.example.busexpress.data

import android.content.Context
import com.example.busexpress.data.database.FavouritesBusDatabase
import com.example.busexpress.network.BusApiService
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit


/**
 * A container is an object that contains the dependencies that the app requires.
 * These dependencies are used across the whole application, so they need to be in a common place
 * that all activities can use. You can create a subclass of the Application class and store a
 * reference to the container.
 */
interface AppContainer {
    // Abstract Properties
    val singaporeBusRepository: SingaporeBusRepository
    val favouriteBusStopRepository: FavouriteBusStopRepository
}

class DefaultAppContainer(private val context: Context): AppContainer {
    // TODO Replace the Bus Code with User Query
    // URL does not include query parameters like ServiceNo and BusStopCode
    private val BASE_URL =
        "http://datamall2.mytransport.sg/ltaodataservice/"

    // For AppViewModel
    @OptIn(ExperimentalSerializationApi::class)
    private val retrofit = Retrofit.Builder()
        .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
        .baseUrl(BASE_URL)
        .build()

    private val retrofitService: BusApiService by lazy {
        retrofit.create(BusApiService::class.java)
    }

    // Override the Property in the Interface
    override val singaporeBusRepository: SingaporeBusRepository by lazy {
        DefaultSingaporeBusRepository(retrofitService)
    }

    override val favouriteBusStopRepository: FavouriteBusStopRepository by lazy {
        DefaultFavouriteBusRepository(
            favouriteBusStopDao = FavouritesBusDatabase.getDatabase(context).favouriteBusDao(),
            busApiService = retrofitService
        )
    }
}













