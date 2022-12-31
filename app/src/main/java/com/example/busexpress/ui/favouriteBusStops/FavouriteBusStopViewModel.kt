package com.example.busexpress.ui.favouriteBusStops

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.busexpress.data.FavouriteBusStop
import com.example.busexpress.data.FavouriteBusStopList
import com.example.busexpress.data.FavouriteBusStopRepository
import com.example.busexpress.data.SingaporeBusRepository
import com.example.busexpress.network.SingaporeBus
import com.example.busexpress.ui.screens.AppViewModel
import kotlinx.coroutines.flow.*

/**
 * View Model to validate and insert items in the Room database.
 */
class FavouriteBusStopViewModel(private val favouriteBusStopRepository: FavouriteBusStopRepository): ViewModel() {
    /**
     *  StateFlows to store the Data of Database
     */
    var goingOutUiState: StateFlow<FavouriteBusStopList> =
        favouriteBusStopRepository.retrieveGoingOutFavouriteBusStops().map { FavouriteBusStopList(it) }
            // Convert Flow into a StateFlow, allowing UI to update itself
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = FavouriteBusStopList()
            )

    /**
     * Holds the current Ui State
     */
    var favouriteBusStopUiState by mutableStateOf(FavouriteBusStopUiState())
        private set

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
            goingOutUiState = favouriteBusStopRepository.retrieveGoingOutFavouriteBusStops().map { FavouriteBusStopList(it) }
                // Convert Flow into a StateFlow, allowing UI to update itself
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                    initialValue = FavouriteBusStopList()
                )
        }
        else {
            goingOutUiState = favouriteBusStopRepository.retrieveComingBackFavouriteBusStops().map { FavouriteBusStopList(it) }
                // Convert Flow into a StateFlow, allowing UI to update itself
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                    initialValue = FavouriteBusStopList()
                )
        }

    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}











