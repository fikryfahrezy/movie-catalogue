package com.dicoding.picodiploma.moviecatalogue.data.source.local.entity

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "showsentities")
data class ShowEntity(

    @PrimaryKey
    @NonNull
    val id: Int,
    val name: String,
    val overview: String,
    val posterPath: String,
    val firstAirDate: String,
    var bookmarked: Boolean = false
)
