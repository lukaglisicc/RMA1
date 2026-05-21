package com.example.rma1.movies.db.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "movies_images",
    foreignKeys = [
        ForeignKey(
            entity = MovieEntity::class,
            parentColumns = ["id"],
            childColumns = ["movieId"],
            onDelete = ForeignKey.CASCADE,
        )
    ],
    indices = [Index("movieId")]
)
data class ImagePathEntity (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val movieId: String,
    val path: String,
)