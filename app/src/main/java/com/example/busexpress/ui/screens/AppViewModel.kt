package com.example.busexpress.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.busexpress.BusExpressApplication
import com.example.busexpress.data.SingaporeBusRepository
import com.example.busexpress.determineBusServiceorStop
import com.example.busexpress.network.*
import com.example.busexpress.ui.favouriteBusStops.FavouriteBusStopViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import retrofit2.HttpException
import java.io.IOException


/**
 * [AppViewModel] holds information about a cupcake order in terms of quantity, flavor, and
 * pickup date. It also knows how to calculate the total price based on these order details.
 */
class AppViewModel(private val singaporeBusRepository: SingaporeBusRepository): ViewModel() {
    /**
     *  StateFlows to store the Data of API Calls
     */
    private val _busServiceUiState = MutableStateFlow(SingaporeBus())
    val busServiceUiState: StateFlow<SingaporeBus> = _busServiceUiState.asStateFlow()

    private val _busStopNameUiState = MutableStateFlow(BusStopValue())
    val busStopNameUiState: StateFlow<BusStopValue> = _busStopNameUiState.asStateFlow()

    private val _busRouteUiState = MutableStateFlow(BusRoutes())
    val busRouteUiState: StateFlow<BusRoutes> = _busRouteUiState.asStateFlow()

    private val _multipleBusUiState = MutableStateFlow(BusServicesRoute())
    val multipleBusUiState: StateFlow<BusServicesRoute> = _multipleBusUiState.asStateFlow()

    private val _multipleBusStopNameUiState = MutableStateFlow(BusStopValue())
    val multipleBusStopNameUiState: StateFlow<BusStopValue> = _multipleBusStopNameUiState.asStateFlow()

    /**
     * StateFlow
     */

    /** The mutable State that stores the status of the most recent request */
    var busUiState: BusUiState by mutableStateOf(BusUiState.Loading)     // Loading as Default Value
        // Setter is private to protect writes to the busUiState
        private set

    private var busNameUiState: BusStopNameUiState by mutableStateOf(BusStopNameUiState.Loading)

    private var busRoutingUiState: BusRouteUiState by mutableStateOf(BusRouteUiState.Loading)

    // Boolean to determine if we should show Bus Stops or Routes
    var busServiceBoolUiState: Boolean by mutableStateOf(false)

    /**
     * Call init so we can display status immediately.
     */
    init {

    }

    /**
     *  Function to get Bus Timings depending on Bus Service No. or Bus Stop Code
     */
    fun determineUserQuery(userInput: String) {
        // Determine if User Provided a Bus Service No. or Bus Stop Code
        val userInputResult = determineBusServiceorStop(userInput)
        viewModelScope.launch {
            if (userInputResult.busServiceBool) {
                busServiceBoolUiState = true
                // Clear the Screen First
                busUiState = BusUiState.Loading
                // Provided Bus Service, Need get Route first
                getBusRoutes(targetBusService = userInputResult.busServiceNo)
                // Get the Bus Timing for Each Route
                getMultipleBusTimings(busRoutes = _busRouteUiState.value.busRouteArray)
            } else {
                busServiceBoolUiState = false
                // Clear the Screen First
                busUiState = BusUiState.Loading
                // Provided Bus Stop Code
                getBusStopNames(targetBusStopCode = userInputResult.busStopCode?.toInt())
                getBusTimings(userInput = userInputResult.busStopCode)
            }
        }
    }

