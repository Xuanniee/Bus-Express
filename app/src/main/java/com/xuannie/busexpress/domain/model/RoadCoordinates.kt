package com.xuannie.busexpress.domain.model

data class RoutePoint(
    val latitude: Double,
    val longitude: Double
)

data class RouteLeg(
    val fromStopCode: String,
    val toStopCode: String,
    val pathPoints: List<RoutePoint>
)