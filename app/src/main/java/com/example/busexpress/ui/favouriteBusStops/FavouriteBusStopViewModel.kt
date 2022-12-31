package com.example.busexpress.ui.favouriteBusStops

import android.util.Log
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.busexpress.BusExpressApplication
import com.example.busexpress.data.*
import com.example.busexpress.network.BusStopValue
import com.example.busexpress.network.SingaporeBus
import com.example.busexpress.ui.screens.AppViewModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

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
     * Holds the current Ui State
     */
    var favouriteBusStopUiState by mutableStateOf(FavouriteBusStopUiState())
        private set

    /**
     * Function to return Favourite buses to the Composable
     */
    fun determineOutAndBack(
        goingOutFavouriteUiState: FavouriteBusStopList,
        appViewModel: AppViewModel,
        busStopNameUiState: BusStopValue,
        busServiceUiState: SingaporeBus,
    ) {
        // Retrieve Bus Stops ONCE for Favourites [Both Going Out]
        val busStopList = goingOutFavouriteUiState.busStopList
        val busStopListLength = busStopList.size - 1
        Log.d("DebugTag", "Size of Bus Stop List $busStopList")

        // 2 Lists each for Going Out and Coming Back
        val singaporeBusGoingOutList: MutableList<SingaporeBus> = mutableListOf()
        val singaporeBusComingBackList: MutableList<SingaporeBus> = mutableListOf()
        val busStopValueGoingOutList: MutableList<BusStopValue> = mutableListOf()
        val busStopValueComingBackList: MutableList<BusStopValue> = mutableListOf()

        // Determine Bus Details
        for (index in 0..busStopListLength) {
            // Retrieve Current Bus Stop
            val currentBusStop = busStopList[index]?.favouriteBusStopCode
            Log.d("DebugTag", "Current Busstop $currentBusStop")
            // Use Bus Stop Code to get Bus Details and Stuff
            appViewModel.determineUserQuery(currentBusStop.toString())

            // Save Timings & Details in an array from API Call
            if (busStopList[index]?.goingOutBusStop == 0) {
                // Going Out
                singaporeBusGoingOutList.add(busServiceUiState)
                Log.d("DebugTag", busServiceUiState.toString())
                busStopValueGoingOutList.add(busStopNameUiState)
                Log.d("DebugTag", busServiceUiState.toString())
            }
            else {
                // Coming Back
                singaporeBusComingBackList.add(busServiceUiState)
                busStopValueComingBackList.add(busStopNameUiState)
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
            favouriteBusStopRepository.insertBusStop(favouriteBusStop = favouriteBusStopUiState.toFavouriteBusStop())
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
                val application =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as BusExpressApplication)
                val favouriteBusStopRepository = application.container.favouriteBusStopRepository
                FavouriteBusStopViewModel(favouriteBusStopRepository = favouriteBusStopRepository)
            }
        }
    }
}











