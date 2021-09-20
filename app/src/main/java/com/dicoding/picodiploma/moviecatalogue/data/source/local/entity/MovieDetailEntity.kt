package com.dicoding.picodiploma.moviecatalogue.data.source.local.entity

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "moviedetailentities")
data class MovieDetailEntity(

    @PrimaryKey
    @NonNull
    val id: Int,
    val title: String,
    val overview: String,
    val posterPath: String,
    val releaseDate: String,
    val duration: Int,
    val genres: String,
    val originalTitle: String,
    val tagline: String,
    val productionCountries: String,
    val status: String,
    val spokenLanguages: String,
    var bookmarked: Boolean = false,
)
