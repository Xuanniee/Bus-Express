package com.xuannie.busexpress.domain.model

/**
 * Represents the current recommendation for whether the user
 * should remain on the baseline bus or transfer to another bus.
 */
data class TransferSuggestion(
    val shouldTransfer: Boolean,
    val baselineService: String? = null,
    val transferFromService: String? = null,
    val transferStopCode: String? = null,
    val transferStopDescription: String? = null,
    val transferToService: String? = null,
    val destinationStopCode: String,
    val directEtaSeconds: Int? = null,
    val transferEtaSeconds: Int? = null,
    val timeSavedSeconds: Int = 0,
    val message: String = ""
)