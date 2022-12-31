package com.example.busexpress.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.busexpress.data.FavouriteBusStop
import com.example.busexpress.data.dao.FavouriteBusStopDao


/**
 * @Database for Room to build the Database for us
 * Version Number is Updated whenever we update the schema
 * exportSchema to backup Schema Version History Backups
 */
@Database(entities = [FavouriteBusStop::class], version = 1, exportSchema = false)
abstract class FavouritesBusDatabase: RoomDatabase() {
    /**
     * Abstract function that returns the DAO for Database to be aware of it
     */
    abstract fun favouriteBusDao(): FavouriteBusStopDao

    // Companion Object that provides access to methods to GET / CREATE the database, with class name as qualifier
    companion object {
        // Instance Variable that acts as a reference to the database, when created {Maintains a SINGLE Database}
        @Volatile
        private var Instance: FavouritesBusDatabase? = null

        fun getDatabase(context: Context): FavouritesBusDatabase {
            /**
             * Wrapped in Synchronised Block to prevent multiple threads from accessing at once, resulting in a race condition
             */
            return Instance ?: synchronized(this) {
                // Build to get a Database
                Room.databaseBuilder(context, FavouritesBusDatabase::class.java, "favourite_bus_stop_database")
                    // Destroys the Database to rebuild if there are any migrations
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }

        }
    }

}



