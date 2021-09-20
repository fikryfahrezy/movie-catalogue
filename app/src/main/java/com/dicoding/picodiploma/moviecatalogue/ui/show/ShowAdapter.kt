package com.dicoding.picodiploma.moviecatalogue.ui.show

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dicoding.picodiploma.moviecatalogue.BuildConfig
import com.dicoding.picodiploma.moviecatalogue.R
import com.dicoding.picodiploma.moviecatalogue.data.source.local.entity.ShowEntity
import com.dicoding.picodiploma.moviecatalogue.databinding.ItemMovieBinding

class ShowAdapter : PagedListAdapter<ShowEntity, ShowAdapter.ViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ShowEntity>() {
            override fun areItemsTheSame(oldItem: ShowEntity, newItem: ShowEntity) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: ShowEntity, newItem: ShowEntity) =
                oldItem == newItem
        }
    }

    private var onItemClickCallback: OnItemClickCallback? = null

    interface OnItemClickCallback {
        fun onItemClicked(show: ShowEntity)
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    inner class ViewHolder(private val binding: ItemMovieBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(show: ShowEntity) {
            with(binding) {
                tvItemId.text = show.id.toString()
                tvItemTitle.text = show.name
                tvItemDate.text = show.firstAirDate
                tvItemOverview.text = show.overview
                Glide.with(itemView.context)
                    .load("${BuildConfig.TMBDB_W500_IMAGE_URL}${show.posterPath}")
                    .apply(RequestOptions.placeholderOf(R.drawable.ic_loading))
                    .error(R.drawable.ic_error)
                    .into(imgPoster)

                itemView.setOnClickListener { onItemClickCallback?.onItemClicked(show) }
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemMovieBinding =
            ItemMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder((itemMovieBinding))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val show = getItem(position)
        if (show != null)
            holder.bind(show)
    }
}