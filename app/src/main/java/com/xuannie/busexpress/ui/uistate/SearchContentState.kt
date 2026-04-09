//package com.xuannie.busexpress.ui.uistate
//
//import com.xuannie.busexpress.network.BusRoutes
//import com.xuannie.busexpress.network.BusServicesRoute
//import com.xuannie.busexpress.network.BusStopValue
//import com.xuannie.busexpress.network.SingaporeBus
//
///**
// * This UIState stores the possible states of the Landing Page, Nearby + Search
// */
//sealed interface SearchContentState {
//    data object Loading : SearchContentState
//
//    data class SearchResults(
//        val busArrivalsJson: SingaporeBus = SingaporeBus(),
//        val busRoutes: BusRoutes = BusRoutes(),
//        val busServiceBool: Boolean = false,
//        val busStopDetails: BusStopValue = BusStopValue(),
//        val busServicesRouteList: BusServicesRoute = BusServicesRoute()
//    ) : SearchContentState
//
//    data class NearbyStops(
//        val nearbyStops: List<BusStopValue> = emptyList(),
//        val nearbyArrivals: List<SingaporeBus> = emptyList()
//    ) : SearchContentState
//
//    data class Error(val message: String? = null) : SearchContentState
//}