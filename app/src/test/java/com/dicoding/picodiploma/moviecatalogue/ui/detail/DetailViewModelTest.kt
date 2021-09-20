package com.dicoding.picodiploma.moviecatalogue.ui.detail

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.dicoding.picodiploma.moviecatalogue.config.FetchConfig
import com.dicoding.picodiploma.moviecatalogue.data.source.MovieRepository
import com.dicoding.picodiploma.moviecatalogue.data.source.local.entity.*
import com.dicoding.picodiploma.moviecatalogue.vo.Resource
import com.nhaarman.mockitokotlin2.verify
import org.junit.Test
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

/**
 * DON'T FORGET TO UNCOMMENT `EspressoIdlingResource.increment()` AND
 * `EspressoIdlingResource.decrement()` IN `RemoteDataSource` OR `LocalDataSource`
 */
@RunWith(MockitoJUnitRunner::class)
class DetailViewModelTest {

    private lateinit var detailViewModel: DetailViewModel
    private val movieId = 464052
    private val showId = 77169

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var movieRepository: MovieRepository

    @Mock
    private lateinit var showObserver: Observer<Resource<ShowDetailWithCompany>>

    @Mock
    private lateinit var movieObserver: Observer<Resource<MovieDetailWithCompany>>

    @Before
    fun setUp() {
        detailViewModel = DetailViewModel(movieRepository)
    }

    @Test
    fun getMovie() {
        val mutableMovie = MutableLiveData<Resource<MovieDetailWithCompany>>()
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
        val movieResult = Resource.success(movieDetailWithCompany)
        mutableMovie.value = movieResult

        Mockito.`when`(movieRepository.getMovie(movieId))
            .thenReturn(mutableMovie)
        val movieEntity = detailViewModel.getMovie(movieId).value
        verify(movieRepository).getMovie(movieId)
        assertNotNull(movieEntity)

        detailViewModel.getMovie(movieId).observeForever(movieObserver)
        verify(movieObserver).onChanged(movieResult)
    }

    @Test
    fun getShow() {
        val mutableShow = MutableLiveData<Resource<ShowDetailWithCompany>>()
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
        val showResult = Resource.success(showDetailWithCompany)
        mutableShow.value = showResult

        Mockito.`when`(movieRepository.getShow(showId))
            .thenReturn(mutableShow)
        val showEntity = detailViewModel.getShow(showId).value
        verify(movieRepository).getShow(showId)
        assertNotNull(showEntity)

        detailViewModel.getShow(showId).observeForever(showObserver)
        verify(showObserver).onChanged(showResult)
    }
}