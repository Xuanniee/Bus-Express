package com.xuannie.busexpress.data.local

import android.content.Context
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString

data class BusStopLocalInfo(
    val busStopCode: String,
    val roadName: String,
    val description: String,
    val latitude: Double,
    val longitude: Double
)

class BusStopAssetRepository(
    context: Context
) {
    private val json = Json { ignoreUnknownKeys = true }

    private val busStopMap: Map<String, BusStopLocalInfo> by lazy {
        val raw = context.assets.open("BusStops.json")
            .bufferedReader()
            .use { it.readText() }

        val parsed = json.decodeFromString<BusStopsAssetResponse>(raw)

        parsed.value.associate { item ->
            item.busStopCode to BusStopLocalInfo(
                busStopCode = item.busStopCode,
                roadName = item.roadName,
                description = item.description,
                latitude = item.latitude,
                longitude = item.longitude
            )
        }
    }

    fun getByCode(busStopCode: String?): BusStopLocalInfo? {
        if (busStopCode.isNullOrBlank()) return null
        return busStopMap[busStopCode]
    }
}