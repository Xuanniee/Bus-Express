package com.xuannie.busexpress.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.xuannie.busexpress.data.FavouriteBusStop
import kotlinx.coroutines.flow.Flow


@Dao
interface FavouriteBusStopDao {
    /**
     * Allows User to add any BusStop into their Favourites List.
     * Suspend to allow it to run on a separate thread as database operations take a long time to perform
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertBusStop(favouriteBusStop: FavouriteBusStop)

    /**
     * Allows User to Edit their favourite Bus Stops such as renaming the Bus Stop
     */
    @Update
    suspend fun updateBusStop(favouriteBusStop: FavouriteBusStop)

    /**
     * Allows Users to remove any BusStop from their Favourites List
     */
    @Delete
    suspend fun deleteBusStop(favouriteBusStop: FavouriteBusStop)

    /**
     * Allows User to retrieve all the BusStops available in their favourites
     * Returning Flow allows us to be notified whenever data in the database is altered and causing a background thread to be used
     * NOTE: Use Coroutine Flow and Not other Flow
     */
    @Query(value = "SELECT * FROM `User's Favourites List` ORDER BY id ASC")
    fun retrieveAllFavouriteBusStops(): Flow<List<FavouriteBusStop>>

    @Query(value = "SELECT * FROM `User's Favourites List` WHERE id = :id") // Variable Substitution
    fun retrieveFavouriteBusStops(id: Int): Flow<FavouriteBusStop?>

    @Query(value = "SELECT * FROM `User's Favourites List` WHERE goingOutBusStop = 0")  // 0 is True here, i.e. Going Out
    fun retrieveGoingOutFavouriteBusStops(): Flow<List<FavouriteBusStop?>>

    @Query(value = "SELECT * FROM `User's Favourites List` WHERE goingOutBusStop = 1")
    fun retrieveComingBackFavouriteBusStops(): Flow<List<FavouriteBusStop?>>
}


