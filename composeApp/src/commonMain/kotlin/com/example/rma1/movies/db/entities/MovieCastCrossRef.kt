package com.example.rma1.movies.db.entities

import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "movies_cast",
    primaryKeys = ["movieId", "castId"],
    indices = [Index("movieId"), Index("castId")]
)
data class MovieCastCrossRef(
    val movieId: String,
    val castId: String,
)