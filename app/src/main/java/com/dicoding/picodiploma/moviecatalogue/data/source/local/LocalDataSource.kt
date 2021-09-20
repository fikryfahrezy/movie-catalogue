package com.dicoding.picodiploma.moviecatalogue.data.source.local

import androidx.paging.DataSource
import androidx.sqlite.db.SimpleSQLiteQuery
import com.dicoding.picodiploma.moviecatalogue.data.source.local.entity.*
import com.dicoding.picodiploma.moviecatalogue.data.source.local.room.MovieDao
import javax.inject.Inject

class LocalDataSource @Inject constructor(private val movieDao: MovieDao) {

    fun getMovies(sql: SimpleSQLiteQuery): DataSource.Factory<Int, MovieEntity> =
        movieDao.getMovies(sql)

    fun getShows(sql: SimpleSQLiteQuery): DataSource.Factory<Int, ShowEntity> =
        movieDao.getShows(sql)

    fun insertMovies(movies: List<MovieEntity>) {
        movieDao.insertMovies(movies)
    }

    fun insertShows(shows: List<ShowEntity>) {
        movieDao.insertShows(shows)
    }

    fun getMovie(movieId: Int) = movieDao.getMovie(movieId)

    fun getShow(showId: Int) = movieDao.getShow(showId)

    fun insertMovie(movie: MovieDetailEntity, companies: List<MovieCompanyEntity>) {
        movieDao.insertMovie(movie)
        movieDao.insertMovieCompanies(companies)
    }

    fun insertShow(show: ShowDetailEntity, companies: List<ShowCompanyEntity>) {
        movieDao.insertShow(show)
        movieDao.insertShowCompanies(companies)
    }

    fun setBookmarkMovie(movieId: Int, state: Int) {
        movieDao.updateMovie(movieId, state)
    }

    fun setBookmarkShow(showId: Int, state: Int) {
        movieDao.updateShow(showId, state)
    }

    fun updateMovieDetail(movieDetailEntity: MovieDetailEntity) {
        movieDao.updateMovieDetail(movieDetailEntity)
    }

    fun updateShowDetail(showDetailEntity: ShowDetailEntity) {
        movieDao.updateShowDetail(showDetailEntity)
    }

    fun setBookmarkMovieDetail(detailMovieId: Int, state: Int) {
        movieDao.setBookmarkMovieDetail(detailMovieId, state)
    }

    fun setBookmarkShowDetail(detailShowId: Int, state: Int) {
        movieDao.setBookmarkShowDetail(detailShowId, state)
    }
}