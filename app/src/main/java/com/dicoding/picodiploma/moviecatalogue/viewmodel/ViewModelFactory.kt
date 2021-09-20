package com.dicoding.picodiploma.moviecatalogue.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.picodiploma.moviecatalogue.data.source.MovieRepository
import com.dicoding.picodiploma.moviecatalogue.di.ApplicationModule
import com.dicoding.picodiploma.moviecatalogue.ui.detail.DetailViewModel
import com.dicoding.picodiploma.moviecatalogue.ui.favorite.tab.movie.FavoriteMovieViewModel
import com.dicoding.picodiploma.moviecatalogue.ui.favorite.tab.show.FavoriteShowViewModel
import com.dicoding.picodiploma.moviecatalogue.ui.movie.MovieViewModel
import com.dicoding.picodiploma.moviecatalogue.ui.show.ShowViewModel
import javax.inject.Inject

class ViewModelFactory @Inject constructor(private val movieRepository: MovieRepository) :
    ViewModelProvider.NewInstanceFactory() {

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null
        fun getInstance(context: Context): ViewModelFactory =
            instance ?: synchronized(this) {
                instance
                    ?: ViewModelFactory(ApplicationModule.provideRemoteRepository(context))
            }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MovieViewModel::class.java) -> {
                MovieViewModel(movieRepository) as T
            }
            modelClass.isAssignableFrom(ShowViewModel::class.java) -> {
                ShowViewModel(movieRepository) as T
            }
            modelClass.isAssignableFrom(DetailViewModel::class.java) -> {
                DetailViewModel(movieRepository) as T
            }
            modelClass.isAssignableFrom(FavoriteShowViewModel::class.java) -> {
                FavoriteShowViewModel(movieRepository) as T
            }
            modelClass.isAssignableFrom(FavoriteMovieViewModel::class.java) -> {
                FavoriteMovieViewModel(movieRepository) as T
            }
            else -> throw Throwable("Unknown ViewModel class: " + modelClass.name)
        }
    }
}