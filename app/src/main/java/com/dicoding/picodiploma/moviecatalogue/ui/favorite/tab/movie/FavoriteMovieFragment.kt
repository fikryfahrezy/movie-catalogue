package com.dicoding.picodiploma.moviecatalogue.ui.favorite.tab.movie

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagedList
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.picodiploma.moviecatalogue.R
import com.dicoding.picodiploma.moviecatalogue.data.source.local.entity.MovieEntity
import com.dicoding.picodiploma.moviecatalogue.databinding.FragmentMovieBinding
import com.dicoding.picodiploma.moviecatalogue.utils.SqlUtils
import com.dicoding.picodiploma.moviecatalogue.viewmodel.ViewModelFactory
import com.google.android.material.snackbar.Snackbar

class FavoriteMovieFragment : Fragment() {

    companion object {
        const val FROM_MOVIE = "from_movie"
    }

    private lateinit var viewModelFavorite: FavoriteMovieViewModel
    private lateinit var viewBinding: FragmentMovieBinding
    private lateinit var recyclerViewAdapter: FavoriteMovieAdapter
    private var descSorted = false
    private var currentSorted = SqlUtils.OLDEST
    private var currentSearched = ""

    private val moviesObserver = Observer<PagedList<MovieEntity?>> { movies ->
        if (movies != null) {
            recyclerViewAdapter.submitList(movies)
            loadSearchBar()
            loadSortButton()
            viewBinding.progressBar.visibility = View.GONE
            if (movies.isNullOrEmpty()) {
                viewBinding.noDataText.visibility = View.VISIBLE
                viewBinding.rvMovie.visibility = View.GONE
                viewBinding.noDataText.text =
                    context?.resources?.getString(R.string.no_data_available)
            } else {
                viewBinding.noDataText.visibility = View.GONE
                viewBinding.rvMovie.visibility = View.VISIBLE
            }
        }
    }

    private val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.Callback() {
        override fun getMovementFlags(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder
        ): Int = makeMovementFlags(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT)

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean = true

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            if (view != null) {
                val swipedPosition = viewHolder.adapterPosition
                val movieEntity = recyclerViewAdapter.getSwipedData(swipedPosition)
                setBookmark(movieEntity, 0)

                val snackBar =
                    Snackbar.make(view as View, R.string.message_undo, Snackbar.LENGTH_LONG)
                snackBar.setAction(R.string.message_ok) {
                    setBookmark(movieEntity)
                }
                snackBar.show()
            }
        }
    })

    private fun setBookmark(movieEntity: MovieEntity?, newState: Int = 1) {
        if (movieEntity != null) {
            viewModelFavorite.setBookmarkMovie(movieEntity.id, newState)
            viewModelFavorite.setBookmarkMovieDetail(movieEntity.id, newState)
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
                        R.drawable.ic_baseline_arrow_upward_24,
                    )
                )
            }
        }
    }

    private fun observe(search: String, sort: String, bookmarked: Boolean = true) {
        viewModelFavorite.getBookmarkedMovies(SqlUtils.getMoviesQuery(search, bookmarked, sort))
            .observe(requireActivity(), moviesObserver)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val viewModelFactory = ViewModelFactory.getInstance(requireContext())
        viewModelFavorite =
            ViewModelProvider(this, viewModelFactory)[FavoriteMovieViewModel::class.java]
        viewBinding = FragmentMovieBinding.inflate(layoutInflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        itemTouchHelper.attachToRecyclerView(viewBinding.rvMovie)

        recyclerViewAdapter = FavoriteMovieAdapter()
        observe(currentSearched, currentSorted)

        with(viewBinding.rvMovie)
        {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = recyclerViewAdapter
        }
    }
}