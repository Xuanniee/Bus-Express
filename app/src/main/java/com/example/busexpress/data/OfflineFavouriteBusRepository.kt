package com.example.busexpress.data

import com.example.busexpress.data.dao.FavouriteBusStopDao
import kotlinx.coroutines.flow.Flow

/**
 * Implements the Interface in FavouriteBusStopRepository.
 * Initially, they were in the same file but I choose to separate this time
 */
class OfflineFavouriteBusRepository(
    private val favouriteBusStopDao: FavouriteBusStopDao
    ): FavouriteBusStopRepository {
    override fun getAllFavouriteBusStopStream(): Flow<List<FavouriteBusStop>> = favouriteBusStopDao.retrieveAllFavouriteBusStops()

    override fun getFavouriteBusStopStream(id: Int): Flow<FavouriteBusStop?> = favouriteBusStopDao.retrieveFavouriteBusStops(id = id)

    override suspend fun insertBusStop(favouriteBusStop: FavouriteBusStop) = favouriteBusStopDao.insertBusStop(favouriteBusStop)

    override suspend fun deleteBusStop(favouriteBusStop: FavouriteBusStop) = favouriteBusStopDao.deleteBusStop(favouriteBusStop)

    override suspend fun updateBusStop(favouriteBusStop: FavouriteBusStop) = favouriteBusStopDao.updateBusStop(favouriteBusStop)

    override fun retrieveGoingOutFavouriteBusStops(): Flow<List<FavouriteBusStop?>> = favouriteBusStopDao.retrieveGoingOutFavouriteBusStops()

    override fun retrieveComingBackFavouriteBusStops(): Flow<List<FavouriteBusStop?>> = favouriteBusStopDao.retrieveComingBackFavouriteBusStops()

    override fun retrieveAllFavouriteBusStops(): Flow<List<FavouriteBusStop?>> = favouriteBusStopDao.retrieveAllFavouriteBusStops()
}