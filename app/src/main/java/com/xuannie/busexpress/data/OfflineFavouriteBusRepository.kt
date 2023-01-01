import com.xuannie.busexpress.data.FavouriteBusStop
import com.xuannie.busexpress.data.FavouriteBusStopRepository
import com.xuannie.busexpress.data.dao.FavouriteBusStopDao
import com.xuannie.busexpress.network.BusApiService
import com.xuannie.busexpress.network.BusStop
import com.xuannie.busexpress.network.SingaporeBus
import kotlinx.coroutines.flow.Flow

/**
 * Implements the Interface in FavouriteBusStopRepository.
 * Initially, they were in the same file but I choose to separate this time
 */
class DefaultFavouriteBusRepository(
    private val favouriteBusStopDao: FavouriteBusStopDao,
    private val busApiService: BusApiService
): FavouriteBusStopRepository {
    override suspend fun getBusFavTimings(busStopCode: String?, busServiceNumber: String?): SingaporeBus {
        return busApiService.getTimingsOfBusStop(
            BusStopCode = busStopCode,
            ServiceNo = null
        )
    }

    override suspend fun getBusFavDetails(numRecordsToSkip: Int?): BusStop {
        return busApiService.getDetailsOfBusStop(
            NumRecordsToSkip = numRecordsToSkip
        )
    }

    override fun getAllFavouriteBusStopStream(): Flow<List<FavouriteBusStop>> = favouriteBusStopDao.retrieveAllFavouriteBusStops()

    override fun getFavouriteBusStopStream(id: Int): Flow<FavouriteBusStop?> = favouriteBusStopDao.retrieveFavouriteBusStops(id = id)

    override suspend fun insertBusStop(favouriteBusStop: FavouriteBusStop) = favouriteBusStopDao.insertBusStop(favouriteBusStop)

    override suspend fun deleteBusStop(favouriteBusStop: FavouriteBusStop) = favouriteBusStopDao.deleteBusStop(favouriteBusStop)

    override suspend fun updateBusStop(favouriteBusStop: FavouriteBusStop) = favouriteBusStopDao.updateBusStop(favouriteBusStop)

    override fun retrieveGoingOutFavouriteBusStops(): Flow<List<FavouriteBusStop?>> = favouriteBusStopDao.retrieveGoingOutFavouriteBusStops()

    override fun retrieveComingBackFavouriteBusStops(): Flow<List<FavouriteBusStop?>> = favouriteBusStopDao.retrieveComingBackFavouriteBusStops()

    override fun retrieveAllFavouriteBusStops(): Flow<List<FavouriteBusStop?>> = favouriteBusStopDao.retrieveAllFavouriteBusStops()
}