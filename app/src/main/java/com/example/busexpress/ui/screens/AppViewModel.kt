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
import com.example.busexpress.network.SingaporeBus
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException


/**
 * [AppViewModel] holds information about a cupcake order in terms of quantity, flavor, and
 * pickup date. It also knows how to calculate the total price based on these order details.
 */
class AppViewModel(private val singaporeBusRepository: SingaporeBusRepository): ViewModel() {
    /** The mutable State that stores the status of the most recent request */
    var busUiState: BusUiState by mutableStateOf(BusUiState.Loading)     // Loading as Default Value
        // Setter is private to protect writes to the busUiState
        private set

    /**
     * Call init so we can display status immediately.
     */
    init {
        getBusTimings()
    }

    private fun getBusTimings() {
        // Launch the Coroutine using a ViewModelScope
        viewModelScope.launch {
            busUiState = BusUiState.Loading
            // Might have Connectivity Issues
            busUiState = try {
                // Within this Scope, use the Repository, not the Object to access the Data, abstracting the data within the Data Layer
                val listResult = singaporeBusRepository.getBusTimings()
                // Assign results from backend server to marsUiState {A mutable state object that represents the status of the most recent web request}
                BusUiState.Success(listResult)
            }
            catch (e: IOException) {
                BusUiState.Error
            }
//            catch (e: HttpException) {
//                BusUiState.Error
//            }
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

//    private val _uiState = MutableStateFlow(AppUiState(65199))
//    val uiState: StateFlow<AppUiState> = _uiState.asStateFlow()

}

// Simply saving the UiState as a Mutable State prevents us from saving the different status
// like Loading, Error, and Success
sealed interface BusUiState {
    data class Success(val timings: List<SingaporeBus>) : BusUiState
    // The 2 States below need not set new data and create new objects, which is why an object is sufficient for the web response
    object Error: BusUiState
    object Loading: BusUiState
    // Sealed Interface used instead of Interface to remove Else Branch
}



