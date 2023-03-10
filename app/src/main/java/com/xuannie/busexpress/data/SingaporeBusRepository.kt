package com.xuannie.busexpress.data


import com.xuannie.busexpress.network.BusApiService
import com.xuannie.busexpress.network.BusRoutes
import com.xuannie.busexpress.network.BusStop
import com.xuannie.busexpress.network.SingaporeBus


interface SingaporeBusRepository {
    // Bus Timings
    suspend fun getBusTimings(busStopCode: String?, busServiceNumber: String?): SingaporeBus

    // Bus Details
    suspend fun getBusDetails(numRecordsToSkip: Int?): BusStop

    // Bus Routes
    suspend fun getBusRoutes(numRecordsToSkip: Int?): BusRoutes
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

    override suspend fun getBusDetails(numRecordsToSkip: Int?): BusStop {
        return busApiService.getDetailsOfBusStop(
            NumRecordsToSkip = numRecordsToSkip
        )
    }

    override suspend fun getBusRoutes(numRecordsToSkip: Int?): BusRoutes {
        return busApiService.getBusRoutes(
            NumRecordsToSkip = numRecordsToSkip
        )
    }
}


