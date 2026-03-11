package com.xuannie.busexpress.data

import DefaultFavouriteBusRepository
import android.content.Context
import android.util.Log
import com.xuannie.busexpress.data.database.FavouritesBusDatabase
import com.xuannie.busexpress.network.BusApiService
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.xuannie.busexpress.LTA_API_SECRET_KEY
import com.xuannie.busexpress.data.local.BusStopAssetRepository
import com.xuannie.busexpress.data.repository.DefaultTransferRepository
import com.xuannie.busexpress.data.repository.TransferRepository
import com.xuannie.busexpress.network.transfer.TransferApiService
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

interface AppContainer {
    val singaporeBusRepository: SingaporeBusRepository
    val favouriteBusStopRepository: FavouriteBusStopRepository
    val transferRepository: TransferRepository
    val busStopAssetRepository: BusStopAssetRepository
}

class DefaultAppContainer(private val context: Context): AppContainer {

    private val BASE_URL = "https://datamall2.mytransport.sg/ltaodataservice/"
    // Python Server Base
    private val TRANSFER_BASE_URL = "http://10.0.2.2:8000/"


    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    private val loggingInterceptor = HttpLoggingInterceptor(
        object : HttpLoggingInterceptor.Logger {
            override fun log(message: String) {
                Log.d("LTA_HTTP", message)
            }
        }
    ).apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val ltaClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("accept", "application/json")
                // CHANGED: add LTA key here if not already added in BusApiService annotations
                .addHeader("AccountKey", LTA_API_SECRET_KEY)
                .build()
            Log.d("LTA_HTTP", "Request URL = ${request.url}")
            chain.proceed(request)
        }
        .addInterceptor(loggingInterceptor)
        .build()

    // CHANGED: separate client for your Python backend
    private val transferClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("accept", "application/json")
                .build()
            Log.d("TRANSFER_HTTP", "Request URL = ${request.url}")
            chain.proceed(request)
        }
        .addInterceptor(loggingInterceptor)
        .build()

    @OptIn(ExperimentalSerializationApi::class)
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(ltaClient)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

    // CHANGED: second Retrofit for Python transfer server
    @OptIn(ExperimentalSerializationApi::class)
    private val transferRetrofit = Retrofit.Builder()
        .baseUrl(TRANSFER_BASE_URL)
        .client(transferClient)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

    private val retrofitService: BusApiService by lazy {
        retrofit.create(BusApiService::class.java)
    }

    // CHANGED: create transfer API service
    private val transferApiService: TransferApiService by lazy {
        transferRetrofit.create(TransferApiService::class.java)
    }

    override val singaporeBusRepository: SingaporeBusRepository by lazy {
        DefaultSingaporeBusRepository(retrofitService)
    }

    override val favouriteBusStopRepository: FavouriteBusStopRepository by lazy {
        DefaultFavouriteBusRepository(
            favouriteBusStopDao = FavouritesBusDatabase.getDatabase(context).favouriteBusDao(),
            busApiService = retrofitService
        )
    }

    override val transferRepository: TransferRepository by lazy {
        DefaultTransferRepository(transferApiService)
    }

    override val busStopAssetRepository: BusStopAssetRepository by lazy {
        BusStopAssetRepository(context)
    }
}
