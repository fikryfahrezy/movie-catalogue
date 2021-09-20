package com.dicoding.picodiploma.moviecatalogue.data.source.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class ShowDetailWithCompany(

    @Embedded
    val showDetailEntity: ShowDetailEntity,

    @Relation(parentColumn = "id", entityColumn = "showDetailId")
    val companies: List<ShowCompanyEntity>,
)
