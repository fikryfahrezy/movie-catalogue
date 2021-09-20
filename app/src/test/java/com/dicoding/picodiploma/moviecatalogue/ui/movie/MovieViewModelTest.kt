package com.dicoding.picodiploma.moviecatalogue.ui.movie

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.paging.PagedList
import com.dicoding.picodiploma.moviecatalogue.data.source.MovieRepository
import com.dicoding.picodiploma.moviecatalogue.data.source.local.entity.MovieEntity
import com.dicoding.picodiploma.moviecatalogue.utils.SqlUtils
import com.dicoding.picodiploma.moviecatalogue.vo.Resource
import com.nhaarman.mockitokotlin2.verify
import org.junit.Test
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

/**
 * DON'T FORGET TO UNCOMMENT `EspressoIdlingResource.increment()` AND
 * `EspressoIdlingResource.decrement()` IN `RemoteDataSource` OR `LocalDataSource`
 */
@RunWith(MockitoJUnitRunner::class)
class MovieViewModelTest {

    private lateinit var movieViewModel: MovieViewModel

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var movieRepository: MovieRepository

    @Mock
    private lateinit var moviesObserver: Observer<Resource<PagedList<MovieEntity>?>>

    @Mock
    private lateinit var pagedList: PagedList<MovieEntity>

    @Before
    fun setUp() {
        movieViewModel = MovieViewModel(movieRepository)
    }

    @Test
    fun getMovies() {
        val expectedResultSize = 20
        val moviesResponse = Resource.success(pagedList) as Resource<PagedList<MovieEntity>?>?
        `when`(moviesResponse?.data?.size).thenReturn(expectedResultSize)
        val movies = MutableLiveData<Resource<PagedList<MovieEntity>?>>()
        movies.value = moviesResponse

        val sqlQuery = SqlUtils.getMoviesQuery("", false, "")
        `when`(movieRepository.getMovies(sqlQuery)).thenReturn(movies)

        val movieEntities = movieViewModel.getMovies(sqlQuery).value?.data
        verify(movieRepository).getMovies(sqlQuery)
        assertNotNull(movieEntities)
        assertEquals(expectedResultSize, movieEntities?.size)

        movieViewModel.getMovies(sqlQuery).observeForever(moviesObserver)
        verify(moviesObserver).onChanged(moviesResponse)
    }
}