    private suspend fun getBusRoutes(targetBusService: String?) {
        busRoutingUiState = BusRouteUiState.Loading
        busRoutingUiState = try {
            // Goal: To get the Route, i.e. all the Bus Stop Codes for a SINGLE Bus Service
            var skipIndex = 0
            val targetServiceRoute = mutableListOf<BusStopInRoute>()
            var targetRouteFound = false

            do {
                // Variables to hold Conditions
                var iteratorIndex = 0
                var completedRoute = false

                // API Call
                val listResult = singaporeBusRepository.getBusRoutes(skipIndex)
                val busStopRoute = listResult.busRouteArray
                val busStopRouteLength = busStopRoute.size - 1

                // 500 since according to LTA Record each API Call is confined to 500 Records
                for (i in 0..busStopRouteLength) {
                    // Must compare string since Bus Services like 901M exist
                    if (busStopRoute[iteratorIndex].serviceNo == targetBusService) {
                        // Found a Bus Stop of Route, append to Result Array
                        targetServiceRoute.add(busStopRoute[iteratorIndex])
                        if (busStopRoute[iteratorIndex].stopSequence != 1) {
                            targetRouteFound = true
                        }
                    }
                    // Condition to Check if Route is Complete
                    else if (targetRouteFound && (busStopRoute[iteratorIndex].stopSequence == 1)) {
                        // Passed the Target Route alr
                        completedRoute = true
                        break
                    }
                    iteratorIndex += 1
                }

                // Stopping Condition
                if (completedRoute) {
                    _busRouteUiState.value = BusRoutes(
                        metaData = listResult.metaData,
                        busRouteArray = targetServiceRoute
                    )
                    // For Loop to get all the Arrival Timings for all Bus Stops in the Route
                    break
                } else {
                    skipIndex += 500
                }
            } while (true)

            BusRouteUiState.Success(targetServiceRoute)
        } catch (e: IOException) {
            BusRouteUiState.Error
        } catch (e: HttpException) {
            BusRouteUiState.Error
        }
    }

    // TODO Make it async and await()
    private suspend fun getBusStopNames(targetBusStopCode: Int?, multipleBusBool: Boolean = false) {
        busNameUiState = BusStopNameUiState.Loading
        busNameUiState = try {
            var targetBusStop = BusStopValue()
            var targetBusStopFound = false
            var skipIndex = 0

            // Retrieve the Desired Bus Stop Object
            do {
                val listResult = singaporeBusRepository.getBusDetails(numRecordsToSkip = skipIndex)

                val busStopDetails = listResult.value
                val forLoopSize = busStopDetails.size
                var indexBSD = 0

                // Loop through the 500 Records of this Call to see if the Bus Stop we want is inside
                for(i in 1..forLoopSize) {
                    if (busStopDetails[indexBSD].busStopCode.toInt() == targetBusStopCode) {
                        targetBusStopFound = true
                        break
                    }
                    // Update to check every Record of API Call
                    indexBSD += 1
                }

                // Check if the Bus Stop we want is in this API Call
                if (targetBusStopFound) {
                    targetBusStop = busStopDetails[indexBSD]
                }
                else {
                    // Call the Next 500/ or whatever size pulled Records
                    skipIndex += forLoopSize
                }
            } while(!targetBusStopFound)

            // After finding the Correct Bus Stop
            if (targetBusStop.busStopCode != "Bus Stop Not Found") {
                if (multipleBusBool) {
                    _multipleBusStopNameUiState.value = BusStopValue(
                        busStopCode = targetBusStop.busStopCode,
                        busStopRoadName = targetBusStop.busStopRoadName,
                        busStopDescription = targetBusStop.busStopDescription,
                        latitude = targetBusStop.latitude,
                        longitude = targetBusStop.longitude
                    )
                }
                else {
                    _busStopNameUiState.value = BusStopValue(
                        busStopCode = targetBusStop.busStopCode,
                        busStopRoadName = targetBusStop.busStopRoadName,
                        busStopDescription = targetBusStop.busStopDescription,
                        latitude = targetBusStop.latitude,
                        longitude = targetBusStop.longitude
                    )
                }
            }

            BusStopNameUiState.Success(targetBusStop.busStopRoadName)
        }
        catch (e: IOException) {
            BusStopNameUiState.Error
        }
        catch (e: HttpException) {
            BusStopNameUiState.Error
        }
    }

