package com.xuannie.busexpress.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Bus Arrival Timings API Data Classes
 */
@Serializable
data class SingaporeBus(
    @SerialName(value = "odata.metadata")
    val metaData: String = "http://datamall2.mytransport.sg/ltaodataservice/${'$'}metadata#BusArrivalv2/@Element",

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
    val metaData: String = "http://datamall2.mytransport.sg/ltaodataservice/${'$'}metadata#BusStops",

    // No Bus Stops should be an Empty List and not Null
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

/**
 *  API Call to get all Bus Routes in Singapore
 */

@Serializable
data class BusRoutes(
    @SerialName(value = "odata.metadata")
    val metaData: String = "http://datamall2.mytransport.sg/ltaodataservice/${'$'}metadataBusRoutes",

    @SerialName(value = "value")
    val busRouteArray: List<BusStopInRoute> = listOf(BusStopInRoute())
)

@Serializable
data class BusStopInRoute(
    @SerialName(value = "ServiceNo")
    val serviceNo: String = "Bus Service not found!!",

    @SerialName(value = "Operator")
    val busOperator: String = "NA",

    // Either 1 or 2 Directions only
    @SerialName(value = "Direction")
    val routeDirection: Int = 1,

    @SerialName(value = "StopSequence")
    val stopSequence: Int = 1,

    @SerialName(value = "BusStopCode")
    val busStopCode: String = "NA",

    @SerialName(value = "Distance")
    val distanceTravelled: Double = 0.0,

    @SerialName(value = "WD_FirstBus")
    val weekdayFirstBusTime: String = "NA",

    @SerialName(value = "WD_LastBus")
    val weekdayLastBusTime: String = "NA",

    @SerialName(value = "SAT_FirstBus")
    val saturdayFirstBusTime: String = "NA",

    @SerialName(value = "SAT_LastBus")
    val saturdayLastBusTime: String = "NA",

    @SerialName(value = "SUN_FirstBus")
    val sundayFirstBusTime: String = "NA",

    @SerialName(value = "SUN_LastBus")
    val sundayLastBusTime: String = "NA",
)

/**
 * Data Class to hold result of parsed User Input
 */
data class UserInputResult(
    val busStopCodeBool: Boolean,
    val busServiceBool: Boolean,
    val busStopCode: String?,
    val busServiceNo: String?
)

/**
 * Data Class to Hold Bus Arrival Features
 */
data class BusOccupancyResults(
    val nextBusOccupancyArray: Array<Int>,
    val nextBusOccupancyDescArray: Array<Int>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BusOccupancyResults

        if (!nextBusOccupancyArray.contentEquals(other.nextBusOccupancyArray)) return false
        if (!nextBusOccupancyDescArray.contentEquals(other.nextBusOccupancyDescArray)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = nextBusOccupancyArray.contentHashCode()
        result = 31 * result + nextBusOccupancyDescArray.contentHashCode()
        return result
    }
}

/**
 * Data Class to Hold all the Bus Stops in the Route
 */
data class BusServicesRoute(
    val busArrivalsJSONList: MutableList<SingaporeBus> = mutableListOf(SingaporeBus()),
    val busStopDetailsJSONList: MutableList<BusStopValue> = mutableListOf(BusStopValue()),
    val busRoutesList: List<BusStopInRoute> = listOf(BusStopInRoute()),
)