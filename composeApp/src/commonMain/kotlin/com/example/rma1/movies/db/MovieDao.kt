package com.example.rma1.movies.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.RoomRawQuery
import androidx.room.Transaction
import com.example.rma1.movies.db.entities.GenreEntity
import com.example.rma1.movies.db.entities.MovieDetailsFull
import com.example.rma1.movies.db.entities.MovieEntity
import com.example.rma1.movies.db.entities.MovieGenreCrossRef
import com.example.rma1.movies.db.entities.MovieWithGenres
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {

    @Transaction
    @RawQuery(observedEntities = [MovieEntity::class, GenreEntity::class, MovieGenreCrossRef::class])
    fun observeMovies(
        query: RoomRawQuery
    ): Flow<List<MovieWithGenres>>

    @Transaction
    @Query("SELECT * FROM movie_details WHERE movieId = :id")
    fun observeMovieDetails(
        id: String,
    ): Flow<MovieDetailsFull>

}