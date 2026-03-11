package com.xuannie.busexpress.domain.model

/**
 * Represents the latest known progress of the user's live bus trip.
 *
 * currentStopCode:
 * - the stop most recently reached
 *
 * nextStopCode:
 * - the next expected stop on the baseline route, if known
 *
 * stopsRemainingOnBaseline:
 * - number of remaining stops if the user simply stays on the baseline bus
 */
data class TripProgress(
    val currentStopCode: String? = null,
    val currentStopName: String? = null,
    val nextStopCode: String? = null,
    val nextStopName: String? = null,
    val stopsRemainingOnBaseline: Int? = null
)