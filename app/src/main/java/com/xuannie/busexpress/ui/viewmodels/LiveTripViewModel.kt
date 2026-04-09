package com.xuannie.busexpress.ui.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.xuannie.busexpress.BusExpressApplication
import com.xuannie.busexpress.data.local.BusStopAssetRepository
import com.xuannie.busexpress.data.repository.TransferRepository
import com.xuannie.busexpress.domain.model.ActiveTrip
import com.xuannie.busexpress.domain.model.RouteLeg
import com.xuannie.busexpress.domain.model.RoutePoint
import com.xuannie.busexpress.domain.model.TimelineStop
import com.xuannie.busexpress.domain.model.TransferSuggestion
import com.xuannie.busexpress.domain.model.TripProgress
import kotlinx.coroutines.launch
import java.time.OffsetDateTime

enum class JourneyMode {
    BASELINE,
    TRANSFER
}

data class LiveTripUiState(
    val originStopCode: String = "",
    // CHANGED: destination is fixed label, no longer user-entered
    val destinationLabel: String = "Pasir Ris Bus Interchange",

    // CHANGED: user can type initial timestamp
    val inputTimestamp: String = "2026-03-03T08:55:00+00:00",

    val activeTrip: ActiveTrip? = null,
    val tripProgress: TripProgress? = null,
    val suggestion: TransferSuggestion? = null,

    val simulatedTimestamp: String = "",
    val simulatedTimeDisplay: String = "",

    val baselineTimeline: List<TimelineStop> = emptyList(),
    val transferTimeline: List<TimelineStop> = emptyList(),
    val journeyMode: JourneyMode = JourneyMode.BASELINE,

    val currentTimelineIndex: Int = 0,

    // Current rendered marker position - Start at Punggol Bus Stop
    val currentLatitude: Double = 1.398950,
    val currentLongitude: Double = 103.904752,

    val transferDecisionMade: Boolean = false,

    val isLoading: Boolean = false,

    // Snackbar message instead of error card
    val snackbarMessage: String? = null,

    // List of coordinats for user to move from 1 coordiante to another to simulate moving on the road
    val baselineLegs: List<RouteLeg> = emptyList(),
    val transferLegs: List<RouteLeg> = emptyList(),

    // Selected path
    val activeLegPath: List<RoutePoint> = emptyList(),

    // Current cooridiante of road
    val activeLegStepIndex: Int = 0,

    // To update the user theyr eached the destination
    val showArrivalDialog: Boolean = false,
    val arrivalTitle: String = "",
    val arrivalMessage: String = "",
)

