package com.xuannie.busexpress.data.repository

import com.xuannie.busexpress.domain.model.RouteLeg
import com.xuannie.busexpress.domain.model.RoutePoint
import com.xuannie.busexpress.domain.model.TimelineStop
import com.xuannie.busexpress.domain.model.TransferSuggestion
import com.xuannie.busexpress.network.transfer.TransferApiService
import com.xuannie.busexpress.network.transfer.TransferSuggestionRequestDto
import retrofit2.HttpException
import kotlin.String

data class TransferPlanResult(
    val suggestion: TransferSuggestion,
    val baselineTimeline: List<TimelineStop>,
    val transferTimeline: List<TimelineStop>,
    val baselineLegs: List<RouteLeg>,
    val transferLegs: List<RouteLeg>
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

        val response = try {
            transferApiService.getTransferSuggestion(request)
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            throw RuntimeException("HTTP ${e.code()}: ${errorBody ?: e.message()}")
        }


//        val response = transferApiService.getTransferSuggestion(request)

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

        val baselineLegs = response.baselineLegs.map { leg ->
            RouteLeg(
                fromStopCode = leg.fromStopCode,
                toStopCode = leg.toStopCode,
                pathPoints = leg.pathPoints.map { p ->
                    RoutePoint(
                        latitude = p.latitude,
                        longitude = p.longitude
                    )
                }
            )
        }

        val transferLegs = response.transferLegs.map { leg ->
            RouteLeg(
                fromStopCode = leg.fromStopCode,
                toStopCode = leg.toStopCode,
                pathPoints = leg.pathPoints.map { p ->
                    RoutePoint(
                        latitude = p.latitude,
                        longitude = p.longitude
                    )
                }
            )
        }

        return TransferPlanResult(
            suggestion = suggestion,
            baselineTimeline = baselineTimeline,
            transferTimeline = transferTimeline,
            baselineLegs = baselineLegs,
            transferLegs = transferLegs,
        )
    }
}