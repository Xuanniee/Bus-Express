package com.xuannie.busexpress.network

import com.xuannie.busexpress.LTA_API_SECRET_KEY
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query


interface BusApiService {
    /**
     *  Function to get JSON Objects from URI by specifying Type of Request and Endpoint like "/photos" a URL of sorts
     */
    // 1. Returns Bus Timings for Bus Stop and/or Service Number
    @Headers(
        "accept: application/json",
        "AccountKey: $LTA_API_SECRET_KEY"
    )
    @GET("BusArrivalv2")
    suspend fun getTimingsOfBusStop(
        @Query("BusStopCode") BusStopCode: String? = null,
        @Query("ServiceNo") ServiceNo: String? = null
    ): SingaporeBus

    // 2. Returns the details for all the Bus Stops in Singapore
    @Headers(
        "accept: application/json",
        "AccountKey: $LTA_API_SECRET_KEY"
    )
    @GET("BusStops")
    suspend fun getDetailsOfBusStop(
        @Query("${'$'}skip") NumRecordsToSkip: Int? = null
    ): BusStop

    // 3. Returns all the Bus Routes in Singapore
    @Headers(
        "accept: application/json",
        "AccountKey: $LTA_API_SECRET_KEY"
    )
    @GET("BusRoutes")
    suspend fun getBusRoutes(
        @Query("${'$'}skip") NumRecordsToSkip: Int? = null
    ): BusRoutes


    /*
    // 2. Returns Bus Timings for Bus Service
    @Headers(
        "accept: application/json",
        "AccountKey: $LTA_API_SECRET_KEY"
    )
    @GET("BusArrivalv2")
    suspend fun getTimingsOfBusService(
        @Query("ServiceNo") ServiceNo: String
    ): SingaporeBus

    // 3. Returns Bus Timings for Bus Stop and Bus Service
    @Headers(
        "accept: application/json",
        "AccountKey: $LTA_API_SECRET_KEY"
    )
    @GET("BusArrivalv2")
    suspend fun getTimingsOfBusStopAndBusService(
        @Query("BusStopCode") BusStopCode: String,
        @Query("ServiceNo") ServiceNo: String
    ): SingaporeBus
     */
}






