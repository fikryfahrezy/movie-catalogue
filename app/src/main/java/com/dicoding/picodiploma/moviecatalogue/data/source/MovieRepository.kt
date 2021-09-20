package com.dicoding.picodiploma.moviecatalogue.data.source

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import androidx.sqlite.db.SimpleSQLiteQuery
import com.dicoding.picodiploma.moviecatalogue.data.source.local.LocalDataSource
import com.dicoding.picodiploma.moviecatalogue.data.source.local.entity.*
import com.dicoding.picodiploma.moviecatalogue.data.source.remote.ApiResponse
import com.dicoding.picodiploma.moviecatalogue.data.source.remote.RemoteDataSource
import com.dicoding.picodiploma.moviecatalogue.data.source.remote.response.*
import com.dicoding.picodiploma.moviecatalogue.utils.AppExecutors
import com.dicoding.picodiploma.moviecatalogue.vo.Resource
import javax.inject.Inject

class MovieRepository @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource,
    private val appExecutors: AppExecutors
) :
    MovieDataSource {

    private val _isLoading = MutableLiveData<Boolean>()
    private val _isError = MutableLiveData<Boolean>()

    val isLoading: LiveData<Boolean>
        get() = _isLoading
    val isError: LiveData<Boolean>
        get() = _isError

    override fun getShows(sql: SimpleSQLiteQuery): LiveData<Resource<PagedList<ShowEntity>?>> {
        return object :
            NetworkBoundResource<PagedList<ShowEntity>?, PopularTvShowsResponse?>(
                appExecutors
            ) {
            override fun loadFromDB(): LiveData<PagedList<ShowEntity>?> {
                val config = PagedList.Config.Builder()
                    .setEnablePlaceholders(false)
                    .setInitialLoadSizeHint(10)
                    .setPageSize(10)
                    .build()
                return LivePagedListBuilder(
                    localDataSource.getShows(sql),
                    config
                ).build()
            }

            override fun shouldFetch(data: PagedList<ShowEntity>?): Boolean =
                data.isNullOrEmpty()

            override fun createCall(): LiveData<ApiResponse<PopularTvShowsResponse?>> =
                remoteDataSource.getShows()

            override fun saveCallResult(data: PopularTvShowsResponse?) {
                val shows = ArrayList<ShowEntity>()

                if (data != null)
                    for (response in data.results) {
                        val show = ShowEntity(
                            response.id,
                            response.name,
                            response.overview,
                            response.posterPath,
                            response.firstAirDate,
                            false
                        )
                        shows.add(show)
                    }
                localDataSource.insertShows(shows)
            }

        }.asLiveData()
    }

    override fun getMovies(sql: SimpleSQLiteQuery): LiveData<Resource<PagedList<MovieEntity>?>> {
        return object :
            NetworkBoundResource<PagedList<MovieEntity>?, PopularMoviesResponse?>(appExecutors) {
            override fun loadFromDB(): LiveData<PagedList<MovieEntity>?> {
                val config = PagedList.Config.Builder()
                    .setEnablePlaceholders(false)
                    .setInitialLoadSizeHint(10)
                    .setPageSize(10)
                    .build()
                return LivePagedListBuilder(
                    localDataSource.getMovies(sql),
                    config
                ).build()
            }

            override fun shouldFetch(data: PagedList<MovieEntity>?): Boolean =
                data.isNullOrEmpty()

            override fun createCall(): LiveData<ApiResponse<PopularMoviesResponse?>> =
                remoteDataSource.getMovies()

            override fun saveCallResult(data: PopularMoviesResponse?) {
                val movies = ArrayList<MovieEntity>()

                if (data != null)
                    for (response in data.results) {
                        val movie = MovieEntity(
                            response.id,
                            response.title,
                            response.overview,
                            response.posterPath,
                            response.releaseDate,
                            false
                        )
                        movies.add(movie)
                    }

                localDataSource.insertMovies(movies)

            }
        }.asLiveData()
    }

    override fun getMovie(movieId: Int): LiveData<Resource<MovieDetailWithCompany>> {
        return object :
            NetworkBoundResource<MovieDetailWithCompany, MovieDetailResponse?>(appExecutors) {
            override fun loadFromDB(): LiveData<MovieDetailWithCompany> =
                localDataSource.getMovie(movieId)

            override fun shouldFetch(data: MovieDetailWithCompany?): Boolean =
                data?.movieDetailEntity == null || data.companies.isNullOrEmpty()

            override fun createCall(): LiveData<ApiResponse<MovieDetailResponse?>> =
                remoteDataSource.getMovie(movieId)

            override fun saveCallResult(data: MovieDetailResponse?) {
                if (data != null) {
                    val movieDetail = MovieDetailEntity(
                        data.id,
                        data.title,
                        data.overview,
                        data.posterPath,
                        data.releaseDate,
                        data.runtime,
                        data.genres.joinToString { it.name },
                        data.originalTitle,
                        data.tagline,
                        data.productionCountries.joinToString { it.name },
                        data.status,
                        data.spokenLanguages.joinToString { it.name },
                        false
                    )

                    val companies = ArrayList<MovieCompanyEntity>()
                    for (companyData in data.productionCompanies) {
                        val company = MovieCompanyEntity(
                            companyData.id,
                            data.id,
                            companyData.name,
                            companyData.logoPath,
                            companyData.originCountry,
                        )
                        companies.add(company)
                    }

                    localDataSource.insertMovie(movieDetail, companies)
                }
            }

        }.asLiveData()
    }

    override fun getShow(showId: Int): LiveData<Resource<ShowDetailWithCompany>> {
        return object :
            NetworkBoundResource<ShowDetailWithCompany, TvShowDetailResponse?>(appExecutors) {
            override fun loadFromDB(): LiveData<ShowDetailWithCompany> =
                localDataSource.getShow(showId)

            override fun shouldFetch(data: ShowDetailWithCompany?): Boolean =
                data?.showDetailEntity == null || data.companies.isNullOrEmpty()

            override fun createCall(): LiveData<ApiResponse<TvShowDetailResponse?>> =
                remoteDataSource.getShow(showId)

            override fun saveCallResult(data: TvShowDetailResponse?) {
                if (data != null) {
                    val showDetail = ShowDetailEntity(
                        data.id,
                        data.name,
                        data.overview,
                        data.posterPath,
                        data.firstAirDate,
                        data.lastAirDate,
                        data.episodeRunTime.size,
                        data.genres.joinToString { it.name },
                        data.originalName,
                        data.tagline,
                        data.productionCountries.joinToString { it.name },
                        data.status,
                        data.spokenLanguages.joinToString { it.name },
                        false
                    )

                    val companies = ArrayList<ShowCompanyEntity>()
                    for (companyData in data.productionCompanies) {
                        val company = ShowCompanyEntity(
                            companyData.id,
                            data.id,
                            companyData.name,
                            companyData.logoPath,
                            companyData.originCountry
                        )
                        companies.add(company)
                    }

                    localDataSource.insertShow(showDetail, companies)
                }
            }
        }.asLiveData()
    }

    override fun getBookmarkedMovies(sql: SimpleSQLiteQuery): LiveData<PagedList<MovieEntity?>> {
        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setInitialLoadSizeHint(10)
            .setPageSize(10)
            .build()
        return LivePagedListBuilder(
            localDataSource.getMovies(sql),
            config
        ).build()
    }

    override fun getBookmarkedShows(sql: SimpleSQLiteQuery): LiveData<PagedList<ShowEntity?>> {
        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setInitialLoadSizeHint(10)
            .setPageSize(10)
            .build()
        return LivePagedListBuilder(
            localDataSource.getShows(sql),
            config
        ).build()
    }

    override fun setBookmarkMovie(movieId: Int, state: Int) {
        appExecutors.diskIO().execute { localDataSource.setBookmarkMovie(movieId, state) }
    }

    override fun setBookmarkShow(showId: Int, state: Int) {
        appExecutors.diskIO().execute { localDataSource.setBookmarkShow(showId, state) }

    }

    override fun updateMovieDetail(movieDetailEntity: MovieDetailEntity) {
        appExecutors.diskIO()
            .execute { localDataSource.updateMovieDetail(movieDetailEntity) }
    }

    override fun updateShowDetail(showDetailEntity: ShowDetailEntity) {
        appExecutors.diskIO().execute { localDataSource.updateShowDetail(showDetailEntity) }
    }

    override fun setBookmarkMovieDetail(detailMovieId: Int, state: Int) {
        appExecutors.diskIO()
            .execute { localDataSource.setBookmarkMovieDetail(detailMovieId, state) }
    }

    override fun setBookmarkShowDetail(detailShowId: Int, state: Int) {
        appExecutors.diskIO().execute { localDataSource.setBookmarkShowDetail(detailShowId, state) }
    }
}