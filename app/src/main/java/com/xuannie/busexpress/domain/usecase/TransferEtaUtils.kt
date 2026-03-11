package com.xuannie.busexpress.domain.usecase

import com.xuannie.busexpress.network.NextBusTiming
import com.xuannie.busexpress.network.SingaporeBus
import java.time.OffsetDateTime
import java.time.ZoneOffset

object TransferEtaUtils {
    fun estimateRideSeconds(stopCount: Int, secondsPerStop: Int = 90): Int {
        return stopCount * secondsPerStop
    }

    fun waitTimeSeconds(nextBus: NextBusTiming): Int? {
        if (nextBus.estimatedArrival == "No Bus Service Available") return null
        return try {
            val now = OffsetDateTime.now(ZoneOffset.UTC)
            val eta = OffsetDateTime.parse(nextBus.estimatedArrival)
            val diff = eta.toEpochSecond() - now.toEpochSecond()
            if (diff < 0) 0 else diff.toInt()
        } catch (e: Exception) {
            null
        }
    }

    fun nextWaitForService(arrival: SingaporeBus, serviceNo: String): Int? {
        val svc = arrival.services.firstOrNull { it.busServiceNumber == serviceNo } ?: return null
        return waitTimeSeconds(svc.nextBus1)
    }
}