package com.example.rma1.movies.db.entities

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation


data class MovieDetailsFull (
    @Embedded val movieDetails: MovieDetailsEntity,
    @Relation(
        parentColumn = "movieId",
        entityColumn = "movieId",
    )
    val imagePaths: List<ImagePathEntity>,
    @Relation(
        parentColumn = "movieId",
        entityColumn = "id",
        associateBy = Junction(
            value = MovieGenreCrossRef::class,
            parentColumn = "movieId",
            entityColumn = "genreId",
        ),
    )
    val genres: List<GenreEntity>,
    @Relation(
        parentColumn = "movieId",
        entityColumn = "id",
        associateBy = Junction(
            value = MovieCastCrossRef::class,
            parentColumn = "movieId",
            entityColumn = "castId",
        ),
    )
    val cast: List<CastEntity>,
)