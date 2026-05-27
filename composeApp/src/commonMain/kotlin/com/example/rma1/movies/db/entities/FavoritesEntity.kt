package com.example.rma1.movies.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoritesEntity (
    @PrimaryKey
    val id: String,
)
