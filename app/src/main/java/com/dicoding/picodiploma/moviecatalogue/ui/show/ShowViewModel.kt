package com.dicoding.picodiploma.moviecatalogue.ui.show

import androidx.lifecycle.ViewModel
import androidx.sqlite.db.SimpleSQLiteQuery
import com.dicoding.picodiploma.moviecatalogue.data.source.MovieRepository

class ShowViewModel(private val movieRepository: MovieRepository) : ViewModel() {

    fun getShows(sql: SimpleSQLiteQuery) = movieRepository.getShows(sql)
}