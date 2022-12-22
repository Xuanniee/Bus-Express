package com.example.busexpress.data

import com.example.busexpress.network.BusApiService
import com.example.busexpress.network.SingaporeBus


interface SingaporeBusRepository {
    suspend fun getBusTimings(): List<SingaporeBus>
}

class DefaultSingaporeBusRepository(
    private val busApiService: BusApiService
): SingaporeBusRepository {
    override suspend fun getBusTimings(): List<SingaporeBus> {
        return busApiService.getTimingsOfBusStop(
            accept = "application/json",
            AccountKey = "LJYAC7aJQAC4UDbIAPNEMQ==",
            BusStopCode = "65199"
        )
    }
}




