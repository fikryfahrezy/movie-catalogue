package com.dicoding.picodiploma.moviecatalogue.data.source.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class MovieDetailWithCompany(

    @Embedded
    val movieDetailEntity: MovieDetailEntity,

    @Relation(parentColumn = "id", entityColumn = "movieDetailId")
    val companies: List<MovieCompanyEntity>
)
