package com.example.busexpress.data


import com.example.busexpress.network.BusApiService
import com.example.busexpress.network.BusStop
import com.example.busexpress.network.SingaporeBus


interface SingaporeBusRepository {
    // Bus Timings
    suspend fun getBusTimings(busStopCode: String?, busServiceNumber: String?): SingaporeBus

    // Bus Details
    suspend fun getBusDetails(): BusStop
}

class DefaultSingaporeBusRepository(
    private val busApiService: BusApiService
): SingaporeBusRepository {
    override suspend fun getBusTimings(busStopCode: String?, busServiceNumber: String?): SingaporeBus {
        // TODO Replace with User Query
        return busApiService.getTimingsOfBusStop(
            BusStopCode = busStopCode,
            ServiceNo = busServiceNumber
        )
    }

    override suspend fun getBusDetails(): BusStop {
        return busApiService.getDetailsOfBusStop()
    }
}


