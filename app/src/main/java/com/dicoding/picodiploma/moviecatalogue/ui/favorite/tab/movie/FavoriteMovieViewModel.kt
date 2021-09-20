package com.dicoding.picodiploma.moviecatalogue.ui.favorite.tab.movie

import androidx.lifecycle.ViewModel
import androidx.sqlite.db.SimpleSQLiteQuery
import com.dicoding.picodiploma.moviecatalogue.data.source.MovieRepository

class FavoriteMovieViewModel(private val movieRepository: MovieRepository) : ViewModel() {

    fun getBookmarkedMovies(sql: SimpleSQLiteQuery) = movieRepository.getBookmarkedMovies(sql)
    fun setBookmarkMovie(movieId: Int, state: Int) {
        movieRepository.setBookmarkMovie(movieId, state)
    }

    fun setBookmarkMovieDetail(detailMovieId: Int, state: Int) {
        movieRepository.setBookmarkMovieDetail(detailMovieId, state)
    }
}