    private suspend fun getMultipleBusTimings(busRoutes: List<BusStopInRoute>) {
        val busArrivalsJSONList: MutableList<SingaporeBus> = mutableListOf(SingaporeBus())
        val busStopDetailsJSONList: MutableList<BusStopValue> = mutableListOf(BusStopValue())
        val numberBusStopsInRoute = busRoutes.size - 1

        busUiState = BusUiState.Loading
        busUiState = try {
            // Get Bus Route
            var currentListResult = SingaporeBus()
            for (index in 0..numberBusStopsInRoute) {
                val currentBusStopCode = busRoutes[index].busStopCode
                currentListResult = singaporeBusRepository.getBusTimings(
                    busStopCode = currentBusStopCode,
                    busServiceNumber = null
                )

//                // Update the UI State
//                _busServiceUiState.value = SingaporeBus(
//                    metaData = currentListResult.metaData,
//                    busStopCode = currentListResult.busStopCode,
//                    services = currentListResult.services
//                )

                // Use UIState to append to List
                busArrivalsJSONList.add(SingaporeBus(
                    metaData = currentListResult.metaData,
                    busStopCode = currentListResult.busStopCode,
                    services = currentListResult.services
                ))

                // Update UI State for Bus Details
                getBusStopNames(currentBusStopCode.toInt(), true)

                // Use Updated UI State to append to List
                busStopDetailsJSONList.add(BusStopValue(
                    busStopCode = _multipleBusStopNameUiState.value.busStopCode,
                    busStopRoadName = _multipleBusStopNameUiState.value.busStopRoadName,
                    busStopDescription = _multipleBusStopNameUiState.value.busStopDescription,
                    longitude = _multipleBusStopNameUiState.value.longitude,
                    latitude = _multipleBusStopNameUiState.value.latitude,
                ))
            }

            // Update the UI Variable for the List
            busArrivalsJSONList.removeAt(0)
            busStopDetailsJSONList.removeAt(0)
            _multipleBusUiState.value = BusServicesRoute(
                busArrivalsJSONList = busArrivalsJSONList,
                busStopDetailsJSONList = busStopDetailsJSONList,
                busRoutesList = busRoutes,
            )
            _busStopNameUiState.value = BusStopValue(
                busStopCode = _multipleBusStopNameUiState.value.busStopCode,
                busStopRoadName = _multipleBusStopNameUiState.value.busStopRoadName,
                busStopDescription = _multipleBusStopNameUiState.value.busStopDescription,
                longitude = _multipleBusStopNameUiState.value.longitude,
                latitude = _multipleBusStopNameUiState.value.latitude,
            )

            BusUiState.Success(busTimings = currentListResult)
        }
        catch (e: IOException) {
            BusUiState.Error
        }
        catch (e: HttpException) {
            BusUiState.Error
        }
    }

    private suspend fun getBusTimings(userInput: String?) {
        // Determine if UserInput is a BusStopCode
        val userInputResult = determineBusServiceorStop(userInput = userInput)
        val busStopCode = userInputResult.busStopCode
        val busServiceNumber = userInputResult.busServiceNo

        //var listResult: SingaporeBus = SingaporeBus(metaData = "Initialised", busStopCode = "Initialised")
        busUiState = BusUiState.Loading
        // Might have Connectivity Issues
        busUiState = try {
            // Within this Scope, use the Repository, not the Object to access the Data, abstracting the data within the Data Layer
            val listResult = singaporeBusRepository.getBusTimings(
                busServiceNumber = busServiceNumber,
                busStopCode = busStopCode
            )

            _busServiceUiState.value = SingaporeBus(
                metaData = listResult.metaData,
                busStopCode = listResult.busStopCode,
                services = listResult.services
            )
            // Assign results from backend server to busUiState {A mutable state object that represents the status of the most recent web request}
            BusUiState.Success(busTimings = listResult)
        }
        catch (e: IOException) {
            BusUiState.Error
        }
        catch (e: HttpException) {
            BusUiState.Error
        }
    }

    // Factory Object to retrieve the singaporeBusRepository and pass it to the ViewModel
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as BusExpressApplication)
                val singaporeBusRepository = application.container.singaporeBusRepository
                AppViewModel(singaporeBusRepository = singaporeBusRepository)
            }
        }
    }

}

// Simply saving the UiState as a Mutable State prevents us from saving the different status
// like Loading, Error, and Success
sealed interface BusUiState {
    data class Success(val busTimings: SingaporeBus) : BusUiState
    // The 2 States below need not set new data and create new objects, which is why an object is sufficient for the web response
    object Error: BusUiState
    object Loading: BusUiState
    // Sealed Interface used instead of Interface to remove Else Branch
}

sealed interface BusStopNameUiState {
    data class Success(val busStopName: String): BusStopNameUiState
    object Error: BusStopNameUiState
    object Loading: BusStopNameUiState
}

sealed interface BusRouteUiState {
    data class Success(val busRoutes: MutableList<BusStopInRoute>): BusRouteUiState
    object Error: BusRouteUiState
    object Loading: BusRouteUiState
}

