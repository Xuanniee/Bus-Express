package com.example.busexpress.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SingaporeBus(
    @SerialName(value = "odata.metadata")
    val metaData: String,

    @SerialName(value = "BusStopCode")
    val busStopCode: String,

    @SerialName(value = "Services")
    val services: List<SingaporeBusServices?>,
)

@Serializable
data class SingaporeBusServices(
    @SerialName(value = "ServiceNo")
    val busServiceNumber: String?,

    @SerialName(value = "Operator")
    val busOperator: String?,

    // Bus Arrival Timings
    @SerialName(value = "NextBus")
    val nextBus1: NextBusTiming?,

    @SerialName(value = "NextBus2")
    val nextBus2: NextBusTiming?,

    @SerialName(value = "NextBus3")
    val nextBus3: NextBusTiming?
)

@Serializable
data class NextBusTiming(
    // Date-Time expressed in the UTC standard, GMT+8 for Singapore Standard Time (SST)
    @SerialName(value = "OriginCode")
    val startingBusStop: String?,

    @SerialName(value = "DestinationCode")
    val endingBusStop: String?,

    @SerialName(value = "EstimatedArrival")
    val estimatedArrival: String?,

    // Bus Approximate Location
    @SerialName(value = "Latitude")
    val latitude: String?,

    @SerialName(value = "Longitude")
    val longitude: String?,

    @SerialName(value = "VisitNumber")
    val visitNumber: String?,

    // Current Bus Occupancy Levels
    @SerialName(value = "Load")
    val busOccupancyLevels: String?,

    // Wheelchair Support
    @SerialName(value = "Feature")
    val wheelchairAccessible: String?,

    // Bus Type
    @SerialName(value = "Type")
    val vehicleType: String?,
)

