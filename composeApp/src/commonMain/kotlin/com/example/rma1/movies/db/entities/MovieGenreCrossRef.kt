package com.example.rma1.movies.db.entities

import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "movies_genres",
    primaryKeys = ["movieId", "genreId"],
    indices = [Index("movieId"), Index("genreId")]
)
data class MovieGenreCrossRef (
    val movieId: String,
    val genreId: Int,
)