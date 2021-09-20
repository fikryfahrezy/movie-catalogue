package com.dicoding.picodiploma.moviecatalogue.ui.favorite.movie

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.paging.PagedList
import com.dicoding.picodiploma.moviecatalogue.data.source.MovieRepository
import com.dicoding.picodiploma.moviecatalogue.data.source.local.entity.MovieEntity
import com.dicoding.picodiploma.moviecatalogue.ui.favorite.tab.movie.FavoriteMovieViewModel
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
class FavoriteMovieViewModelTest {

    private lateinit var favoriteMovieViewModel: FavoriteMovieViewModel

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var movieRepository: MovieRepository

    @Mock
    private lateinit var moviesObserver: Observer<PagedList<MovieEntity?>>

    @Mock
    private lateinit var pagedList: PagedList<MovieEntity>

    @Before
    fun setUp() {
        favoriteMovieViewModel = FavoriteMovieViewModel(movieRepository)
    }

    @Test
    fun getMovies() {
        val expectedResultSize = 20
        val moviesResponse = pagedList as PagedList<MovieEntity?>
        `when`(moviesResponse.size).thenReturn(expectedResultSize)
        val movies = MutableLiveData<PagedList<MovieEntity?>>()
        movies.value = moviesResponse

        val sqlQuery = SqlUtils.getMoviesQuery("", true, "")
        `when`(movieRepository.getBookmarkedMovies(sqlQuery)).thenReturn(movies)

        val movieEntities = favoriteMovieViewModel.getBookmarkedMovies(sqlQuery).value
        verify(movieRepository).getBookmarkedMovies(sqlQuery)
        assertNotNull(movieEntities)
        assertEquals(expectedResultSize, movieEntities?.size)

        favoriteMovieViewModel.getBookmarkedMovies(sqlQuery).observeForever(moviesObserver)
        verify(moviesObserver).onChanged(moviesResponse)
    }
}