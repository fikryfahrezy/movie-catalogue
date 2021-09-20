package com.dicoding.picodiploma.moviecatalogue.ui.detail

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ShareCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dicoding.picodiploma.moviecatalogue.BuildConfig
import com.dicoding.picodiploma.moviecatalogue.R
import com.dicoding.picodiploma.moviecatalogue.data.source.local.entity.MovieCompanyEntity
import com.dicoding.picodiploma.moviecatalogue.data.source.local.entity.ShowCompanyEntity
import com.dicoding.picodiploma.moviecatalogue.data.source.local.entity.MovieDetailEntity
import com.dicoding.picodiploma.moviecatalogue.data.source.local.entity.ShowDetailEntity
import com.dicoding.picodiploma.moviecatalogue.databinding.ActivityDetailBinding
import com.dicoding.picodiploma.moviecatalogue.databinding.ContentScrollingBinding
import com.dicoding.picodiploma.moviecatalogue.ui.detail.companies.ShowCompaniesAdapter
import com.dicoding.picodiploma.moviecatalogue.ui.detail.companies.CompaniesItemCallback
import com.dicoding.picodiploma.moviecatalogue.ui.detail.companies.MovieCompaniesAdapter
import com.dicoding.picodiploma.moviecatalogue.ui.movie.MovieFragment
import com.dicoding.picodiploma.moviecatalogue.ui.show.ShowFragment
import com.dicoding.picodiploma.moviecatalogue.viewmodel.ViewModelFactory
import com.dicoding.picodiploma.moviecatalogue.vo.Status.*
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailActivity : AppCompatActivity(), CompaniesItemCallback {

    private lateinit var activityDetailBinding: ActivityDetailBinding
    private lateinit var detailViewModel: DetailViewModel
    private lateinit var detailContentBinding: ContentScrollingBinding

    companion object {
        const val EXTRA_MOVIE_ID = "extra_movie_id"
        const val EXTRA_FROM = "extra_name"
        const val EXTRA_SHOW = "extra_show"
        const val EXTRA_MOVIE = "extra_movie"
    }

    private fun finishActivity() {
        Toast.makeText(
            this@DetailActivity,
            getString(R.string.no_detail),
            Toast.LENGTH_SHORT
        ).show()
        finish()
    }

    private fun someSuccess() {
        activityDetailBinding.noDataText.visibility = View.GONE
        activityDetailBinding.progressBar.visibility = View.GONE
        activityDetailBinding.mainPage.visibility = View.VISIBLE
    }

    private fun someError() {
        activityDetailBinding.mainPage.visibility = View.GONE
        activityDetailBinding.progressBar.visibility = View.GONE
        activityDetailBinding.noDataText.visibility = View.VISIBLE
        activityDetailBinding.noDataText.text =
            resources?.getString(R.string.server_error)
    }

    private fun someLoading() {
        activityDetailBinding.mainPage.visibility = View.GONE
        activityDetailBinding.noDataText.visibility = View.GONE
        activityDetailBinding.progressBar.visibility = View.VISIBLE
    }

    private fun showSnackBar(text: String) {
        Snackbar.make(activityDetailBinding.root, text, Snackbar.LENGTH_LONG)
            .setAction("Action", null).show()
    }

    private fun populateMovieView(movie: MovieDetailEntity) {
        detailContentBinding.apply {
            movieTitle.text = movie.title
            val year = "(${movie.releaseDate})"
            movieYear.text = year
            val duration = "${movie.duration} time"
            movieDuration.text = duration
            movieGenre.text = movie.genres
            movieOriginalTitle.text = movie.originalTitle
            movieTagLine.text = movie.tagline
            movieOverview.text = movie.overview
            movieProductionCountries.text = movie.productionCountries
            movieStatus.text = movie.status
            movieLanguage.text = movie.spokenLanguages
        }
    }

    private fun populateTvShowView(show: ShowDetailEntity) {
        detailContentBinding.apply {
            movieTitle.text = show.name
            val year = "(${show.lastAirDate})"
            movieYear.text = year
            val duration = "${show.duration} total hours"
            movieDuration.text = duration
            movieGenre.text = show.genres
            movieOriginalTitle.text = show.originalName
            movieTagLine.text = show.tagline
            movieOverview.text = show.overview
            movieProductionCountries.text = show.productionCountries
            movieStatus.text = show.status
            movieLanguage.text = show.spokenLanguages
        }
    }

    private fun populateMovieCompanies(productionCompanies: List<MovieCompanyEntity>) {
        val movieCompaniesAdapter = MovieCompaniesAdapter(this)
        movieCompaniesAdapter.setCompanies(productionCompanies)

        with(detailContentBinding.rvCompanies) {
            isNestedScrollingEnabled = false
            layoutManager = LinearLayoutManager(
                context,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            adapter = movieCompaniesAdapter
        }
    }

    private fun populateShowCompanies(productionCompanies: List<ShowCompanyEntity>) {
        val showCompaniesAdapter = ShowCompaniesAdapter(this)
        showCompaniesAdapter.setCompanies(productionCompanies)

        with(detailContentBinding.rvCompanies) {
            isNestedScrollingEnabled = false
            layoutManager = LinearLayoutManager(
                context,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            adapter = showCompaniesAdapter
        }
    }

    private fun setBookmarkState(state: Boolean) {
        val bookmarkButton = detailContentBinding.bookmarkButton
        if (state) {
            bookmarkButton.text = resources.getString(R.string.bookmarked)
            val bookmarkedIcon =
                ContextCompat.getDrawable(this, R.drawable.ic_baseline_bookmark_black_24)
            bookmarkButton.setCompoundDrawablesRelativeWithIntrinsicBounds(
                bookmarkedIcon,
                null,
                null,
                null
            )
        } else {
            bookmarkButton.text = resources.getString(R.string.bookmark)
            val bookmarkIcon =
                ContextCompat.getDrawable(this, R.drawable.ic_baseline_bookmark_border_24)
            bookmarkButton.setCompoundDrawablesRelativeWithIntrinsicBounds(
                bookmarkIcon,
                null,
                null,
                null
            )
        }
    }

    private fun movieDetail(movieId: Int) {
        detailViewModel.getMovie(movieId).observe(this, { movieDetail ->
            if (movieDetail != null) {
                when (movieDetail.status) {
                    SUCCESS -> {
                        if (movieDetail.data !== null) {
                            someSuccess()
                            val movie = movieDetail.data
                            val detailedMovie = movie.movieDetailEntity

                            activityDetailBinding.toolbarLayout.title = detailedMovie.title
                            activityDetailBinding.fab.setOnClickListener {
                                val mimeType = "text/plain"
                                ShareCompat.IntentBuilder
                                    .from(this)
                                    .setType(mimeType)
                                    .setChooserTitle(detailedMovie.title)
                                    .setText(detailedMovie.title)
                                    .startChooser()
                            }

                            Glide.with(this)
                                .load("${BuildConfig.TMBDB_W500_IMAGE_URL}${detailedMovie.posterPath}")
                                .apply(RequestOptions.placeholderOf(R.drawable.ic_loading))
                                .error(R.drawable.ic_error)
                                .into(activityDetailBinding.imgPoster)
                            populateMovieView(detailedMovie)
                            setBookmarkState(detailedMovie.bookmarked)

                            val movieCompanies = movie.companies
                            populateMovieCompanies(movieCompanies)

                            detailContentBinding.bookmarkButton.setOnClickListener {
                                detailedMovie.bookmarked = !detailedMovie.bookmarked
                                detailViewModel.setBookmarkMovieDetail(detailedMovie)
                                val newState = if (detailedMovie.bookmarked) 1 else 0
                                detailViewModel.setBookmarkMovie(detailedMovie.id, newState)
                            }
                        }
                    }
                    LOADING -> someLoading()
                    ERROR -> someError()
                }
            }
        })
    }

    private fun tvShowDetail(tvShowId: Int) {
        detailViewModel.getShow(tvShowId).observe(this, { tvDetail ->
            if (tvDetail != null) {
                when (tvDetail.status) {
                    SUCCESS -> {
                        if (tvDetail.data != null) {
                            someSuccess()
                            val show = tvDetail.data
                            val detailedShow = show.showDetailEntity

                            activityDetailBinding.toolbarLayout.title = detailedShow.name
                            activityDetailBinding.fab.setOnClickListener {
                                val mimeType = "text/plain"
                                ShareCompat.IntentBuilder
                                    .from(this)
                                    .setType(mimeType)
                                    .setChooserTitle(detailedShow.name)
                                    .setText(detailedShow.name)
                                    .startChooser()
                            }

                            Glide.with(this)
                                .load("${BuildConfig.TMBDB_W500_IMAGE_URL}${detailedShow.posterPath}")
                                .apply(RequestOptions.placeholderOf(R.drawable.ic_loading))
                                .error(R.drawable.ic_error)
                                .into(activityDetailBinding.imgPoster)
                            populateTvShowView(detailedShow)
                            setBookmarkState(detailedShow.bookmarked)

                            val showCompanies = show.companies
                            populateShowCompanies(showCompanies)

                            detailContentBinding.bookmarkButton.setOnClickListener {
                                detailedShow.bookmarked = !detailedShow.bookmarked
                                detailViewModel.setBookmarkShowDetail(detailedShow)
                                val newState = if (detailedShow.bookmarked) 1 else 0
                                detailViewModel.setBookmarkShow(detailedShow.id, newState)
                            }
                        }
                    }
                    LOADING -> someLoading()
                    ERROR -> someError()
                }
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityDetailBinding = ActivityDetailBinding.inflate(layoutInflater)
        detailContentBinding = activityDetailBinding.detailContent
        setContentView(activityDetailBinding.root)
        setSupportActionBar(activityDetailBinding.toolbar)

        val viewModelFactory = ViewModelFactory.getInstance(this)
        detailViewModel = ViewModelProvider(
            this,
            viewModelFactory
        )[DetailViewModel::class.java]

        val extras = intent.extras
        val movieId = extras?.getInt(EXTRA_MOVIE_ID, -1)
        val callerName = extras?.getString(EXTRA_FROM)
        if (movieId == null || callerName.isNullOrEmpty()) {
            finishActivity()
        } else {
            detailViewModel.isLoading.observe(this, { isLoading ->
                if (isLoading) {
                    activityDetailBinding.progressBar.visibility = View.VISIBLE
                    activityDetailBinding.mainPage.visibility = View.GONE
                } else {
                    activityDetailBinding.progressBar.visibility = View.GONE
                }
            })
            detailViewModel.isError.observe(this, { isError ->
                if (isError) {
                    activityDetailBinding.noDataText.visibility = View.VISIBLE
                    activityDetailBinding.noDataText.text =
                        resources.getString(R.string.server_error)
                    activityDetailBinding.mainPage.visibility = View.GONE
                } else {
                    activityDetailBinding.noDataText.visibility = View.GONE
                }
            })
            if (callerName == MovieFragment.FROM_MOVIE) {
                movieDetail(movieId)
            } else if (callerName == ShowFragment.FROM_SHOW) {
                tvShowDetail(movieId)
            }
        }
    }

    override fun onShowCompanyClick(company: ShowCompanyEntity) {
        showSnackBar(company.name)
    }

    override fun onMovieCompanyClick(company: MovieCompanyEntity) {
        showSnackBar(company.name)
    }
}