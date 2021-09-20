package com.dicoding.picodiploma.moviecatalogue.ui.detail

import androidx.lifecycle.ViewModel
import com.dicoding.picodiploma.moviecatalogue.data.source.MovieRepository
import com.dicoding.picodiploma.moviecatalogue.data.source.local.entity.MovieDetailEntity
import com.dicoding.picodiploma.moviecatalogue.data.source.local.entity.ShowDetailEntity

class DetailViewModel(private val movieRepository: MovieRepository) : ViewModel() {

    val isLoading = movieRepository.isLoading
    val isError = movieRepository.isError
    fun getMovie(movieId: Int) = movieRepository.getMovie(movieId)
    fun getShow(showId: Int) = movieRepository.getShow(showId)
    fun setBookmarkMovie(movieId: Int, state: Int) {
        movieRepository.setBookmarkMovie(movieId, state)
    }

    fun setBookmarkShow(showId: Int, state: Int) {
        movieRepository.setBookmarkShow(showId, state)
    }

    fun setBookmarkMovieDetail(movieDetailEntity: MovieDetailEntity) {
        movieRepository.updateMovieDetail(movieDetailEntity)
    }

    fun setBookmarkShowDetail(showDetailEntity: ShowDetailEntity) {
        movieRepository.updateShowDetail(showDetailEntity)
    }
}