package com.dicoding.picodiploma.moviecatalogue.ui.show

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
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.picodiploma.moviecatalogue.R
import com.dicoding.picodiploma.moviecatalogue.data.source.local.entity.ShowEntity
import com.dicoding.picodiploma.moviecatalogue.databinding.FragmentShowBinding
import com.dicoding.picodiploma.moviecatalogue.ui.detail.DetailActivity
import com.dicoding.picodiploma.moviecatalogue.utils.SqlUtils
import com.dicoding.picodiploma.moviecatalogue.viewmodel.ViewModelFactory
import com.dicoding.picodiploma.moviecatalogue.vo.Resource
import com.dicoding.picodiploma.moviecatalogue.vo.Status.*

class ShowFragment : Fragment() {

    companion object {
        const val FROM_SHOW = "from_show"
    }

    private lateinit var viewBinding: FragmentShowBinding
    private lateinit var viewModel: ShowViewModel
    private lateinit var recyclerViewAdapter: ShowAdapter
    private var descSorted = false
    private var currentSorted = SqlUtils.OLDEST
    private var currentSearched = ""

    private val showsObserver = Observer<Resource<PagedList<ShowEntity>?>> { shows ->
        if (shows != null) {
            when (shows.status) {
                SUCCESS -> {
                    recyclerViewAdapter.submitList(shows.data)
                    loadSearchBar()
                    loadSortButton()
                    viewBinding.progressBar.visibility = View.GONE
                    if (shows.data.isNullOrEmpty()) {
                        viewBinding.noDataText.visibility = View.VISIBLE
                        viewBinding.rvShow.visibility = View.GONE
                        viewBinding.noDataText.text =
                            context?.resources?.getString(R.string.no_data_available)
                    } else {
                        viewBinding.noDataText.visibility = View.GONE
                        viewBinding.rvShow.visibility = View.VISIBLE
                    }
                }
                LOADING -> {
                    viewBinding.noDataText.visibility = View.GONE
                    viewBinding.rvShow.visibility = View.GONE
                    viewBinding.progressBar.visibility = View.VISIBLE
                }
                ERROR -> {
                    viewBinding.progressBar.visibility = View.GONE
                    viewBinding.rvShow.visibility = View.GONE
                    viewBinding.noDataText.visibility = View.VISIBLE
                    viewBinding.noDataText.text =
                        context?.resources?.getString(R.string.server_error)
                }
            }
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
                        R.drawable.ic_baseline_arrow_downward_24,
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

    private fun observe(search: String, sort: String, bookmarked: Boolean = false) {
        viewModel.getShows(SqlUtils.getShowsQuery(search, bookmarked, sort))
            .observe(requireActivity(), showsObserver)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val viewModelFactory = ViewModelFactory.getInstance(requireContext())
        viewModel = ViewModelProvider(
            this,
            viewModelFactory
        )[ShowViewModel::class.java]
        viewBinding = FragmentShowBinding.inflate(layoutInflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerViewAdapter = ShowAdapter()
        observe(currentSearched, currentSorted)

        recyclerViewAdapter.setOnItemClickCallback(object : ShowAdapter.OnItemClickCallback {
            override fun onItemClicked(show: ShowEntity) {
                val intent = Intent(context, DetailActivity::class.java).apply {
                    putExtra(DetailActivity.EXTRA_MOVIE_ID, show.id)
                    putExtra(DetailActivity.EXTRA_FROM, FROM_SHOW)
                }
                startActivity(intent)
            }
        })

        with(viewBinding.rvShow) {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = recyclerViewAdapter
        }
    }
}