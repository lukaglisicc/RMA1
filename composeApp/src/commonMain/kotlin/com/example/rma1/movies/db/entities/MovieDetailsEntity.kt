package com.example.rma1.movies.db.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "movie_details",
    foreignKeys = [
        ForeignKey(
            entity = MovieEntity::class,
            parentColumns = ["id"],
            childColumns = ["movieId"],
            onDelete = ForeignKey.CASCADE,
        )
    ]
)
data class MovieDetailsEntity (
    @PrimaryKey val movieId: String,
    val title: String,
    val year: Int,
    val desc: String,
    val budget: Int,
    val revenue: Int,
    val languageCode: String,
    val popularity: Float,
    val imdbRating: Float,
    val posterPath: String,
    val backdropPath: String,
    val trailerPath: String,
)
