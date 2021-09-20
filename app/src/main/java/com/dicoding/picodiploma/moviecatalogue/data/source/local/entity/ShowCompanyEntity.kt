package com.dicoding.picodiploma.moviecatalogue.data.source.local.entity

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "showcompanyentities",
    primaryKeys = ["id"],
    foreignKeys = [
        ForeignKey(
            entity = ShowDetailEntity::class,
            parentColumns = ["id"],
            childColumns = ["showDetailId"]
        )
    ],
    indices = [
        Index(value = ["id"]),
        Index(value = ["showDetailId"]),
    ]
)
data class ShowCompanyEntity(

    @NonNull
    val id: Int,
    val showDetailId: Int,
    val name: String,
    val logoPath: String?,
    val originCountry: String
)