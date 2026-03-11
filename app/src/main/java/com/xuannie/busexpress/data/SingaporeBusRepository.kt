package com.xuannie.busexpress.data

import android.util.Log
import com.xuannie.busexpress.network.BusApiService
import com.xuannie.busexpress.network.BusRoutes
import com.xuannie.busexpress.network.BusStop
import com.xuannie.busexpress.network.SingaporeBus

interface SingaporeBusRepository {
    suspend fun getBusTimings(busStopCode: String?, busServiceNumber: String?): SingaporeBus
    suspend fun getBusDetails(numRecordsToSkip: Int?): BusStop
    suspend fun getBusRoutes(numRecordsToSkip: Int?): BusRoutes
}

class DefaultSingaporeBusRepository(
    private val busApiService: BusApiService
): SingaporeBusRepository {

    override suspend fun getBusTimings(busStopCode: String?, busServiceNumber: String?): SingaporeBus {
        Log.d("LTA_REPO", "getBusTimings called with busStopCode=$busStopCode serviceNo=$busServiceNumber")
        return try {
            val result = busApiService.getTimingsOfBusStop(
                BusStopCode = busStopCode,
                ServiceNo = busServiceNumber
            )
            Log.d("LTA_REPO", "Success. BusStop=${result.busStopCode}, services=${result.services.size}")
            result
        } catch (e: Exception) {
            Log.e("LTA_REPO", "getBusTimings failed", e)
            throw e
        }
    }

    override suspend fun getBusDetails(numRecordsToSkip: Int?): BusStop {
        Log.d("LTA_REPO", "getBusDetails called skip=$numRecordsToSkip")
        return try {
            val result = busApiService.getDetailsOfBusStop(
                NumRecordsToSkip = numRecordsToSkip
            )
            Log.d("LTA_REPO", "getBusDetails success count=${result.value.size}")
            result
        } catch (e: Exception) {
            Log.e("LTA_REPO", "getBusDetails failed", e)
            throw e
        }
    }

    override suspend fun getBusRoutes(numRecordsToSkip: Int?): BusRoutes {
        Log.d("LTA_REPO", "getBusRoutes called skip=$numRecordsToSkip")
        return try {
            val result = busApiService.getBusRoutes(
                NumRecordsToSkip = numRecordsToSkip
            )
            Log.d("LTA_REPO", "getBusRoutes success count=${result.busRouteArray.size}")
            result
        } catch (e: Exception) {
            Log.e("LTA_REPO", "getBusRoutes failed", e)
            throw e
        }
    }
}