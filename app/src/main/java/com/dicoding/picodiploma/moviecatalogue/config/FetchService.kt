package com.dicoding.picodiploma.moviecatalogue.config

import com.dicoding.picodiploma.moviecatalogue.BuildConfig
import com.dicoding.picodiploma.moviecatalogue.data.source.remote.response.*
import retrofit2.Call
import retrofit2.http.*

interface FetchService {
    @GET("movie/popular")
    fun getPopularMovies(
        @Query("page") page: Int = 1,
        @Query("region") region: String = "US",
        @Query("language") language: String = "en-US",
        @Query("api_key") api_key: String = BuildConfig.TMDB_API_KEY
    ): Call<PopularMoviesResponse>

    @GET("tv/popular")
    fun getPopularTvShows(
        @Query("page") page: Int = 1,
        @Query("language") language: String = "en-US",
        @Query("api_key") api_key: String = BuildConfig.TMDB_API_KEY
    ): Call<PopularTvShowsResponse>

    @GET("movie/{id}")
    fun getMovieDetail(
        @Path("id") id: String,
        @Query("language") language: String = "en-US",
        @Query("api_key") api_key: String = BuildConfig.TMDB_API_KEY
    ): Call<MovieDetailResponse>

    @GET("tv/{id}")
    fun getTvShowDetail(
        @Path("id") id: String,
        @Query("language") language: String = "en-US",
        @Query("api_key") api_key: String = BuildConfig.TMDB_API_KEY
    ): Call<TvShowDetailResponse>
}