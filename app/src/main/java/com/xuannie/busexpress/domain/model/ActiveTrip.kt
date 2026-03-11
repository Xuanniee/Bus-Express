package com.xuannie.busexpress.domain.model

/**
 * Represents a live bus journey that the user has started tracking.
 *
 * originStopCode:
 * - where the trip began
 *
 * destinationStopCode:
 * - final intended alighting stop
 *
 * baselineServiceNo / baselineDirection:
 * - the bus the user is currently riding as the baseline journey
 *
 * currentStopCode:
 * - latest known stop that the user/bus has reached
 *
 * isActive:
 * - whether this live trip session is still ongoing
 */

data class ActiveTrip(
    val originStopCode: String,
    val destinationStopCode: String,
    val baselineServiceNo: String,
    val baselineDirection: Int,
    val simulatedTimestamp: String,
    val isActive: Boolean = true
)