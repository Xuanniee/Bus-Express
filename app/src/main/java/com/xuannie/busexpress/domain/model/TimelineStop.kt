package com.xuannie.busexpress.domain.model

data class TimelineStop(
    val stopCode: String,
    val stopName: String,
    val arrivalTimestamp: String,
    val serviceNo: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null
)