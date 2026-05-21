package com.example.rma1.movies.db.entities

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class MovieWithGenres (
    @Embedded val movie: MovieEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = MovieGenreCrossRef::class,
            parentColumn = "movieId",
            entityColumn = "genreId",
        ),
    )
    val genres: List<GenreEntity>,
    @Relation(
        parentColumn = "id",
        entityColumn = "movieId",
    )
    val imagePaths: List<ImagePathEntity>
)