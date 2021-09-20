package com.dicoding.picodiploma.moviecatalogue.di

import android.content.Context
import com.dicoding.picodiploma.moviecatalogue.config.FetchConfig
import com.dicoding.picodiploma.moviecatalogue.data.source.MovieRepository
import com.dicoding.picodiploma.moviecatalogue.data.source.local.LocalDataSource
import com.dicoding.picodiploma.moviecatalogue.data.source.local.room.MovieDatabase
import com.dicoding.picodiploma.moviecatalogue.data.source.remote.RemoteDataSource
import com.dicoding.picodiploma.moviecatalogue.utils.AppExecutors
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object ApplicationModule {

    @Provides
    @Singleton
    fun provideRemoteRepository(context: Context): MovieRepository {
        val fetch = FetchConfig()
        val remoteRepository = RemoteDataSource(fetch)
        val database = MovieDatabase.getInstance(context)
        val localDataSource = LocalDataSource(database.movieDao())
        val appExecutors = AppExecutors()
        return MovieRepository(remoteRepository, localDataSource, appExecutors)
    }
}