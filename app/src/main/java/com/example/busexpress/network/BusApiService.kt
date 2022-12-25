package com.example.busexpress.network

import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query


interface BusApiService {
    /**
     *  Function to get JSON Objects from URI by specifying Type of Request and Endpoint like "/photos" a URL of sorts
     */
    // 1. Returns Bus Timings for Bus Stop
    @Headers(
        "accept: application/json",
        "AccountKey: LJYAC7aJQAC4UDbIAPNEMQ=="
    )
    @GET("BusArrivalv2")
    suspend fun getTimingsOfBusStop(
        @Query("BusStopCode") BusStopCode: String,
    ): SingaporeBus

    // 2. Returns Bus Timings for Bus Service
    @Headers(
        "accept: application/json",
        "AccountKey: LJYAC7aJQAC4UDbIAPNEMQ=="
    )
    @GET("BusArrivalv2")
    suspend fun getTimingsOfBusService(
        @Query("ServiceNo") ServiceNo: String
    ): List<SingaporeBus>

    // 3. Returns Bus Timings for Bus Stop and Bus Service
    @Headers(
        "accept: application/json",
        "AccountKey: LJYAC7aJQAC4UDbIAPNEMQ=="
    )
    @GET("BusArrivalv2")
    suspend fun getTimingsOfBusStopAndBusService(
        @Query("BusStopCode") BusStopCode: String,
        @Query("ServiceNo") ServiceNo: String
    ): List<SingaporeBus>
}






