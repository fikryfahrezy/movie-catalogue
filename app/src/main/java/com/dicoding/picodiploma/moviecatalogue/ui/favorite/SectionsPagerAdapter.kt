package com.dicoding.picodiploma.moviecatalogue.ui.favorite

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.dicoding.picodiploma.moviecatalogue.R
import com.dicoding.picodiploma.moviecatalogue.ui.favorite.tab.movie.FavoriteMovieFragment
import com.dicoding.picodiploma.moviecatalogue.ui.favorite.tab.show.FavoriteShowFragment

class SectionsPagerAdapter(private val context: Context, fm: FragmentManager) :
    FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private val TAB_TITLES = arrayOf(
        R.string.title_movie,
        R.string.title_show
    )

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> FavoriteMovieFragment()
            1 -> FavoriteShowFragment()
            else -> Fragment()
        }
    }

    override fun getPageTitle(position: Int): CharSequence =
        context.resources.getString(TAB_TITLES[position])

    override fun getCount(): Int = TAB_TITLES.size

    override fun getItemPosition(`object`: Any): Int = POSITION_NONE
}