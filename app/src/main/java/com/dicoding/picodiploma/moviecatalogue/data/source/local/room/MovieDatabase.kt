package com.dicoding.picodiploma.moviecatalogue.data.source.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dicoding.picodiploma.moviecatalogue.data.source.local.entity.*

@Database(
    entities = [
        MovieEntity::class,
        ShowEntity::class,
        MovieDetailEntity::class,
        ShowDetailEntity::class,
        MovieCompanyEntity::class,
        ShowCompanyEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class MovieDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao

    companion object {
        private var instance: MovieDatabase? = null
        fun getInstance(context: Context): MovieDatabase =
            instance ?: synchronized(MovieDatabase::class.java) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    MovieDatabase::class.java,
                    "Movies.db"
                ).build()
            }
    }
}