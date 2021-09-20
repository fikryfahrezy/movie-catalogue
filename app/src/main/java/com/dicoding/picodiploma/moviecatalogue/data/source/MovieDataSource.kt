package com.dicoding.picodiploma.moviecatalogue.data.source

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import androidx.sqlite.db.SimpleSQLiteQuery
import com.dicoding.picodiploma.moviecatalogue.data.source.local.entity.*
import com.dicoding.picodiploma.moviecatalogue.data.source.remote.response.*
import com.dicoding.picodiploma.moviecatalogue.vo.Resource

interface MovieDataSource {
    fun getShows(sql: SimpleSQLiteQuery): LiveData<Resource<PagedList<ShowEntity>?>>
    fun getMovies(sql: SimpleSQLiteQuery): LiveData<Resource<PagedList<MovieEntity>?>>
    fun getMovie(movieId: Int): LiveData<Resource<MovieDetailWithCompany>>
    fun getShow(showId: Int): LiveData<Resource<ShowDetailWithCompany>>
    fun getBookmarkedMovies(sql: SimpleSQLiteQuery): LiveData<PagedList<MovieEntity?>>
    fun getBookmarkedShows(sql: SimpleSQLiteQuery): LiveData<PagedList<ShowEntity?>>
    fun setBookmarkMovie(movieId: Int, state: Int)
    fun setBookmarkShow(showId: Int, state: Int)
    fun updateMovieDetail(movieDetailEntity: MovieDetailEntity)
    fun updateShowDetail(showDetailEntity: ShowDetailEntity)
    fun setBookmarkMovieDetail(detailMovieId: Int, state: Int)
    fun setBookmarkShowDetail(detailShowId: Int, state: Int)
}