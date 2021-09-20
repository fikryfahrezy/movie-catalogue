package com.dicoding.picodiploma.moviecatalogue.ui.favorite.tab.show

import android.content.Intent
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
import com.dicoding.picodiploma.moviecatalogue.data.source.local.entity.ShowEntity
import com.dicoding.picodiploma.moviecatalogue.databinding.FragmentShowBinding
import com.dicoding.picodiploma.moviecatalogue.ui.detail.DetailActivity
import com.dicoding.picodiploma.moviecatalogue.utils.SqlUtils
import com.dicoding.picodiploma.moviecatalogue.viewmodel.ViewModelFactory
import com.google.android.material.snackbar.Snackbar

class FavoriteShowFragment : Fragment() {

    companion object {
        const val FROM_SHOW = "from_show"
    }

    private lateinit var viewBinding: FragmentShowBinding
    private lateinit var viewModelFavorite: FavoriteShowViewModel
    private lateinit var recyclerViewAdapter: FavoriteShowAdapter
    private var descSorted = false
    private var currentSorted = SqlUtils.OLDEST
    private var currentSearched = ""

    private val showsObserver = Observer<PagedList<ShowEntity?>> { shows ->
        if (shows != null) {
            recyclerViewAdapter.submitList(shows)
            loadSearchBar()
            loadSortButton()
            viewBinding.progressBar.visibility = View.GONE
            if (shows.isNullOrEmpty()) {
                viewBinding.noDataText.visibility = View.VISIBLE
                viewBinding.rvShow.visibility = View.GONE
                viewBinding.noDataText.text =
                    context?.resources?.getString(R.string.no_data_available)
            } else {
                viewBinding.noDataText.visibility = View.GONE
                viewBinding.rvShow.visibility = View.VISIBLE
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
                val showEntity = recyclerViewAdapter.getSwipedData(swipedPosition)
                setBookmark(showEntity, 0)

                val snackBar =
                    Snackbar.make(view as View, R.string.message_undo, Snackbar.LENGTH_LONG)
                snackBar.setAction(R.string.message_ok) {
                    setBookmark(showEntity)
                }
                snackBar.show()
            }
        }
    })

    private fun setBookmark(showEntity: ShowEntity?, newState: Int = 1) {
        if (showEntity != null) {
            viewModelFavorite.setBookmarkShowDetail(showEntity.id, newState)
            viewModelFavorite.setBookmarkShow(showEntity.id, newState)
        }
    }

    private fun loadSearchBar() {
        viewBinding.showSearchButton.setOnClickListener {
            val word = viewBinding.showSearchBar.text.toString()
            currentSearched = word
            observe(currentSearched, currentSorted)
        }
    }

    private fun loadSortButton() {
        val sortButton = viewBinding.showSortButton
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

    private fun observe(search: String, sort: String, bookmarked: Boolean = true) {
        viewModelFavorite.getBookmarkedShows(SqlUtils.getShowsQuery(search, bookmarked, sort))
            .observe(requireActivity(), showsObserver)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val viewModelFactory = ViewModelFactory.getInstance(requireContext())
        viewModelFavorite = ViewModelProvider(
            this,
            viewModelFactory
        )[FavoriteShowViewModel::class.java]
        viewBinding = FragmentShowBinding.inflate(layoutInflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        itemTouchHelper.attachToRecyclerView(viewBinding.rvShow)

        recyclerViewAdapter = FavoriteShowAdapter()
        observe(currentSearched, currentSorted)

        recyclerViewAdapter.setOnItemClickCallback(
            object :
                FavoriteShowAdapter.OnItemClickCallback {
                override fun onItemClicked(show: ShowEntity) {
                    val intent = Intent(context, DetailActivity::class.java).apply {
                        putExtra(DetailActivity.EXTRA_MOVIE_ID, show.id)
                        putExtra(DetailActivity.EXTRA_FROM, FROM_SHOW)
                    }
                    startActivity(intent)
                }
            })

        with(viewBinding.rvShow)
        {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = recyclerViewAdapter
        }
    }
}