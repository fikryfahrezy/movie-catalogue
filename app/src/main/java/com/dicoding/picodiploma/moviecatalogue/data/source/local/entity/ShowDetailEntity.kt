package com.dicoding.picodiploma.moviecatalogue.data.source.local.entity

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "showdetailentities")
data class ShowDetailEntity(

    @PrimaryKey
    @NonNull
    var id: Int,
    val name: String,
    val overview: String,
    val posterPath: String,
    val firstAirDate: String,
    val lastAirDate: String,
    val duration: Int,
    val genres: String,
    val originalName: String,
    val tagline: String,
    val productionCountries: String,
    val status: String,
    val spokenLanguages: String,
    var bookmarked: Boolean = false,
)
