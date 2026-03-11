package com.xuannie.busexpress.domain.usecase

import com.xuannie.busexpress.network.*

class TransferPlanner(
    private val busRoutes: List<BusStopInRoute>
) {
    private val groupedRoutes: Map<Pair<String, Int>, List<BusStopInRoute>> =
        busRoutes.groupBy { it.serviceNo to it.routeDirection }
            .mapValues { (_, stops) -> stops.sortedBy { it.stopSequence } }

    fun getServicesAtStop(busStopCode: String): List<Pair<String, Int>> {
        return groupedRoutes
            .filter { (_, stops) -> stops.any { it.busStopCode == busStopCode } }
            .keys
            .toList()
    }

    fun routeCanReach(
        serviceNo: String,
        direction: Int,
        fromStop: String,
        toStop: String
    ): Boolean {
        val route = groupedRoutes[serviceNo to direction] ?: return false
        val fromIdx = route.indexOfFirst { it.busStopCode == fromStop }
        val toIdx = route.indexOfFirst { it.busStopCode == toStop }
        return fromIdx != -1 && toIdx != -1 && toIdx > fromIdx
    }

    fun stopsBetween(
        serviceNo: String,
        direction: Int,
        fromStop: String,
        toStop: String
    ): Int? {
        val route = groupedRoutes[serviceNo to direction] ?: return null
        val fromIdx = route.indexOfFirst { it.busStopCode == fromStop }
        val toIdx = route.indexOfFirst { it.busStopCode == toStop }
        if (fromIdx == -1 || toIdx == -1 || toIdx <= fromIdx) return null
        return toIdx - fromIdx
    }

    fun downstreamStops(
        serviceNo: String,
        direction: Int,
        fromStop: String
    ): List<BusStopInRoute> {
        val route = groupedRoutes[serviceNo to direction] ?: return emptyList()
        val fromIdx = route.indexOfFirst { it.busStopCode == fromStop }
        if (fromIdx == -1) return emptyList()
        return route.drop(fromIdx + 1)
    }
}