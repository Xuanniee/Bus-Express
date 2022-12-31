package com.example.busexpress.data

import com.example.busexpress.network.BusStopValue
import com.example.busexpress.network.SingaporeBus

data class BusStopsInFavourites(
    val singaporeBusGoingOutList: MutableList<SingaporeBus> = mutableListOf(),
    val singaporeBusComingBackList: MutableList<SingaporeBus> = mutableListOf(),
    val busStopValueGoingOutList: MutableList<BusStopValue> = mutableListOf(),
    val busStopValueComingBackList: MutableList<BusStopValue> = mutableListOf(),
)
