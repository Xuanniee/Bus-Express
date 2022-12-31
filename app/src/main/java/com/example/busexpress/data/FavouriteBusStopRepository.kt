package com.example.busexpress.data

import kotlinx.coroutines.flow.Flow


/**
 * Repository that provides insert, update, delete, and retrieve of [FavouriteBusStop] from a given data source.
 */
interface FavouriteBusStopRepository {
    /**
     * Retrieves all the bus stops from a given data source
     */
    fun getAllFavouriteBusStopStream(): Flow<List<FavouriteBusStop>>

    /**
     * Retrieves a single bus stop from the given data source that matches with the [id].
     */
    fun getFavouriteBusStopStream(id: Int): Flow<FavouriteBusStop?>

    /**
     * Retrieves all bus stops from given data source, where goingOut = true
     */
    fun retrieveGoingOutFavouriteBusStops(): Flow<List<FavouriteBusStop?>>

    /**
     * Retrieves all bus stops from the given data source, where goingOut = false
     */
    fun retrieveComingBackFavouriteBusStops(): Flow<List<FavouriteBusStop?>>

    /**
     * Insert bus stop into the data source. Remember database ops takes time, which is why we suspend
     */
    suspend fun insertBusStop(favouriteBusStop: FavouriteBusStop)

    /**
     * Delete bus stop from data source.
     */
    suspend fun deleteBusStop(favouriteBusStop: FavouriteBusStop)

    /**
     * Update bus stop in the data source
     */
    suspend fun updateBusStop(favouriteBusStop: FavouriteBusStop)

}




