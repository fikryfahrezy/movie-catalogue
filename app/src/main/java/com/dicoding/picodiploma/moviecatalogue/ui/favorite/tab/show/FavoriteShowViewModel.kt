package com.dicoding.picodiploma.moviecatalogue.ui.favorite.tab.show

import androidx.lifecycle.ViewModel
import androidx.sqlite.db.SimpleSQLiteQuery
import com.dicoding.picodiploma.moviecatalogue.data.source.MovieRepository

class FavoriteShowViewModel(private val movieRepository: MovieRepository) : ViewModel() {

    fun getBookmarkedShows(sql: SimpleSQLiteQuery) = movieRepository.getBookmarkedShows(sql)
    fun setBookmarkShow(showId: Int, state: Int) {
        movieRepository.setBookmarkShow(showId, state)
    }

    fun setBookmarkShowDetail(detailShowId: Int, state: Int) {
        movieRepository.setBookmarkShowDetail(detailShowId, state)
    }
}