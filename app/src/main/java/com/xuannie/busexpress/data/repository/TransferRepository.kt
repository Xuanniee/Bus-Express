package com.xuannie.busexpress.data.repository

import com.xuannie.busexpress.domain.model.TimelineStop
import com.xuannie.busexpress.domain.model.TransferSuggestion
import com.xuannie.busexpress.network.transfer.TransferApiService
import com.xuannie.busexpress.network.transfer.TransferSuggestionRequestDto
import kotlin.String

data class TransferPlanResult(
    val suggestion: TransferSuggestion,
    val baselineTimeline: List<TimelineStop>,
    val transferTimeline: List<TimelineStop>
)

interface TransferRepository {
    suspend fun getTransferPlan(
        apiTimestamp: String,
        originStopCode: String,
        destinationStopCodes: List<String>,
        baselineServiceNo: String,
        baselineDirection: Int
    ): TransferPlanResult
}

class DefaultTransferRepository(
    private val transferApiService: TransferApiService
) : TransferRepository {

    @kotlinx.serialization.InternalSerializationApi
    override suspend fun getTransferPlan(
        apiTimestamp: String,
        originStopCode: String,
        destinationStopCodes: List<String>,
        baselineServiceNo: String,
        baselineDirection: Int
    ): TransferPlanResult {
        val request = TransferSuggestionRequestDto(
            apiTimestamp = apiTimestamp,
            originStopCode = originStopCode,
            destinationStopCodes = destinationStopCodes,
            baselineServiceNo = baselineServiceNo,
            baselineDirection = baselineDirection
        )

        val response = transferApiService.getTransferSuggestion(request)

        val suggestion = TransferSuggestion(
            shouldTransfer = response.shouldTransfer,
            baselineService = response.baselineService,
            transferFromService = response.transferFromService,
            transferStopCode = response.transferStopCode,
            transferStopDescription = response.transferStopDescription,
            transferToService = response.transferToService,
            destinationStopCode = response.destinationStopCode,
            directEtaSeconds = response.directEtaSeconds,
            transferEtaSeconds = response.transferEtaSeconds,
            timeSavedSeconds = response.timeSavedSeconds,
            message = response.message
        )

        val baselineTimeline = response.baselineTimeline.map {
            TimelineStop(
                stopCode = it.stopCode,
                stopName = it.stopName,
                arrivalTimestamp = it.arrivalTimestamp,
                serviceNo = it.serviceNo,
                latitude = it.latitude,
                longitude = it.longitude
            )
        }

        val transferTimeline = response.transferTimeline.map {
            TimelineStop(
                stopCode = it.stopCode,
                stopName = it.stopName,
                arrivalTimestamp = it.arrivalTimestamp,
                serviceNo = it.serviceNo,
                latitude = it.latitude,
                longitude = it.longitude
            )
        }

        return TransferPlanResult(
            suggestion = suggestion,
            baselineTimeline = baselineTimeline,
            transferTimeline = transferTimeline
        )
    }
}