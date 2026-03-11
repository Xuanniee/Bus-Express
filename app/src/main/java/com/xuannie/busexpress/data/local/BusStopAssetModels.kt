package com.xuannie.busexpress.data.local

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BusStopsAssetResponse(
    @SerialName("value")
    val value: List<BusStopAssetItem> = emptyList()
)

@Serializable
data class BusStopAssetItem(
    @SerialName("BusStopCode")
    val busStopCode: String,

    @SerialName("RoadName")
    val roadName: String,

    @SerialName("Description")
    val description: String,

    @SerialName("Latitude")
    val latitude: Double,

    @SerialName("Longitude")
    val longitude: Double
)