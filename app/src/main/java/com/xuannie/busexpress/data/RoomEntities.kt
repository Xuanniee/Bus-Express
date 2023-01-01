package com.xuannie.busexpress.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "User's Favourites List")
data class FavouriteBusStop(
    // To autogenerate Primary Keys for every Record
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    // To retrieve Bus Timings for the BusStops
    val favouriteBusStopCode: String = "",

    val goingOutBusStop: Int = 0,
)

data class FavouriteBusStopList(
    val busStopList: List<FavouriteBusStop?> = listOf()
)
