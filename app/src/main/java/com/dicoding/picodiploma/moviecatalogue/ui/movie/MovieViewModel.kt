package com.dicoding.picodiploma.moviecatalogue.ui.movie

import androidx.lifecycle.ViewModel
import androidx.sqlite.db.SimpleSQLiteQuery
import com.dicoding.picodiploma.moviecatalogue.data.source.MovieRepository

class MovieViewModel(private val movieRepository: MovieRepository) : ViewModel() {

    fun getMovies(sql: SimpleSQLiteQuery) = movieRepository.getMovies(sql)
}