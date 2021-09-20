package com.dicoding.picodiploma.moviecatalogue.data.source.local.entity

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "moviecompanyentities",
    primaryKeys = ["id"],
    foreignKeys = [
        ForeignKey(
            entity = MovieDetailEntity::class,
            parentColumns = ["id"],
            childColumns = ["movieDetailId"]
        )
    ],
    indices = [
        Index(value = ["id"]),
        Index(value = ["movieDetailId"]),
    ]
)
data class MovieCompanyEntity(

    @NonNull
    val id: Int,
    val movieDetailId: Int,
    val name: String,
    val logoPath: String?,
    val originCountry: String
)