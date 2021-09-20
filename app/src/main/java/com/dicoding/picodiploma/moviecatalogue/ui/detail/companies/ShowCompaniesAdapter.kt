package com.dicoding.picodiploma.moviecatalogue.ui.detail.companies

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dicoding.picodiploma.moviecatalogue.BuildConfig
import com.dicoding.picodiploma.moviecatalogue.R
import com.dicoding.picodiploma.moviecatalogue.data.source.local.entity.ShowCompanyEntity
import com.dicoding.picodiploma.moviecatalogue.databinding.ItemCastBinding

class ShowCompaniesAdapter(private val itemCallback: CompaniesItemCallback) :
    RecyclerView.Adapter<ShowCompaniesAdapter.ViewHolder>() {

    private var listCompanies = ArrayList<ShowCompanyEntity>()

    fun setCompanies(casts: List<ShowCompanyEntity>?) {
        if (casts.isNullOrEmpty()) return
        listCompanies.clear()
        listCompanies.addAll(casts)
    }

    inner class ViewHolder(private val binding: ItemCastBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(company: ShowCompanyEntity) {
            with(binding) {
                tvItemName.text = company.name
                Glide.with(itemView.context)
                    .load("${BuildConfig.TMBDB_W500_IMAGE_URL}${company.logoPath}")
                    .apply(RequestOptions.placeholderOf(R.drawable.ic_loading))
                    .error(R.drawable.ic_error)
                    .into(imgProfile)

                itemView.setOnClickListener { itemCallback.onShowCompanyClick(company) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemMovieBinding =
            ItemCastBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder((itemMovieBinding))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val company = listCompanies[position]
        holder.bind(company)
    }

    override fun getItemCount(): Int = listCompanies.size
}