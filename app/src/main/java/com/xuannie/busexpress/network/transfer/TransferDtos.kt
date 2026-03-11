package com.xuannie.busexpress.network.transfer

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@kotlinx.serialization.InternalSerializationApi
data class TransferSuggestionRequestDto(
    @SerialName("api_timestamp")
    val apiTimestamp: String,

    @SerialName("origin_stop_code")
    val originStopCode: String,

    @SerialName("destination_stop_codes")
    val destinationStopCodes: List<String>,

    @SerialName("baseline_service_no")
    val baselineServiceNo: String,

    @SerialName("baseline_direction")
    val baselineDirection: Int
)

@Serializable
@kotlinx.serialization.InternalSerializationApi
data class TimelineStopDto(
    @SerialName("stop_code")
    val stopCode: String,

    @SerialName("stop_name")
    val stopName: String,

    @SerialName("arrival_timestamp")
    val arrivalTimestamp: String,

    @SerialName("service_no")
    val serviceNo: String? = null,

    @SerialName("latitude")
    val latitude: Double? = null,

    @SerialName("longitude")
    val longitude: Double? = null
)

@Serializable
@kotlinx.serialization.InternalSerializationApi
data class TransferSuggestionResponseDto(
    @SerialName("should_transfer")
    val shouldTransfer: Boolean,

    @SerialName("baseline_service")
    val baselineService: String? = null,

    @SerialName("transfer_from_service")
    val transferFromService: String? = null,

    @SerialName("transfer_stop_code")
    val transferStopCode: String? = null,

    @SerialName("transfer_stop_description")
    val transferStopDescription: String? = null,

    @SerialName("transfer_to_service")
    val transferToService: String? = null,

    @SerialName("destination_stop_code")
    val destinationStopCode: String,

    @SerialName("direct_eta_seconds")
    val directEtaSeconds: Int? = null,

    @SerialName("transfer_eta_seconds")
    val transferEtaSeconds: Int? = null,

    @SerialName("time_saved_seconds")
    val timeSavedSeconds: Int = 0,

    @SerialName("message")
    val message: String = "",

    @SerialName("baseline_timeline")
    val baselineTimeline: List<TimelineStopDto> = emptyList(),

    @SerialName("transfer_timeline")
    val transferTimeline: List<TimelineStopDto> = emptyList()
)