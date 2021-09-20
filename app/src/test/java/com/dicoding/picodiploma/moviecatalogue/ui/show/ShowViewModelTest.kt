package com.dicoding.picodiploma.moviecatalogue.ui.show

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.paging.PagedList
import com.dicoding.picodiploma.moviecatalogue.data.source.MovieRepository
import com.dicoding.picodiploma.moviecatalogue.data.source.local.entity.ShowEntity
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
class ShowViewModelTest {

    private lateinit var showViewModel: ShowViewModel

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var movieRepository: MovieRepository

    @Mock
    private lateinit var showsObserver: Observer<Resource<PagedList<ShowEntity>?>>

    @Mock
    private lateinit var pagedList: PagedList<ShowEntity>

    @Before
    fun setUp() {
        showViewModel = ShowViewModel(movieRepository)
    }

    @Test
    fun getMovies() {
        val expectedResultSize = 20
        val showsResponse = Resource.success(pagedList) as Resource<PagedList<ShowEntity>?>?
        `when`(showsResponse?.data?.size).thenReturn(expectedResultSize)
        val shows = MutableLiveData<Resource<PagedList<ShowEntity>?>>()
        shows.value = showsResponse

        val sqlQuery = SqlUtils.getShowsQuery("", false, "")
        `when`(movieRepository.getShows(sqlQuery)).thenReturn(shows)

        val showsEntities = showViewModel.getShows(sqlQuery).value?.data
        verify(movieRepository).getShows(sqlQuery)
        assertNotNull(showsEntities)
        assertEquals(expectedResultSize, showsEntities?.size)

        showViewModel.getShows(sqlQuery).observeForever(showsObserver)
        verify(showsObserver).onChanged(showsResponse)
    }
}