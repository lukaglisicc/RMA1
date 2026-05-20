package com.example.rma1.movies.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movies")
data class MovieEntity (
    @PrimaryKey
    val id: String,
    val title: String,
    val year: Int,
    val rating: Float,
    val votes: Int,
    val posterPath: String,
)