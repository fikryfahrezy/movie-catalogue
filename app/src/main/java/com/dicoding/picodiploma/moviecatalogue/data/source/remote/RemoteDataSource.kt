package com.dicoding.picodiploma.moviecatalogue.data.source.remote

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dicoding.picodiploma.moviecatalogue.config.FetchConfig
import com.dicoding.picodiploma.moviecatalogue.data.source.remote.response.*
import com.dicoding.picodiploma.moviecatalogue.utils.EspressoIdlingResource
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class RemoteDataSource @Inject constructor(private val fetchConfig: FetchConfig) {

    fun getMovies(): LiveData<ApiResponse<PopularMoviesResponse?>> {
        // UNCOMMENT THIS IF DOING TESTING
        EspressoIdlingResource.increment()
        val moviesResult = MutableLiveData<ApiResponse<PopularMoviesResponse?>>()
        val fetch = fetchConfig.getFetchService().getPopularMovies()
        fetch.enqueue(object : Callback<PopularMoviesResponse> {
            override fun onResponse(
                call: Call<PopularMoviesResponse>,
                response: Response<PopularMoviesResponse>
            ) {
                if (response.isSuccessful) {
                    if (response.body()?.results.isNullOrEmpty()) moviesResult.value =
                        ApiResponse.empty(response.message(), response.body())
                    else moviesResult.value = ApiResponse.success(response.body())
                } else moviesResult.value = ApiResponse.error(response.message(), null)
                // UNCOMMENT THIS IF DOING TESTING
                EspressoIdlingResource.decrement()
            }

            override fun onFailure(call: Call<PopularMoviesResponse>, t: Throwable) {
                moviesResult.value = ApiResponse.error(t.toString(), null)
                // UNCOMMENT THIS IF DOING TESTING
                EspressoIdlingResource.decrement()
            }
        })
        return moviesResult
    }

    fun getShows(): LiveData<ApiResponse<PopularTvShowsResponse?>> {
        // UNCOMMENT THIS IF DOING TESTING
        EspressoIdlingResource.increment()
        val resultShows = MutableLiveData<ApiResponse<PopularTvShowsResponse?>>()
        val fetch = fetchConfig.getFetchService().getPopularTvShows()
        fetch.enqueue(object : Callback<PopularTvShowsResponse> {
            override fun onResponse(
                call: Call<PopularTvShowsResponse>,
                response: Response<PopularTvShowsResponse>
            ) {
                if (response.isSuccessful) {
                    if (response.body()?.results.isNullOrEmpty())
                        resultShows.value = ApiResponse.empty(response.message(), response.body())
                    else resultShows.value = ApiResponse.success(response.body())
                } else resultShows.value = ApiResponse.error(response.message(), null)
                // UNCOMMENT THIS IF DOING TESTING
                EspressoIdlingResource.decrement()
            }

            override fun onFailure(call: Call<PopularTvShowsResponse>, t: Throwable) {
                resultShows.value = ApiResponse.error(t.toString(), null)
                // UNCOMMENT THIS IF DOING TESTING
                EspressoIdlingResource.decrement()
            }
        })
        return resultShows
    }

    fun getMovie(movieId: Int): LiveData<ApiResponse<MovieDetailResponse?>> {
        // UNCOMMENT THIS IF DOING TESTING
        EspressoIdlingResource.increment()
        val resultMovie = MutableLiveData<ApiResponse<MovieDetailResponse?>>()
        val fetch = fetchConfig.getFetchService().getMovieDetail(movieId.toString())
        fetch.enqueue(object : Callback<MovieDetailResponse> {
            override fun onResponse(
                call: Call<MovieDetailResponse>,
                response: Response<MovieDetailResponse>
            ) {
                if (response.isSuccessful && response.body() != null)
                    resultMovie.value = ApiResponse.success(response.body())
                else resultMovie.value = ApiResponse.empty(response.message(), response.body())
                // UNCOMMENT THIS IF DOING TESTING
                EspressoIdlingResource.decrement()
            }

            override fun onFailure(call: Call<MovieDetailResponse>, t: Throwable) {
                resultMovie.value = ApiResponse.error(t.toString(), null)
                // UNCOMMENT THIS IF DOING TESTING
                EspressoIdlingResource.decrement()
            }
        })

        return resultMovie
    }

    fun getShow(showId: Int): LiveData<ApiResponse<TvShowDetailResponse?>> {
        // UNCOMMENT THIS IF DOING TESTING
        EspressoIdlingResource.increment()
        val resultShow = MutableLiveData<ApiResponse<TvShowDetailResponse?>>()
        val fetch = fetchConfig.getFetchService().getTvShowDetail(showId.toString())
        fetch.enqueue(object : Callback<TvShowDetailResponse> {
            override fun onResponse(
                call: Call<TvShowDetailResponse>,
                response: Response<TvShowDetailResponse>
            ) {
                if (response.isSuccessful && response.body() != null)
                    resultShow.value = ApiResponse.success(response.body())
                else resultShow.value = ApiResponse.empty(response.message(), response.body())
                // UNCOMMENT THIS IF DOING TESTING
                EspressoIdlingResource.decrement()
            }

            override fun onFailure(call: Call<TvShowDetailResponse>, t: Throwable) {
                resultShow.value = ApiResponse.error(t.toString(), null)
                // UNCOMMENT THIS IF DOING TESTING
                EspressoIdlingResource.decrement()
            }
        })

        return resultShow
    }
}