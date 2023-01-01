package com.xuannie.busexpress.data

import com.xuannie.busexpress.network.BusStopValue
import com.xuannie.busexpress.network.SingaporeBus

data class BusStopsInFavourites(
    val singaporeBusGoingOutList: MutableList<SingaporeBus> = mutableListOf(),
    val singaporeBusComingBackList: MutableList<SingaporeBus> = mutableListOf(),
    val busStopValueGoingOutList: MutableList<BusStopValue> = mutableListOf(),
    val busStopValueComingBackList: MutableList<BusStopValue> = mutableListOf(),
)