class LiveTripViewModel(
    private val transferRepository: TransferRepository,
    private val busStopAssetRepository: BusStopAssetRepository
) : ViewModel() {

    private val _uiState = mutableStateOf(LiveTripUiState())
    val uiState: State<LiveTripUiState> = _uiState

    private val demoBaselineServiceNo = "3"
    private val demoBaselineDirection = 2

    fun updateOriginStopCode(value: String) {
        _uiState.value = _uiState.value.copy(originStopCode = value)
    }

    fun updateInputTimestamp(value: String) {
        _uiState.value = _uiState.value.copy(inputTimestamp = value)
    }

    fun clearSnackbar() {
        _uiState.value = _uiState.value.copy(snackbarMessage = null)
    }

    fun startTrip() {
        val state = _uiState.value
        val origin = state.originStopCode.trim()
        val startIso = state.inputTimestamp.trim()

        if (origin.isBlank() || startIso.isBlank()) {
            _uiState.value = state.copy(
                snackbarMessage = "Please enter origin bus stop and timestamp.",
                showArrivalDialog = false,
                arrivalTitle = "",
                arrivalMessage = "",
                activeLegPath = emptyList(),
                activeLegStepIndex = 0,
            )
            return
        }

        // validate timestamp
        try {
            OffsetDateTime.parse(startIso)
        } catch (e: Exception) {
            _uiState.value = state.copy(
                snackbarMessage = "Timestamp must be ISO format, e.g. 2026-03-03T08:55:00+00:00",
                showArrivalDialog = false,
                arrivalTitle = "",
                arrivalMessage = "",
                activeLegPath = emptyList(),
                activeLegStepIndex = 0
            )
            return
        }

        val activeTrip = ActiveTrip(
            originStopCode = origin,
            destinationStopCode = "PBRI",
            baselineServiceNo = demoBaselineServiceNo,
            baselineDirection = demoBaselineDirection,
            simulatedTimestamp = startIso,
            isActive = true
        )

        _uiState.value = state.copy(
            activeTrip = activeTrip,
            simulatedTimestamp = startIso,
            simulatedTimeDisplay = formatTime(startIso),
            tripProgress = TripProgress(),
            suggestion = null,
            baselineTimeline = emptyList(),
            transferTimeline = emptyList(),
            baselineLegs = emptyList(),
            transferLegs = emptyList(),
            journeyMode = JourneyMode.BASELINE,
            currentTimelineIndex = 0,
            transferDecisionMade = false,
            snackbarMessage = null,
            activeLegPath = emptyList(),
            activeLegStepIndex = 0,
            showArrivalDialog = false,
            arrivalTitle = "",
            arrivalMessage = ""
        )

        fetchTransferPlan()
    }

    /**
     * Helper to get the curr cooridnates when moving along the road
     */
    private fun getCurrentLeg(state: LiveTripUiState): RouteLeg? {
        val timeline = getCurrentTimeline(state)
        val currentIndex = state.currentTimelineIndex
        val nextIndex = currentIndex + 1

        if (currentIndex < 0 || nextIndex >= timeline.size) return null

        val currentStop = timeline[currentIndex]
        val nextStop = timeline[nextIndex]

        val legs = if (
            state.journeyMode == JourneyMode.TRANSFER &&
            state.transferLegs.isNotEmpty()
        ) {
            state.transferLegs
        } else {
            state.baselineLegs
        }

        return legs.find {
            it.fromStopCode == currentStop.stopCode &&
                    it.toStopCode == nextStop.stopCode
        }
    }

    fun simulateTrip() {
        val state = _uiState.value
        val timeline = getCurrentTimeline(state)

        if (timeline.isEmpty()) {
            _uiState.value = state.copy(
                snackbarMessage = "No journey timeline available yet."
            )
            return
        }

        val nextIndex = state.currentTimelineIndex + 1
        if (nextIndex >= timeline.size) {
            _uiState.value = state.copy(
                snackbarMessage = "Trip simulation has reached the final stop."
            )
            return
        }

        val currentLeg = getCurrentLeg(state)
        if (currentLeg == null || currentLeg.pathPoints.isEmpty()) {
            _uiState.value = state.copy(
                snackbarMessage = "No road path found for this leg."
            )
            return
        }

        // Do not jump straight to next stop anymore
        _uiState.value = state.copy(
            activeLegPath = currentLeg.pathPoints
                .let { interpolateRoutePoints(it, stepsPerSegment = 6) },
            activeLegStepIndex = 0,
            snackbarMessage = null
        )
    }

    private fun interpolateRoutePoints(
        points: List<RoutePoint>,
        stepsPerSegment: Int = 5
    ): List<RoutePoint> {
        if (points.size < 2) return points

        val result = mutableListOf<RoutePoint>()

        for (i in 0 until points.lastIndex) {
            val start = points[i]
            val end = points[i + 1]

            result.add(start)

            for (step in 1 until stepsPerSegment) {
                val t = step.toDouble() / stepsPerSegment.toDouble()

                val lat = start.latitude + (end.latitude - start.latitude) * t
                val lon = start.longitude + (end.longitude - start.longitude) * t

                result.add(
                    RoutePoint(
                        latitude = lat,
                        longitude = lon
                    )
                )
            }
        }

        result.add(points.last())
        return result
    }

    fun advanceAlongActiveLeg() {
        val state = _uiState.value
        val path = state.activeLegPath

        if (path.isEmpty()) return

        val nextStep = state.activeLegStepIndex + 1

        // Still travelling along the same road segment
        if (nextStep < path.size) {
            val point = path[nextStep]
            _uiState.value = state.copy(
                activeLegStepIndex = nextStep,
                currentLatitude = point.latitude,
                currentLongitude = point.longitude
            )
            return
        }

        // Finished the road segment, now officially arrive at next stop
        val timeline = getCurrentTimeline(state)
        val nextIndex = state.currentTimelineIndex + 1

        if (nextIndex >= timeline.size) {
            _uiState.value = state.copy(
                activeLegPath = emptyList(),
                activeLegStepIndex = 0,
                snackbarMessage = "Trip simulation has reached the final stop."
            )
            return
        }

        val nextStop = timeline[nextIndex]
        val nextTimestamp = nextStop.arrivalTimestamp
        val reachedFinalStop = nextIndex == timeline.lastIndex

        val updatedTrip = state.activeTrip?.copy(
            simulatedTimestamp = nextTimestamp,
            isActive = !reachedFinalStop
        )

        val finalStopName = nextStop.stopName
        val finalTime = formatTime(nextTimestamp)

        val arrivedState = state.copy(
            activeTrip = updatedTrip,
            simulatedTimestamp = nextTimestamp,
            simulatedTimeDisplay = formatTime(nextTimestamp),
            currentTimelineIndex = nextIndex,
            transferDecisionMade = false,
            activeLegPath = emptyList(),
            activeLegStepIndex = 0,
            snackbarMessage = null,
            showArrivalDialog = reachedFinalStop,
            arrivalTitle = if (reachedFinalStop) "Destination reached" else "",
            arrivalMessage = if (reachedFinalStop) {
                "You have arrived at $finalStopName (${nextStop.stopCode}) at $finalTime."
            } else {
                ""
            }
        )

        val progress = buildTripProgress(arrivedState)
        _uiState.value = arrivedState.copy(
            currentLatitude = progress.first ?: arrivedState.currentLatitude,
            currentLongitude = progress.second ?: arrivedState.currentLongitude,
            tripProgress = progress.third
        )
    }

    fun stopTrip() {
        _uiState.value = _uiState.value.copy(
            activeTrip = _uiState.value.activeTrip?.copy(isActive = false),
            activeLegPath = emptyList(),
            activeLegStepIndex = 0
        )
    }

    /**
     * Function to close alert dialog after reachign destination
     */
    fun dismissArrivalDialog() {
        _uiState.value = _uiState.value.copy(
            showArrivalDialog = false,
            arrivalTitle = "",
            arrivalMessage = ""
        )
    }

    fun useBaselineJourney() {
        val state = _uiState.value
        val currentStopCode = state.tripProgress?.currentStopCode ?: return
        val baseline = state.baselineTimeline
        if (baseline.isEmpty()) return

        val alignedIndex = baseline.indexOfLast { it.stopCode == currentStopCode }
            .takeIf { it >= 0 } ?: 0

        val alignedStop = baseline[alignedIndex]
        val updatedState = state.copy(
            journeyMode = JourneyMode.BASELINE,
            currentTimelineIndex = alignedIndex,
            simulatedTimestamp = alignedStop.arrivalTimestamp,
            simulatedTimeDisplay = formatTime(alignedStop.arrivalTimestamp),
            transferDecisionMade = true
        )

        val progress = buildTripProgress(updatedState)
        _uiState.value = updatedState.copy(
            currentLatitude = progress.first ?: updatedState.currentLatitude,
            currentLongitude = progress.second ?: updatedState.currentLongitude,
            tripProgress = progress.third
        )
    }

    fun useTransferJourney() {
        val state = _uiState.value
        val currentStopCode = state.tripProgress?.currentStopCode ?: return
        val transfer = state.transferTimeline
        if (transfer.isEmpty()) return

        val alignedIndex = transfer.indexOfLast { it.stopCode == currentStopCode }
            .takeIf { it >= 0 } ?: 0

        val alignedStop = transfer[alignedIndex]
        val updatedState = state.copy(
            journeyMode = JourneyMode.TRANSFER,
            currentTimelineIndex = alignedIndex,
            simulatedTimestamp = alignedStop.arrivalTimestamp,
            simulatedTimeDisplay = formatTime(alignedStop.arrivalTimestamp),
            transferDecisionMade = true
        )

        val progress = buildTripProgress(updatedState)
        _uiState.value = updatedState.copy(
            currentLatitude = progress.first ?: updatedState.currentLatitude,
            currentLongitude = progress.second ?: updatedState.currentLongitude,
            tripProgress = progress.third
        )
    }

    private fun fetchTransferPlan() {
        val trip = _uiState.value.activeTrip ?: return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                snackbarMessage = null
            )

            try {
                val result = transferRepository.getTransferPlan(
                    apiTimestamp = trip.simulatedTimestamp,
                    originStopCode = trip.originStopCode,
                    destinationStopCodes = listOf("77009", "77039"),
                    baselineServiceNo = trip.baselineServiceNo,
                    baselineDirection = trip.baselineDirection
                )

                val baselineTimeline = result.baselineTimeline
                val firstStop = baselineTimeline.firstOrNull()

                val nextState = _uiState.value.copy(
                    isLoading = false,
                    suggestion = result.suggestion,
                    baselineTimeline = result.baselineTimeline,
                    transferTimeline = result.transferTimeline,
                    baselineLegs = result.baselineLegs,
                    transferLegs = result.transferLegs,
                    journeyMode = JourneyMode.BASELINE,
                    currentTimelineIndex = 0,
                    simulatedTimestamp = firstStop?.arrivalTimestamp ?: trip.simulatedTimestamp,
                    simulatedTimeDisplay = formatTime(firstStop?.arrivalTimestamp ?: trip.simulatedTimestamp),
                    transferDecisionMade = false,
                    snackbarMessage = null,
                )

                val updatedTrip = nextState.activeTrip?.copy(
                    simulatedTimestamp = nextState.simulatedTimestamp
                )

                val progress = buildTripProgress(nextState.copy(activeTrip = updatedTrip))

                _uiState.value = nextState.copy(
                    activeTrip = updatedTrip,
                    currentLatitude = progress.first ?: nextState.currentLatitude,
                    currentLongitude = progress.second ?: nextState.currentLongitude,
                    tripProgress = progress.third
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    snackbarMessage = e.message ?: "Failed to fetch transfer plan."
                )
            }
        }
    }

    private fun getCurrentTimeline(state: LiveTripUiState): List<TimelineStop> {
        return if (
            state.journeyMode == JourneyMode.TRANSFER &&
            state.transferTimeline.isNotEmpty()
        ) state.transferTimeline else state.baselineTimeline
    }

    private fun buildTripProgress(
        state: LiveTripUiState
    ): Triple<Double?, Double?, TripProgress> {
        val timeline = getCurrentTimeline(state)
        if (timeline.isEmpty()) {
            return Triple(null, null, TripProgress())
        }

        val currentIndex = state.currentTimelineIndex.coerceIn(0, timeline.lastIndex)
        val currentStop = timeline[currentIndex]
        val nextStop = timeline.getOrNull(currentIndex + 1)

        val currentLocal = busStopAssetRepository.getByCode(currentStop.stopCode)
        val nextLocal = busStopAssetRepository.getByCode(nextStop?.stopCode)

        val currentName =
            if (currentStop.stopName.isBlank() || currentStop.stopName == currentStop.stopCode)
                currentLocal?.description ?: currentStop.stopName
            else currentStop.stopName

        val nextName =
            when {
                nextStop == null -> null
                nextStop.stopName.isBlank() || nextStop.stopName == nextStop.stopCode ->
                    nextLocal?.description ?: nextStop.stopName
                else -> nextStop.stopName
            }

        val lat = currentStop.latitude ?: currentLocal?.latitude
        val lng = currentStop.longitude ?: currentLocal?.longitude

        val progress = TripProgress(
            currentStopCode = currentStop.stopCode,
            currentStopName = currentName,
            nextStopCode = nextStop?.stopCode,
            nextStopName = nextName,
            stopsRemainingOnBaseline = (timeline.size - 1 - currentIndex).coerceAtLeast(0)
        )

        return Triple(lat, lng, progress)
    }

    fun shouldShowTransferChoice(): Boolean {
        val state = _uiState.value
        val suggestion = state.suggestion ?: return false
        val currentStopCode = state.tripProgress?.currentStopCode ?: return false

        return suggestion.shouldTransfer &&
                state.journeyMode == JourneyMode.BASELINE &&
                !state.transferDecisionMade &&
                suggestion.transferStopCode != null &&
                suggestion.transferStopCode == currentStopCode
    }

    fun getBaselineArrivalDisplay(): String {
        val last = _uiState.value.baselineTimeline.lastOrNull()?.arrivalTimestamp ?: return "N.A."
        return formatTime(last)
    }

    fun getTransferArrivalDisplay(): String {
        val last = _uiState.value.transferTimeline.lastOrNull()?.arrivalTimestamp ?: return "N.A."
        return formatTime(last)
    }

    private fun formatTime(timestamp: String): String {
        return try {
            OffsetDateTime.parse(timestamp)
                .toLocalTime()
                .withSecond(0)
                .withNano(0)
                .toString()
        } catch (_: Exception) {
            timestamp
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as BusExpressApplication)
                val transferRepository = application.container.transferRepository
                val busStopAssetRepository = application.container.busStopAssetRepository
                LiveTripViewModel(
                    transferRepository = transferRepository,
                    busStopAssetRepository = busStopAssetRepository
                )
            }
        }
    }
}