package com.dicoding.picodiploma.moviecatalogue.data.source.local.room

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import com.dicoding.picodiploma.moviecatalogue.data.source.local.entity.*

@Dao
interface MovieDao {

    @RawQuery(observedEntities = [MovieEntity::class])
    fun getMovies(query: SupportSQLiteQuery): DataSource.Factory<Int, MovieEntity>

    @RawQuery(observedEntities = [ShowEntity::class])
    fun getShows(query: SupportSQLiteQuery): DataSource.Factory<Int, ShowEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMovies(movies: List<MovieEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertShows(shows: List<ShowEntity>)

    @Transaction
    @Query("SELECT * FROM moviedetailentities WHERE id = :movieId")
    fun getMovie(movieId: Int): LiveData<MovieDetailWithCompany>

    @Transaction
    @Query("SELECT * FROM showdetailentities WHERE id = :showId")
    fun getShow(showId: Int): LiveData<ShowDetailWithCompany>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMovie(movies: MovieDetailEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertShow(show: ShowDetailEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMovieCompanies(companies: List<MovieCompanyEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertShowCompanies(companies: List<ShowCompanyEntity>)

    @Query("UPDATE moviesentities SET bookmarked = :state WHERE id = :movieId")
    fun updateMovie(movieId: Int, state: Int)

    @Query("UPDATE showsentities SET bookmarked = :state WHERE id = :showId")
    fun updateShow(showId: Int, state: Int)

    @Update
    fun updateMovieDetail(movieDetailEntity: MovieDetailEntity)

    @Update
    fun updateShowDetail(showDetailEntity: ShowDetailEntity)

    @Query("UPDATE moviedetailentities SET bookmarked = :state WHERE id = :detailMovieId")
    fun setBookmarkMovieDetail(detailMovieId: Int, state: Int)

    @Query("UPDATE showdetailentities SET bookmarked = :state WHERE id = :detailShowId")
    fun setBookmarkShowDetail(detailShowId: Int, state: Int)
}