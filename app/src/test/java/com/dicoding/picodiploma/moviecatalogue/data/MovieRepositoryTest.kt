package com.dicoding.picodiploma.moviecatalogue.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.dicoding.picodiploma.moviecatalogue.config.FetchConfig
import com.dicoding.picodiploma.moviecatalogue.data.source.local.LocalDataSource
import com.dicoding.picodiploma.moviecatalogue.data.source.local.entity.*
import com.dicoding.picodiploma.moviecatalogue.data.source.remote.RemoteDataSource
import com.dicoding.picodiploma.moviecatalogue.data.source.remote.response.MoviesResultItem
import com.dicoding.picodiploma.moviecatalogue.data.source.remote.response.TvShowsResultItem
import com.dicoding.picodiploma.moviecatalogue.utils.AppExecutors
import com.dicoding.picodiploma.moviecatalogue.utils.SqlUtils
import com.dicoding.picodiploma.moviecatalogue.utlis.LiveDataTestUtil
import com.dicoding.picodiploma.moviecatalogue.utlis.PagedListUtil
import com.dicoding.picodiploma.moviecatalogue.vo.Resource
import com.nhaarman.mockitokotlin2.verify
import org.junit.Assert.*
import org.mockito.Mockito.mock
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.`when`

/**
 * DON'T FORGET TO UNCOMMENT `EspressoIdlingResource.increment()` AND
 * `EspressoIdlingResource.decrement()` IN `RemoteDataSource` OR `LocalDataSource`
 */
class MovieRepositoryTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private val localDataSource = mock(LocalDataSource::class.java)
    private val remoteDataSource = mock(RemoteDataSource::class.java)
    private val appExecutors = mock(AppExecutors::class.java)

    private val movieRepository =
        FakeMovieRepository(remoteDataSource, localDataSource, appExecutors)
    private val movieId = 464052
    private val showId = 77169


    @Test
    fun getMovies() {
        val fetch = FetchConfig().getFetchService().getPopularMovies().execute()
        val movies = fetch.body()

        val dataSourceFactory =
            mock(DataSource.Factory::class.java) as DataSource.Factory<Int, MovieEntity>

        val sqlQuery = SqlUtils.getMoviesQuery("", false, "")
        `when`(localDataSource.getMovies(sqlQuery)).thenReturn(
            dataSourceFactory
        )
        movieRepository.getMovies(sqlQuery)

        val movieEntities =
            Resource.success(PagedListUtil.mockPagedList(movies?.results as List<MoviesResultItem>))
        verify(localDataSource).getMovies(sqlQuery)
        assertNotNull(movieEntities.data)
        assertEquals(movies.results.size, movieEntities.data?.size)
    }

    @Test
    fun getShows() {
        val fetch = FetchConfig().getFetchService().getPopularTvShows().execute()
        val shows = fetch.body()

        val dataSourceFactory =
            mock(DataSource.Factory::class.java) as DataSource.Factory<Int, ShowEntity>

        val sqlQuery = SqlUtils.getShowsQuery("", false, "")
        `when`(localDataSource.getShows(sqlQuery)).thenReturn(dataSourceFactory)
        movieRepository.getShows(sqlQuery)

        val showEntities =
            Resource.success(PagedListUtil.mockPagedList(shows?.results as List<TvShowsResultItem>))
        verify(localDataSource).getShows(sqlQuery)
        assertNotNull(showEntities.data)
        assertEquals(shows.results.size, showEntities.data?.size)
    }

    @Test
    fun getMovie() {
        val movieEntity = MutableLiveData<MovieDetailWithCompany?>()
        val fetch = FetchConfig().getFetchService().getMovieDetail(movieId.toString()).execute()
        val movie = fetch.body()

        var movieDetailWithCompany: MovieDetailWithCompany? = null
        if (movie != null) {
            val movieDetail = MovieDetailEntity(
                movie.id,
                movie.title,
                movie.overview,
                movie.posterPath,
                movie.releaseDate,
                movie.runtime,
                movie.genres.joinToString { it.name },
                movie.originalTitle,
                movie.tagline,
                movie.productionCountries.joinToString { it.name },
                movie.status,
                movie.spokenLanguages.joinToString { it.name },
                false
            )
            val companies = ArrayList<MovieCompanyEntity>()
            for (companyData in movie.productionCompanies) {
                val company = MovieCompanyEntity(
                    companyData.id,
                    movie.id,
                    companyData.name,
                    companyData.logoPath,
                    companyData.originCountry,
                )
                companies.add(company)
            }
            movieDetailWithCompany = MovieDetailWithCompany(movieDetail, companies)
        }
        movieEntity.value = movieDetailWithCompany
        `when`(localDataSource.getMovie(movieId)).thenReturn(movieEntity as MutableLiveData<MovieDetailWithCompany>)

        val detailMovieEntity = LiveDataTestUtil.getValue(movieRepository.getMovie(movieId))
        verify(localDataSource).getMovie(movieId)
        assertNotNull(detailMovieEntity)
    }

    @Test
    fun getShow() {
        val showEntity = MutableLiveData<ShowDetailWithCompany?>()
        val fetch = FetchConfig().getFetchService().getTvShowDetail(showId.toString()).execute()
        val show = fetch.body()

        var showDetailWithCompany: ShowDetailWithCompany? = null
        if (show != null) {
            val showDetail = ShowDetailEntity(
                show.id,
                show.name,
                show.overview,
                show.posterPath,
                show.firstAirDate,
                show.lastAirDate,
                show.episodeRunTime.size,
                show.genres.joinToString { it.name },
                show.originalName,
                show.tagline,
                show.productionCountries.joinToString { it.name },
                show.status,
                show.spokenLanguages.joinToString { it.name },
                false
            )

            val companies = ArrayList<ShowCompanyEntity>()
            for (companyData in show.productionCompanies) {
                val company = ShowCompanyEntity(
                    companyData.id,
                    show.id,
                    companyData.name,
                    companyData.logoPath,
                    companyData.originCountry
                )
                companies.add(company)
            }

            showDetailWithCompany = ShowDetailWithCompany(showDetail, companies)
        }
        showEntity.value = showDetailWithCompany
        `when`(localDataSource.getShow(showId)).thenReturn(showEntity as MutableLiveData<ShowDetailWithCompany>)

        val detailShowEntity = LiveDataTestUtil.getValue(movieRepository.getShow(showId))
        verify(localDataSource).getShow(showId)
        assertNotNull(detailShowEntity)
    }

    @Test
    fun getBookmarkedMovies() {
        val fetch = FetchConfig().getFetchService().getPopularMovies().execute()
        val movies = fetch.body()

        val dataSourceFactory =
            mock(DataSource.Factory::class.java) as DataSource.Factory<Int, MovieEntity>

        val sqlQuery = SqlUtils.getMoviesQuery("", true, "")
        `when`(localDataSource.getMovies(sqlQuery)).thenReturn(
            dataSourceFactory
        )
        movieRepository.getBookmarkedMovies(sqlQuery)

        val movieEntities =
            Resource.success(PagedListUtil.mockPagedList(movies?.results as List<MoviesResultItem>))
        verify(localDataSource).getMovies(sqlQuery)
        assertNotNull(movieEntities.data)
        assertEquals(movies.results.size, movieEntities.data?.size)
    }

    @Test
    fun getBookmarkedShows() {
        val fetch = FetchConfig().getFetchService().getPopularTvShows().execute()
        val shows = fetch.body()

        val dataSourceFactory =
            mock(DataSource.Factory::class.java) as DataSource.Factory<Int, ShowEntity>

        val sqlQuery = SqlUtils.getShowsQuery("", true, "")
        `when`(localDataSource.getShows(sqlQuery)).thenReturn(dataSourceFactory)
        movieRepository.getBookmarkedShows(sqlQuery)

        val showEntities =
            Resource.success(PagedListUtil.mockPagedList(shows?.results as List<TvShowsResultItem>))
        verify(localDataSource).getShows(sqlQuery)
        assertNotNull(showEntities.data)
        assertEquals(shows.results.size, showEntities.data?.size)
    }

    @Test
    fun setBookmarkMovie() {
        val fetch = FetchConfig().getFetchService().getPopularMovies().execute()
        val movies = fetch.body()

        val dataSourceFactory =
            mock(DataSource.Factory::class.java) as DataSource.Factory<Int, MovieEntity>

        val sqlQuery = SqlUtils.getMoviesQuery("", false, "")
        `when`(localDataSource.getMovies(sqlQuery)).thenReturn(
            dataSourceFactory
        )
        movieRepository.getMovies(sqlQuery)

        val movieEntities =
            Resource.success(PagedListUtil.mockPagedList(movies?.results as List<MoviesResultItem>))
        verify(localDataSource).getMovies(sqlQuery)
        assertNotNull(movieEntities.data)
        assertEquals(movies.results.size, movieEntities.data?.size)

        if (movies.results.isNotEmpty()) {
            val movie = movies.results[0]

            val state = 1
            movieRepository.setBookmarkMovie(movie.id, state)
            verify(localDataSource).setBookmarkMovie(movie.id, state)
        }
    }

    @Test
    fun setBookmarkShow() {
        val fetch = FetchConfig().getFetchService().getPopularTvShows().execute()
        val shows = fetch.body()

        val dataSourceFactory =
            mock(DataSource.Factory::class.java) as DataSource.Factory<Int, ShowEntity>

        val sqlQuery = SqlUtils.getShowsQuery("", true, "")
        `when`(localDataSource.getShows(sqlQuery)).thenReturn(dataSourceFactory)
        movieRepository.getBookmarkedShows(sqlQuery)

        val showEntities =
            Resource.success(PagedListUtil.mockPagedList(shows?.results as List<TvShowsResultItem>))
        verify(localDataSource).getShows(sqlQuery)
        assertNotNull(showEntities.data)
        assertEquals(shows.results.size, showEntities.data?.size)

        if (shows.results.isNotEmpty()) {
            val show = shows.results[0]

            val state = 1
            movieRepository.setBookmarkShow(show.id, state)
            verify(localDataSource).setBookmarkShow(show.id, state)
        }
    }

    @Test
    fun updateMovieDetail() {
        val movieEntity = MutableLiveData<MovieDetailWithCompany?>()
        val fetch = FetchConfig().getFetchService().getMovieDetail(movieId.toString()).execute()
        val movie = fetch.body()

        var movieDetailWithCompany: MovieDetailWithCompany? = null
        if (movie != null) {
            val movieDetail = MovieDetailEntity(
                movie.id,
                movie.title,
                movie.overview,
                movie.posterPath,
                movie.releaseDate,
                movie.runtime,
                movie.genres.joinToString { it.name },
                movie.originalTitle,
                movie.tagline,
                movie.productionCountries.joinToString { it.name },
                movie.status,
                movie.spokenLanguages.joinToString { it.name },
                false
            )
            val companies = ArrayList<MovieCompanyEntity>()
            for (companyData in movie.productionCompanies) {
                val company = MovieCompanyEntity(
                    companyData.id,
                    movie.id,
                    companyData.name,
                    companyData.logoPath,
                    companyData.originCountry,
                )
                companies.add(company)
            }
            movieDetailWithCompany = MovieDetailWithCompany(movieDetail, companies)
        }
        movieEntity.value = movieDetailWithCompany
        `when`(localDataSource.getMovie(movieId)).thenReturn(movieEntity as MutableLiveData<MovieDetailWithCompany>)

        val detailMovieEntity = LiveDataTestUtil.getValue(movieRepository.getMovie(movieId))
        verify(localDataSource).getMovie(movieId)
        assertNotNull(detailMovieEntity)

        val detailMovie = detailMovieEntity.data?.movieDetailEntity
        detailMovie?.bookmarked = true
        assertNotNull(detailMovie)

        movieRepository.updateMovieDetail(detailMovie as MovieDetailEntity)
        verify(localDataSource).updateMovieDetail(detailMovie)

        val newDetailMovie = LiveDataTestUtil.getValue(movieRepository.getMovie(movieId))
        assertEquals(detailMovie.bookmarked, true)
        assertEquals(detailMovie.bookmarked, newDetailMovie.data?.movieDetailEntity?.bookmarked)
    }

    @Test
    fun updateShowDetail() {
        val showEntity = MutableLiveData<ShowDetailWithCompany?>()
        val fetch = FetchConfig().getFetchService().getTvShowDetail(showId.toString()).execute()
        val show = fetch.body()

        var showDetailWithCompany: ShowDetailWithCompany? = null
        if (show != null) {
            val showDetail = ShowDetailEntity(
                show.id,
                show.name,
                show.overview,
                show.posterPath,
                show.firstAirDate,
                show.lastAirDate,
                show.episodeRunTime.size,
                show.genres.joinToString { it.name },
                show.originalName,
                show.tagline,
                show.productionCountries.joinToString { it.name },
                show.status,
                show.spokenLanguages.joinToString { it.name },
                false
            )

            val companies = ArrayList<ShowCompanyEntity>()
            for (companyData in show.productionCompanies) {
                val company = ShowCompanyEntity(
                    companyData.id,
                    show.id,
                    companyData.name,
                    companyData.logoPath,
                    companyData.originCountry
                )
                companies.add(company)
            }

            showDetailWithCompany = ShowDetailWithCompany(showDetail, companies)
        }
        showEntity.value = showDetailWithCompany
        `when`(localDataSource.getShow(showId)).thenReturn(showEntity as MutableLiveData<ShowDetailWithCompany>)

        val detailShowEntity = LiveDataTestUtil.getValue(movieRepository.getShow(showId))
        verify(localDataSource).getShow(showId)
        assertNotNull(detailShowEntity)

        val detailShow = detailShowEntity.data?.showDetailEntity
        detailShow?.bookmarked = true
        assertNotNull(detailShow)

        movieRepository.updateShowDetail(detailShow as ShowDetailEntity)
        verify(localDataSource).updateShowDetail(detailShow)

        val newDetailShow = LiveDataTestUtil.getValue(movieRepository.getShow(showId))
        assertEquals(detailShow.bookmarked, true)
        assertEquals(detailShow.bookmarked, newDetailShow.data?.showDetailEntity?.bookmarked)
    }

    @Test
    fun setBookmarkMovieDetail() {
        val movieEntity = MutableLiveData<MovieDetailWithCompany?>()
        val fetch = FetchConfig().getFetchService().getMovieDetail(movieId.toString()).execute()
        val movie = fetch.body()

        var movieDetailWithCompany: MovieDetailWithCompany? = null
        if (movie != null) {
            val movieDetail = MovieDetailEntity(
                movie.id,
                movie.title,
                movie.overview,
                movie.posterPath,
                movie.releaseDate,
                movie.runtime,
                movie.genres.joinToString { it.name },
                movie.originalTitle,
                movie.tagline,
                movie.productionCountries.joinToString { it.name },
                movie.status,
                movie.spokenLanguages.joinToString { it.name },
                false
            )
            val companies = ArrayList<MovieCompanyEntity>()
            for (companyData in movie.productionCompanies) {
                val company = MovieCompanyEntity(
                    companyData.id,
                    movie.id,
                    companyData.name,
                    companyData.logoPath,
                    companyData.originCountry,
                )
                companies.add(company)
            }
            movieDetailWithCompany = MovieDetailWithCompany(movieDetail, companies)
        }
        movieEntity.value = movieDetailWithCompany
        `when`(localDataSource.getMovie(movieId)).thenReturn(movieEntity as MutableLiveData<MovieDetailWithCompany>)

        val detailMovieEntity = LiveDataTestUtil.getValue(movieRepository.getMovie(movieId))
        verify(localDataSource).getMovie(movieId)
        assertNotNull(detailMovieEntity)

        val detailMovie = detailMovieEntity.data?.movieDetailEntity
        detailMovie?.bookmarked = true
        assertNotNull(detailMovie)

        val state = 1
        movieRepository.setBookmarkMovieDetail(detailMovie?.id as Int, state)
        verify(localDataSource).setBookmarkMovieDetail(detailMovie.id, state)
    }

    @Test
    fun setBookmarkShowDetail() {
        val showEntity = MutableLiveData<ShowDetailWithCompany?>()
        val fetch = FetchConfig().getFetchService().getTvShowDetail(showId.toString()).execute()
        val show = fetch.body()

        var showDetailWithCompany: ShowDetailWithCompany? = null
        if (show != null) {
            val showDetail = ShowDetailEntity(
                show.id,
                show.name,
                show.overview,
                show.posterPath,
                show.firstAirDate,
                show.lastAirDate,
                show.episodeRunTime.size,
                show.genres.joinToString { it.name },
                show.originalName,
                show.tagline,
                show.productionCountries.joinToString { it.name },
                show.status,
                show.spokenLanguages.joinToString { it.name },
                false
            )

            val companies = ArrayList<ShowCompanyEntity>()
            for (companyData in show.productionCompanies) {
                val company = ShowCompanyEntity(
                    companyData.id,
                    show.id,
                    companyData.name,
                    companyData.logoPath,
                    companyData.originCountry
                )
                companies.add(company)
            }

            showDetailWithCompany = ShowDetailWithCompany(showDetail, companies)
        }
        showEntity.value = showDetailWithCompany
        `when`(localDataSource.getShow(showId)).thenReturn(showEntity as MutableLiveData<ShowDetailWithCompany>)

        val detailShowEntity = LiveDataTestUtil.getValue(movieRepository.getShow(showId))
        verify(localDataSource).getShow(showId)
        assertNotNull(detailShowEntity)

        val detailShow = detailShowEntity.data?.showDetailEntity
        assertNotNull(detailShow)

        val state = 1
        movieRepository.setBookmarkShowDetail(detailShow?.id as Int, state)
        verify(localDataSource).setBookmarkShowDetail(detailShow.id, state)
    }

}