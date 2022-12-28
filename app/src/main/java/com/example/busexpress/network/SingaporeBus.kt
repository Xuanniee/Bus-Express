package com.example.busexpress.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Bus Arrival Timings API Data Classes
 */
@Serializable
data class SingaporeBus(
    @SerialName(value = "odata.metadata")
    val metaData: String = "Default Placeholder",

    @SerialName(value = "BusStopCode")
    val busStopCode: String = "Default Placeholder",

    @SerialName(value = "Services")
    val services: List<SingaporeBusServices> = listOf(SingaporeBusServices()),
)

@Serializable
data class SingaporeBusServices(
    @SerialName(value = "ServiceNo")
    val busServiceNumber: String = "No Bus Service Available",

    @SerialName(value = "Operator")
    val busOperator: String = "No Bus Service Available",

    // Bus Arrival Timings
    @SerialName(value = "NextBus")
    val nextBus1: NextBusTiming = NextBusTiming(),

    @SerialName(value = "NextBus2")
    val nextBus2: NextBusTiming = NextBusTiming(),

    @SerialName(value = "NextBus3")
    val nextBus3: NextBusTiming = NextBusTiming()
)

@Serializable
data class NextBusTiming(
    // Date-Time expressed in the UTC standard, GMT+8 for Singapore Standard Time (SST)
    @SerialName(value = "OriginCode")
    val startingBusStop: String = "No Bus Service Available",

    @SerialName(value = "DestinationCode")
    val endingBusStop: String = "No Bus Service Available",

    @SerialName(value = "EstimatedArrival")
    val estimatedArrival: String = "No Bus Service Available",

    // Bus Approximate Location
    @SerialName(value = "Latitude")
    val latitude: String = "No Bus Service Available",

    @SerialName(value = "Longitude")
    val longitude: String = "No Bus Service Available",

    @SerialName(value = "VisitNumber")
    val visitNumber: String = "No Bus Service Available",

    // Current Bus Occupancy Levels
    @SerialName(value = "Load")
    val busOccupancyLevels: String = "No Bus Service Available",

    // Wheelchair Support
    @SerialName(value = "Feature")
    val wheelchairAccessible: String = "No Bus Service Available",

    // Bus Type
    @SerialName(value = "Type")
    val vehicleType: String = "No Bus Service Available",
)

/**
 * Bus Stop Details API Data Classes
 */
@Serializable
data class BusStop(
    @SerialName(value = "odata.metadata")
    val metaData: String,

    // Can be Null if no Bus Stops
    val value: List<BusStopValue> = listOf(BusStopValue())
)

@Serializable
data class BusStopValue(
    @SerialName(value = "BusStopCode")
    val busStopCode: String = "Bus Stop Not Found",

    @SerialName(value = "RoadName")
    val busStopRoadName: String = "NA",

    @SerialName(value = "Description")
    val busStopDescription: String = "NA",

    @SerialName(value = "Latitude")
    val latitude: Double = 0.0,

    @SerialName(value = "Longitude")
    val longitude: Double = 0.0,
)






