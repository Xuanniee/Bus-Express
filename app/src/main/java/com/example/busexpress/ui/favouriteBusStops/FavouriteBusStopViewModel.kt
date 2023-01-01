package com.example.busexpress.ui.favouriteBusStops

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.busexpress.BusExpressApplication
import com.example.busexpress.data.*
import com.example.busexpress.network.BusStopValue
import com.example.busexpress.network.SingaporeBus
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

/**
 * Basically, what I need to do is code such that the viewModels do not rely on each other functions
 */
/**
 * View Model to validate and insert items in the Room database.
 */
class FavouriteBusStopViewModel(private val favouriteBusStopRepository: FavouriteBusStopRepository): ViewModel() {
    /**
     *  StateFlows to store the Data of Database
     */
    var allFavouritesUiState: StateFlow<FavouriteBusStopList> =
        favouriteBusStopRepository.retrieveAllFavouriteBusStops().map { FavouriteBusStopList(it) }
            // Convert Flow into a StateFlow, allowing UI to update itself
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = FavouriteBusStopList()
            )
    /**
     *  StateFlows to store the processed data from Database
     */
    private val _busStopsInFavUiState = MutableStateFlow(BusStopsInFavourites())
    val busStopsInFavUiState: StateFlow<BusStopsInFavourites> = _busStopsInFavUiState.asStateFlow()

    /**
     *  StateFlows to store the Data of API Calls
     */
    private val _favTimingUiState = MutableStateFlow(SingaporeBus())
//    val favTimingUiState: StateFlow<SingaporeBus> = _favTimingUiState.asStateFlow()

    private val _favDetailsUiState = MutableStateFlow(BusStopValue())
