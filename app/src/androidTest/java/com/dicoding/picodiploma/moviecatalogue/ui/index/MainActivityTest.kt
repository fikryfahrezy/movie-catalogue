package com.dicoding.picodiploma.moviecatalogue.ui.index

import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.dicoding.picodiploma.moviecatalogue.R
import com.dicoding.picodiploma.moviecatalogue.config.FetchConfig
import com.dicoding.picodiploma.moviecatalogue.ui.detail.DetailActivity
import com.dicoding.picodiploma.moviecatalogue.utils.EspressoIdlingResource
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Get text of TextView inside RecyclerView - android instrumental test
 * https://stackoverflow.com/questions/38873784/get-text-of-textview-inside-recyclerview-android-instrumental-test
 *
 * RecyclerView - Get view at particular position
 * https://stackoverflow.com/questions/33784369/recyclerview-get-view-at-particular-position/38640656
 *
 * Recyclerview, I can't get item view with getChildAt(position). Null object reference
 * https://stackoverflow.com/questions/28300146/recyclerview-i-cant-get-item-view-with-getchildatposition-null-object-refer
 *
 * How to access RecyclerView ViewHolder with Espresso?
 * https://stackoverflow.com/questions/51678563/how-to-access-recyclerview-viewholder-with-espresso
 *
 * Testing RecyclerView if it has data with Espresso
 * https://stackoverflow.com/questions/40140700/testing-recyclerview-if-it-has-data-with-espresso
 *
 * Fix Kotlin and new ActivityTestRule : The @Rule must be public
 * https://proandroiddev.com/fix-kotlin-and-new-activitytestrule-the-rule-must-be-public-f0c5c583a865
 *
 * How to unit test Retrofit api calls?
 * https://stackoverflow.com/questions/32374508/how-to-unit-test-retrofit-api-calls
 *
 * Could not launch intent within 45 seconds. Perhaps the main thread has not gone idle within a reasonable amount of time?
 * https://stackoverflow.com/questions/46192089/could-not-launch-intent-within-45-seconds-perhaps-the-main-thread-has-not-gone
 */

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @Rule
    @JvmField
    val mainActivityRule = ActivityTestRule(MainActivity::class.java)

    @Rule
    @JvmField
    val detailActivityRule = ActivityTestRule(DetailActivity::class.java)

    @Before
    fun setUp() {
        ActivityScenario.launch(MainActivity::class.java)
        IdlingRegistry.getInstance().register(EspressoIdlingResource.espressoTestIdlingResource)
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.espressoTestIdlingResource)
    }

    @Test
    fun loadMovies() {
        val fetch = FetchConfig().getFetchService().getPopularMovies().execute()
        val movies = fetch.body()
        val isSuccess = fetch.isSuccessful

        if (movies != null && isSuccess) {
            val movieRecyclerView = onView(withId(R.id.rvMovie))
            movieRecyclerView.check(matches(isDisplayed()))

            if (movies.results.isNotEmpty())
                movieRecyclerView.perform(
                    RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(
                        movies.results.size
                    )
                )
        } else {
            val noDataText = onView(withId(R.id.noDataText))
            noDataText.check(matches(isDisplayed()))
        }
    }

    @Test
    fun loadMovieDetail() {
        val position = 0

        val rvMovie = mainActivityRule.activity.findViewById<RecyclerView>(R.id.rvMovie)
        val rvMovieItems = rvMovie.adapter?.itemCount

        if (rvMovieItems != null && rvMovieItems > position) {

            val itemView = rvMovie.findViewHolderForAdapterPosition(position)
            val rvItemId =
                itemView?.itemView?.findViewById<TextView>(R.id.tvItemId)?.text.toString()

            val movieRecyclerView = onView(withId(R.id.rvMovie))
            movieRecyclerView.perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    position,
                    click()
                )
            )

            val fetch = FetchConfig().getFetchService().getMovieDetail(rvItemId).execute()
            val movie = fetch.body()
            val isSuccess = fetch.isSuccessful

            if (isSuccess) {

                val mainPage = onView(withId(R.id.mainPage))
                mainPage.check(matches(isDisplayed()))

                val detailContentView = onView(withId(R.id.detailContent))
                detailContentView.check(matches(isDisplayed()))

                val movieTitle = onView(withId(R.id.movieTitle))
                movieTitle.check(matches(withText(movie?.title)))

                val movieOverview = onView(withId(R.id.movieOverview))
                movieOverview.check(matches(withText(movie?.overview)))

                val movieYear = onView(withId(R.id.movieYear))
                movieYear.check(matches(withText("(${movie?.releaseDate})")))

                val movieDuration = onView(withId(R.id.movieDuration))
                movieDuration.check(matches(withText("${movie?.runtime} time")))

                val movieGenre = onView(withId(R.id.movieGenre))
                movieGenre.check(matches(withText((movie?.genres?.joinToString { it.name }))))

                val movieTotalVote = onView(withId(R.id.movieOriginalTitle))
                movieTotalVote.check(matches(withText(movie?.originalTitle)))

                val movieTagLine = onView(withId(R.id.movieTagLine))
                movieTagLine.check(matches(withText(movie?.tagline)))

                val movieProductionCountries = onView(withId(R.id.movieProductionCountries))
                movieProductionCountries.check(matches(withText(movie?.productionCountries?.joinToString { it.name })))

                val movieStatus = onView(withId(R.id.movieStatus))
                movieStatus.check(matches(withText(movie?.status)))

                val movieLanguage = onView(withId(R.id.movieLanguage))
                movieLanguage.check(matches(withText(movie?.spokenLanguages?.joinToString { it.name })))

                val activityDetail = onView(withId(R.id.activityDetail))
                activityDetail.perform(swipeUp())

                val castRecyclerView = onView(withId(R.id.rvCompanies))
                castRecyclerView.check(matches(isDisplayed()))

                val rvCompanies =
                    detailActivityRule.activity.findViewById<RecyclerView>(R.id.rvCompanies)
                val rvCompaniesItems = rvCompanies.adapter?.itemCount
                if (rvCompaniesItems != null && rvCompaniesItems > 0)
                    castRecyclerView.perform(
                        RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(
                            rvCompaniesItems
                        )
                    )
            }
        }
    }

    @Test
    fun loadShows() {
        val tvShowBtn = onView(withId(R.id.navigation_show))
        tvShowBtn.perform(click())

        val fetch = FetchConfig().getFetchService().getPopularTvShows().execute()
        val shows = fetch.body()
        val isSuccess = fetch.isSuccessful

        if (isSuccess && shows != null) {
            val movieRecyclerView = onView(withId(R.id.rvShow))
            movieRecyclerView.check(matches(isDisplayed()))

            if (shows.results.isNotEmpty())
                movieRecyclerView.perform(
                    RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(
                        shows.results.size
                    )
                )
        } else {
            val noDataText = onView(withId(R.id.noDataText))
            noDataText.check(matches(isDisplayed()))
        }
    }


    @Test
    fun loadShowDetail() {
        val position = 0

        val tvShowBtn = onView(withId(R.id.navigation_show))
        tvShowBtn.perform(click())

        val rvShow = mainActivityRule.activity.findViewById<RecyclerView>(R.id.rvShow)
        val rvShowItems = rvShow.adapter?.itemCount

        if (rvShowItems != null && rvShowItems > position) {

            val itemView = rvShow.findViewHolderForAdapterPosition(position)
            val rvItemId =
                itemView?.itemView?.findViewById<TextView>(R.id.tvItemId)?.text.toString()

            val movieRecyclerView = onView(withId(R.id.rvShow))
            movieRecyclerView.perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    position,
                    click()
                )
            )

            val fetch = FetchConfig().getFetchService().getTvShowDetail(rvItemId).execute()
            val show = fetch.body()
            val isSuccess = fetch.isSuccessful

            if (isSuccess) {
                val mainPage = onView(withId(R.id.mainPage))
                mainPage.check(matches(isDisplayed()))

                val detailContentView = onView(withId(R.id.detailContent))
                detailContentView.check(matches(isDisplayed()))

                val movieTitle = onView(withId(R.id.movieTitle))
                movieTitle.check(matches(withText(show?.name)))

                val movieYear = onView(withId(R.id.movieYear))
                movieYear.check(matches(withText("(${show?.lastAirDate})")))

                val movieDuration = onView(withId(R.id.movieDuration))
                movieDuration.check(matches(withText("${show?.episodeRunTime?.size} total hours")))

                val movieGenre = onView(withId(R.id.movieGenre))
                movieGenre.check(matches(withText((show?.genres?.joinToString { it.name }))))

                val movieTotalVote = onView(withId(R.id.movieOriginalTitle))
                movieTotalVote.check(matches(withText(show?.originalName)))

                val movieTagLine = onView(withId(R.id.movieTagLine))
                movieTagLine.check(matches(withText(show?.tagline)))

                val movieOverview = onView(withId(R.id.movieOverview))
                movieOverview.check(matches(withText(show?.overview)))

                val movieProductionCountries = onView(withId(R.id.movieProductionCountries))
                movieProductionCountries.check(matches(withText(show?.productionCountries?.joinToString { it.name })))

                val movieStatus = onView(withId(R.id.movieStatus))
                movieStatus.check(matches(withText(show?.status)))

                val movieLanguage = onView(withId(R.id.movieLanguage))
                movieLanguage.check(matches(withText(show?.spokenLanguages?.joinToString { it.name })))

                val activityDetail = onView(withId(R.id.activityDetail))
                activityDetail.perform(swipeUp())

                val castRecyclerView = onView(withId(R.id.rvCompanies))
                castRecyclerView.check(matches(isDisplayed()))

                val rvCompanies =
                    detailActivityRule.activity.findViewById<RecyclerView>(R.id.rvCompanies)
                val rvCompaniesItems = rvCompanies.adapter?.itemCount
                if (rvCompaniesItems != null && rvCompaniesItems > 0)
                    castRecyclerView.perform(
                        RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(
                            rvCompaniesItems
                        )
                    )
            }
        }
    }

    @Test
    fun loadBookmarkedMovies() {
        val bookmarkBtn = onView(withId(R.id.navigation_bookmarked))
        bookmarkBtn.perform(click())

        val movieFragment = onView(withId(R.id.movieFragment))
        movieFragment.check(matches(isDisplayed()))
    }

    @Test
    fun loadBookmarkedShows() {
        val bookmarkBtn = onView(withId(R.id.navigation_bookmarked))
        bookmarkBtn.perform(click())

        val tvShowTab = onView(withText("TV Show"))
        tvShowTab.perform(click())

        val showFragment = onView(withId(R.id.showFragment))
        showFragment.check(matches(isDisplayed()))
    }

    @Test
    fun setMovieBookmark() {
        val position = 0

        val bookmarkBtn = onView(withId(R.id.navigation_bookmarked))
        bookmarkBtn.perform(click())

        val movieFragment = onView(withId(R.id.movieFragment))
        movieFragment.check(matches(isDisplayed()))

        val moviesBtn = onView(withId(R.id.navigation_movie))
        moviesBtn.perform(click())

        val rvMovie = mainActivityRule.activity.findViewById<RecyclerView>(R.id.rvMovie)
        val rvMovieItems = rvMovie.adapter?.itemCount

        if (rvMovieItems != null && rvMovieItems > position) {

            val itemView = rvMovie.findViewHolderForAdapterPosition(position)
            val rvItemId =
                itemView?.itemView?.findViewById<TextView>(R.id.tvItemId)?.text.toString()

            val movieRecyclerView = onView(withId(R.id.rvMovie))
            movieRecyclerView.perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    position,
                    click()
                )
            )

            val fetch = FetchConfig().getFetchService().getMovieDetail(rvItemId).execute()
            val isSuccess = fetch.isSuccessful

            if (isSuccess) {

                val mainPage = onView(withId(R.id.mainPage))
                mainPage.check(matches(isDisplayed()))

                val imgPoster = onView(withId(R.id.imgPoster))
                imgPoster.perform(swipeUp())

                val bookmarkButton = onView(withId(R.id.bookmarkButton))
                bookmarkButton.perform(click())

                val root = onView(isRoot())
                root.perform(pressBack())

                bookmarkBtn.perform(click())
                movieFragment.check(matches(isDisplayed()))
            }
        }
    }

    @Test
    fun setShowBookmark() {
        val position = 0

        val bookmarkBtn = onView(withId(R.id.navigation_bookmarked))
        bookmarkBtn.perform(click())

        val tvShowTab = onView(withText("TV Show"))
        tvShowTab.perform(click())

        val showFragment = onView(withId(R.id.showFragment))
        showFragment.check(matches(isDisplayed()))

        val tvShowBtn = onView(withId(R.id.navigation_show))
        tvShowBtn.perform(click())

        val rvShow = mainActivityRule.activity.findViewById<RecyclerView>(R.id.rvShow)
        val rvShowItems = rvShow.adapter?.itemCount

        if (rvShowItems != null && rvShowItems > position) {

            val itemView = rvShow.findViewHolderForAdapterPosition(position)
            val rvItemId =
                itemView?.itemView?.findViewById<TextView>(R.id.tvItemId)?.text.toString()

            val movieRecyclerView = onView(withId(R.id.rvShow))
            movieRecyclerView.perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    position,
                    click()
                )
            )

            val fetch = FetchConfig().getFetchService().getTvShowDetail(rvItemId).execute()
            val isSuccess = fetch.isSuccessful

            if (isSuccess) {
                val mainPage = onView(withId(R.id.mainPage))
                mainPage.check(matches(isDisplayed()))

                val imgPoster = onView(withId(R.id.imgPoster))
                imgPoster.perform(swipeUp())

                val bookmarkButton = onView(withId(R.id.bookmarkButton))
                bookmarkButton.perform(click())

                val root = onView(isRoot())
                root.perform(pressBack())

                bookmarkBtn.perform(click())
                tvShowTab.perform(click())
                showFragment.check(matches(isDisplayed()))
            }
        }
    }

    @Test
    fun useMoviesNavbar() {

        val searchBar = onView(withId(R.id.movieSearchBar))
        searchBar.perform(typeText("Wonder Woman"), closeSoftKeyboard())

        val searchButton = onView(withId(R.id.movieSearchButton))
        searchButton.perform(click())

        val sortButton = onView(withId(R.id.movieSortButton))
        sortButton.perform(click())
        sortButton.perform(click())
    }

    @Test
    fun useShowsNavbar() {

        val tvShowBtn = onView(withId(R.id.navigation_show))
        tvShowBtn.perform(click())

        val searchBar = onView(withId(R.id.showSearchBar))
        searchBar.perform(typeText("Wonder Woman"), closeSoftKeyboard())

        val searchButton = onView(withId(R.id.showSearchButton))
        searchButton.perform(click())

        val sortButton = onView(withId(R.id.showSortButton))
        sortButton.perform(click())
        sortButton.perform(click())
    }

    @Test
    fun useBookmarkedMoviesNavbar() {
        val bookmarkBtn = onView(withId(R.id.navigation_bookmarked))
        bookmarkBtn.perform(click())

        val searchBar = onView(withId(R.id.movieSearchBar))
        searchBar.perform(typeText("Wonder Woman"), closeSoftKeyboard())

        val searchButton = onView(withId(R.id.movieSearchButton))
        searchButton.perform(click())

        val sortButton = onView(withId(R.id.movieSortButton))
        sortButton.perform(click())
        sortButton.perform(click())
    }

    @Test
    fun useBookmarkedShowsNavbar() {
        val bookmarkBtn = onView(withId(R.id.navigation_bookmarked))
        bookmarkBtn.perform(click())

        val tvShowTab = onView(withText("TV Show"))
        tvShowTab.perform(click())

        val searchBar = onView(withId(R.id.showSearchBar))
        searchBar.perform(typeText("Wonder Woman"), closeSoftKeyboard())

        val searchButton = onView(withId(R.id.showSearchButton))
        searchButton.perform(click())

        val sortButton = onView(withId(R.id.showSortButton))
        sortButton.perform(click())
        sortButton.perform(click())
    }

}
