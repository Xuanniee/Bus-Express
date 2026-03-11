package com.xuannie.busexpress.domain.usecase

import com.xuannie.busexpress.data.SingaporeBusRepository
import com.xuannie.busexpress.domain.model.TransferSuggestion
import com.xuannie.busexpress.network.BusStopValue

class GetTransferSuggestionUseCase(
    private val repository: SingaporeBusRepository,
    private val planner: TransferPlanner,
    private val busStopLookup: Map<String, BusStopValue>,
    private val safetyMarginSeconds: Int = 180,
    private val secondsPerStop: Int = 90
) {
    suspend fun suggest(
        originStopCode: String,
        destinationStopCode: String
    ): TransferSuggestion {
        val originArrival = repository.getBusTimings(originStopCode, null)

        val originServices = planner.getServicesAtStop(originStopCode)
        var bestDirectService: String? = null
        var bestDirectEta: Int? = null

        for ((serviceNo, direction) in originServices) {
            if (!planner.routeCanReach(serviceNo, direction, originStopCode, destinationStopCode)) continue

            val wait = TransferEtaUtils.nextWaitForService(originArrival, serviceNo) ?: continue
            val stopCount = planner.stopsBetween(serviceNo, direction, originStopCode, destinationStopCode) ?: continue
            val ride = TransferEtaUtils.estimateRideSeconds(stopCount, secondsPerStop)
            val total = wait + ride

            if (bestDirectEta == null || total < bestDirectEta!!) {
                bestDirectEta = total
                bestDirectService = serviceNo
            }
        }

        var bestTransferFrom: String? = null
        var bestTransferTo: String? = null
        var bestTransferStop: String? = null
        var bestTransferEta: Int? = null

        for ((service1, dir1) in originServices) {
            val wait1 = TransferEtaUtils.nextWaitForService(originArrival, service1) ?: continue
            val downstream = planner.downstreamStops(service1, dir1, originStopCode)

            for (transferStop in downstream) {
                val transferStopCode = transferStop.busStopCode
                val leg1Stops = planner.stopsBetween(service1, dir1, originStopCode, transferStopCode) ?: continue
                val ride1 = TransferEtaUtils.estimateRideSeconds(leg1Stops, secondsPerStop)

                val transferArrival = repository.getBusTimings(transferStopCode, null)
                val servicesAtTransfer = planner.getServicesAtStop(transferStopCode)

                for ((service2, dir2) in servicesAtTransfer) {
                    if (service2 == service1) continue
                    if (!planner.routeCanReach(service2, dir2, transferStopCode, destinationStopCode)) continue

                    val wait2 = TransferEtaUtils.nextWaitForService(transferArrival, service2) ?: continue
                    val leg2Stops = planner.stopsBetween(service2, dir2, transferStopCode, destinationStopCode) ?: continue
                    val ride2 = TransferEtaUtils.estimateRideSeconds(leg2Stops, secondsPerStop)

                    val total = wait1 + ride1 + wait2 + ride2

                    if (bestTransferEta == null || total < bestTransferEta!!) {
                        bestTransferEta = total
                        bestTransferFrom = service1
                        bestTransferTo = service2
                        bestTransferStop = transferStopCode
                    }
                }
            }
        }

        if (bestTransferEta == null) {
            return TransferSuggestion(
                shouldTransfer = false,
                baselineService = bestDirectService,
                destinationStopCode = destinationStopCode,
                directEtaSeconds = bestDirectEta,
                message = if (bestDirectService != null) {
                    "No faster transfer found. Stay on bus $bestDirectService."
                } else {
                    "No direct or faster transfer route found."
                }
            )
        }

        val shouldTransfer = bestDirectEta == null || bestTransferEta!! + safetyMarginSeconds < bestDirectEta!!
        val transferStopDesc = bestTransferStop?.let { busStopLookup[it]?.busStopDescription }

        return if (shouldTransfer) {
            TransferSuggestion(
                shouldTransfer = true,
                baselineService = bestDirectService,
                transferFromService = bestTransferFrom,
                transferStopCode = bestTransferStop,
                transferStopDescription = transferStopDesc,
                transferToService = bestTransferTo,
                destinationStopCode = destinationStopCode,
                directEtaSeconds = bestDirectEta,
                transferEtaSeconds = bestTransferEta,
                timeSavedSeconds = if (bestDirectEta != null) bestDirectEta!! - bestTransferEta!! else 0,
                message = "Transfer from bus $bestTransferFrom to bus $bestTransferTo at ${transferStopDesc ?: bestTransferStop} to save about ${((if (bestDirectEta != null) bestDirectEta!! - bestTransferEta!! else 0) / 60)} min."
            )
        } else {
            TransferSuggestion(
                shouldTransfer = false,
                baselineService = bestDirectService,
                transferFromService = bestTransferFrom,
                transferStopCode = bestTransferStop,
                transferStopDescription = transferStopDesc,
                transferToService = bestTransferTo,
                destinationStopCode = destinationStopCode,
                directEtaSeconds = bestDirectEta,
                transferEtaSeconds = bestTransferEta,
                timeSavedSeconds = if (bestDirectEta != null) bestDirectEta!! - bestTransferEta!! else 0,
                message = if (bestDirectService != null) {
                    "Stay on bus $bestDirectService. Transfer is not sufficiently faster."
                } else {
                    "A transfer exists, but it is not clearly better."
                }
            )
        }
    }
}