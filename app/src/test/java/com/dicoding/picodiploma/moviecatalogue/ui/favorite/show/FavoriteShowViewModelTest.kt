package com.dicoding.picodiploma.moviecatalogue.ui.favorite.show

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.paging.PagedList
import com.dicoding.picodiploma.moviecatalogue.data.source.MovieRepository
import com.dicoding.picodiploma.moviecatalogue.data.source.local.entity.ShowEntity
import com.dicoding.picodiploma.moviecatalogue.ui.favorite.tab.show.FavoriteShowViewModel
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
class FavoriteShowViewModelTest {

    private lateinit var favoriteShowViewModel: FavoriteShowViewModel

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var movieRepository: MovieRepository

    @Mock
    private lateinit var showsObserver: Observer<PagedList<ShowEntity?>>

    @Mock
    private lateinit var pagedList: PagedList<ShowEntity>

    @Before
    fun setUp() {
        favoriteShowViewModel = FavoriteShowViewModel(movieRepository)
    }

    @Test
    fun getMovies() {
        val expectedResultSize = 20
        val showsResponse = pagedList as PagedList<ShowEntity?>
        `when`(showsResponse.size).thenReturn(expectedResultSize)
        val shows = MutableLiveData<PagedList<ShowEntity?>>()
        shows.value = showsResponse

        val sqlQuery = SqlUtils.getShowsQuery("", true, "")
        `when`(movieRepository.getBookmarkedShows(sqlQuery)).thenReturn(shows)

        val showsEntities = favoriteShowViewModel.getBookmarkedShows(sqlQuery).value
        verify(movieRepository).getBookmarkedShows(sqlQuery)
        assertNotNull(showsEntities)
        assertEquals(expectedResultSize, showsEntities?.size)

        favoriteShowViewModel.getBookmarkedShows(sqlQuery).observeForever(showsObserver)
        verify(showsObserver).onChanged(showsResponse)
    }
}