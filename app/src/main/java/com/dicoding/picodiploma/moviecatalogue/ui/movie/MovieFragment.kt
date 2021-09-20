package com.dicoding.picodiploma.moviecatalogue.ui.movie

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.picodiploma.moviecatalogue.R
import com.dicoding.picodiploma.moviecatalogue.data.source.local.entity.MovieEntity
import com.dicoding.picodiploma.moviecatalogue.databinding.FragmentMovieBinding
import com.dicoding.picodiploma.moviecatalogue.utils.SqlUtils
import com.dicoding.picodiploma.moviecatalogue.viewmodel.ViewModelFactory
import com.dicoding.picodiploma.moviecatalogue.vo.Resource
import com.dicoding.picodiploma.moviecatalogue.vo.Status.*

class MovieFragment : Fragment() {

    companion object {
        const val FROM_MOVIE = "from_movie"
    }

    private lateinit var viewModel: MovieViewModel
    private lateinit var viewBinding: FragmentMovieBinding
    private lateinit var recyclerViewAdapter: MovieAdapter
    private var descSorted = false
    private var currentSorted = SqlUtils.OLDEST
    private var currentSearched = ""

    private val movieObserver = Observer<Resource<PagedList<MovieEntity>?>> { movies ->
        if (movies != null) {
            when (movies.status) {
                SUCCESS -> {
                    recyclerViewAdapter.submitList(movies.data)
                    loadSearchBar()
                    loadSortButton()
                    viewBinding.progressBar.visibility = View.GONE
                    if (movies.data.isNullOrEmpty()) {
                        viewBinding.noDataText.visibility = View.VISIBLE
                        viewBinding.rvMovie.visibility = View.GONE
                        viewBinding.noDataText.text =
                            context?.resources?.getString(R.string.no_data_available)
                    } else {
                        viewBinding.noDataText.visibility = View.GONE
                        viewBinding.rvMovie.visibility = View.VISIBLE
                    }
                }
                LOADING -> {
                    viewBinding.noDataText.visibility = View.GONE
                    viewBinding.rvMovie.visibility = View.GONE
                    viewBinding.progressBar.visibility = View.VISIBLE
                }
                ERROR -> {
                    viewBinding.progressBar.visibility = View.GONE
                    viewBinding.rvMovie.visibility = View.GONE
                    viewBinding.noDataText.visibility = View.VISIBLE
                    viewBinding.noDataText.text =
                        context?.resources?.getString(R.string.server_error)
                }
            }
        }
    }

    private fun loadSearchBar() {
        viewBinding.movieSearchButton.setOnClickListener {
            val word = viewBinding.movieSearchBar.text.toString()
            currentSearched = word
            observe(currentSearched, currentSorted)
        }
    }

    private fun loadSortButton() {
        val sortButton = viewBinding.movieSortButton
        sortButton.setOnClickListener {
            if (descSorted) {
                descSorted = false
                currentSorted = SqlUtils.OLDEST
                observe(currentSearched, currentSorted)
                sortButton.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_baseline_arrow_downward_24
                    )
                )
            } else {
                descSorted = true
                currentSorted = SqlUtils.NEWEST
                observe(currentSearched, currentSorted)
                sortButton.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_baseline_arrow_upward_24
                    )
                )
            }
        }
    }

    private fun observe(search: String, sort: String, bookmarked: Boolean = false) {
        viewModel.getMovies(SqlUtils.getMoviesQuery(search, bookmarked, sort))
            .observe(requireActivity(), movieObserver)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val viewModelFactory = ViewModelFactory.getInstance(requireContext())
        viewModel =
            ViewModelProvider(this, viewModelFactory)[MovieViewModel::class.java]
        viewBinding = FragmentMovieBinding.inflate(layoutInflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerViewAdapter = MovieAdapter()
        observe(currentSearched, currentSorted)

        with(viewBinding.rvMovie) {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = recyclerViewAdapter
        }
    }
}