package com.example.busexpress.data

import android.content.ContentValues.TAG
import android.util.Log
import com.example.busexpress.network.BusApiService
import com.example.busexpress.network.SingaporeBus


interface SingaporeBusRepository {
    suspend fun getBusTimings(): SingaporeBus
}

class DefaultSingaporeBusRepository(
    private val busApiService: BusApiService
): SingaporeBusRepository {
    override suspend fun getBusTimings(): SingaporeBus {
        return busApiService.getTimingsOfBusStop(
            BusStopCode = "65199"
        )
    }
}

//accept = "application/json",
//AccountKey = "LJYAC7aJQAC4UDbIAPNEMQ==",




