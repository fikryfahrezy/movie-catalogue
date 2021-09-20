package com.dicoding.picodiploma.moviecatalogue.ui.detail.companies

import com.dicoding.picodiploma.moviecatalogue.data.source.local.entity.MovieCompanyEntity
import com.dicoding.picodiploma.moviecatalogue.data.source.local.entity.ShowCompanyEntity

interface CompaniesItemCallback {
    fun onShowCompanyClick(company: ShowCompanyEntity)
    fun onMovieCompanyClick(company: MovieCompanyEntity)
}