//    val favDetailUiState: StateFlow<BusStopValue> = _favDetailsUiState.asStateFlow()

    private var favouritesUiState: FavouriteDetailsUiState by mutableStateOf(FavouriteDetailsUiState.Loading)


    /**
     * Holds the current Ui State
     */
    private var favouriteBusStopUiState by mutableStateOf(FavouriteBusStopUiState())

    /**
     * Function to return Favourite buses to the Composable
     */
    fun determineOutAndBack(
        goingOutFavouriteUiState: FavouriteBusStopList,
    ) {
        viewModelScope.launch {
            // Retrieve Bus Stops ONCE for Favourites [Both Going Out]
            val busStopList = goingOutFavouriteUiState.busStopList
            val busStopListLength = busStopList.size - 1
            Log.d("DebugTag", "Size of Bus Stop List ${busStopList.size}")

            // 2 Lists each for Going Out and Coming Back
            val singaporeBusGoingOutList: MutableList<SingaporeBus> = mutableListOf()
            val singaporeBusComingBackList: MutableList<SingaporeBus> = mutableListOf()
            val busStopValueGoingOutList: MutableList<BusStopValue> = mutableListOf()
            val busStopValueComingBackList: MutableList<BusStopValue> = mutableListOf()

            // Determine Bus Details
            for (index in 0..busStopListLength) {
                // Retrieve Current Bus Stop
                val currentBusStop = busStopList[index]?.favouriteBusStopCode

                // Use Bus Stop Code to get Bus Details and Stuff
                getFavouriteTimeAndDetails(targetBusStopCode = currentBusStop?.toInt())

                // Save Timings & Details in an array from API Call
                if (busStopList[index]?.goingOutBusStop == 0) {
                    // Going Out
                    singaporeBusGoingOutList.add(_favTimingUiState.value)
                    busStopValueGoingOutList.add(_favDetailsUiState.value)
                }
                else {
                    // Coming Back
                    singaporeBusComingBackList.add(_favTimingUiState.value)
                    Log.d("DebugTag", _favTimingUiState.value.toString())
                    busStopValueComingBackList.add(_favDetailsUiState.value)
                    Log.d("DebugTag", _favDetailsUiState.value.toString())
                }
            }

            // Update after the For Loop
            _busStopsInFavUiState.value = BusStopsInFavourites(
                singaporeBusComingBackList = singaporeBusComingBackList,
                singaporeBusGoingOutList = singaporeBusGoingOutList,
                busStopValueComingBackList = busStopValueComingBackList,
                busStopValueGoingOutList = busStopValueGoingOutList
            )
        }


    }

    /**
     * Function that gets the Bus Timings and Details of Favourite Bus Stops
     */
    private suspend fun getFavouriteTimeAndDetails(targetBusStopCode: Int?) {
        favouritesUiState = FavouriteDetailsUiState.Loading
        // Get Bus Stop Names
        favouritesUiState = try {
            var targetBusStop = BusStopValue()
            var targetBusStopFound = false
            var skipIndex = 0

            Log.d("DebugTag", targetBusStopCode.toString())

            // Timings Result
            val listResultTimings = favouriteBusStopRepository.getBusFavTimings(
                busStopCode = targetBusStopCode.toString(),
                busServiceNumber = null
            )
            Log.d("DebugTag2", listResultTimings.toString())

            _favTimingUiState.value = SingaporeBus(
                metaData = listResultTimings.metaData,
                busStopCode = listResultTimings.busStopCode,
                services = listResultTimings.services
            )
            Log.d("DebugTag", "INSIDE FUNCTION I NEED: ${_favTimingUiState.value}")

            // Retrieve the Desired Bus Stop Object
            do {
                val listResultDetails = favouriteBusStopRepository.getBusFavDetails(numRecordsToSkip = skipIndex)

                val busStopDetails = listResultDetails.value
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
                _favDetailsUiState.value = BusStopValue(
                    busStopCode = targetBusStop.busStopCode,
                    busStopRoadName = targetBusStop.busStopRoadName,
                    busStopDescription = targetBusStop.busStopDescription,
                    latitude = targetBusStop.latitude,
                    longitude = targetBusStop.longitude
                )
            }
            FavouriteDetailsUiState.Success(targetBusStop.busStopRoadName)
        }
        catch (e: IOException) {
            FavouriteDetailsUiState.Error
        }
        catch (e: HttpException) {
            FavouriteDetailsUiState.Error
        }
    }

    /**
     * Updates the [FavouriteBusStopUiState] with value provided in the argument, while providing validation for the input values
     */
    fun updateUiState(newFavouriteBusStopUiState: FavouriteBusStopUiState) {
        favouriteBusStopUiState = newFavouriteBusStopUiState.copy( actionEnabled = newFavouriteBusStopUiState.isValid() )
    }

    /**
     * Function to update the favouriteBusStopUiState
     */
    fun updateFavouriteUiState(favouriteBusStopCode: String, goingOut: Int) {
        favouriteBusStopUiState = FavouriteBusStopUiState(
            favouriteBusStopCode = favouriteBusStopCode,
            goingOutBusStop = goingOut,
        )
    }

    /**
     * Function to insert Bus Stop into the Room Database
     */
    suspend fun saveBusStop() {
        // Check if Valid
        if (favouriteBusStopUiState.isValid()) {
            // Pass the Object Bus Stop to be inserted
            Log.d("DebugTag3", favouriteBusStopUiState.toString())
            favouriteBusStopRepository.insertBusStop(favouriteBusStop = FavouriteBusStop(
                favouriteBusStopCode = favouriteBusStopUiState.favouriteBusStopCode,
                goingOutBusStop = favouriteBusStopUiState.goingOutBusStop
            ))
        }
    }

    /**
     *  Function to retrieve Favourites of User
     */
    fun retrieveFavouriteBusStops(goingOut: Boolean) {
        if (goingOut) {
            allFavouritesUiState = favouriteBusStopRepository.retrieveGoingOutFavouriteBusStops().map { FavouriteBusStopList(it) }
                // Convert Flow into a StateFlow, allowing UI to update itself
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                    initialValue = FavouriteBusStopList()
                )
        }
        else {
            allFavouritesUiState = favouriteBusStopRepository.retrieveComingBackFavouriteBusStops().map { FavouriteBusStopList(it) }
                // Convert Flow into a StateFlow, allowing UI to update itself
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                    initialValue = FavouriteBusStopList()
                )
        }

    }
    // Factory Object to retrieve the singaporeBusRepository and pass it to the ViewModel
    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
        /**
         * Initializer for FavBusStopViewModel
         */
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as BusExpressApplication)
                val favouriteBusStopRepository = application.container.favouriteBusStopRepository
                FavouriteBusStopViewModel(
                    favouriteBusStopRepository = favouriteBusStopRepository,
                )
            }
        }
    }
}

sealed interface FavouriteDetailsUiState {
    data class Success(val busStopName: String): FavouriteDetailsUiState
    object Error: FavouriteDetailsUiState
    object Loading: FavouriteDetailsUiState
}









