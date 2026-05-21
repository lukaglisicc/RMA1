package com.example.rma1.movies.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.RoomRawQuery
import androidx.room.Transaction
import androidx.room.Upsert
import com.example.rma1.movies.db.entities.GenreEntity
import com.example.rma1.movies.db.entities.ImagePathEntity
import com.example.rma1.movies.db.entities.MovieDetailsFull
import com.example.rma1.movies.db.entities.MovieEntity
import com.example.rma1.movies.db.entities.MovieGenreCrossRef
import com.example.rma1.movies.db.entities.MovieWithGenres
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {

    @Transaction
    @RawQuery(observedEntities = [MovieEntity::class, GenreEntity::class, MovieGenreCrossRef::class, ImagePathEntity::class])
    fun observeMovies(
        query: RoomRawQuery
    ): Flow<List<MovieWithGenres>>

    @Transaction
    @Query("SELECT * FROM movie_details WHERE movieId = :id")
    fun observeMovieDetails(
        id: String,
    ): Flow<MovieDetailsFull>

    @Upsert
    suspend fun upsertMovies(movies: List<MovieEntity>)

    @Upsert
    suspend fun upsertGenres(genres: List<GenreEntity>)

    @Upsert
    suspend fun upsertMoviesGenres(moviesGenres: List<MovieGenreCrossRef>)

    @Upsert
    suspend fun upsertImagePaths(imagePaths: List<ImagePathEntity>)

    @Query("SELECT COUNT(*) FROM movies")
    suspend fun getMovieCount(): Int



}