package com.example.rma1.movies.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.RoomRawQuery
import androidx.room.Transaction
import androidx.room.Upsert
import com.example.rma1.movies.db.entities.CastEntity
import com.example.rma1.movies.db.entities.FavoritesEntity
import com.example.rma1.movies.db.entities.GenreEntity
import com.example.rma1.movies.db.entities.ImagePathEntity
import com.example.rma1.movies.db.entities.MovieCastCrossRef
import com.example.rma1.movies.db.entities.MovieDetailsEntity
import com.example.rma1.movies.db.entities.MovieDetailsFull
import com.example.rma1.movies.db.entities.MovieEntity
import com.example.rma1.movies.db.entities.MovieGenreCrossRef
import com.example.rma1.movies.db.entities.MovieWithGenres
import com.example.rma1.movies.db.entities.QuizResultEntity
import com.example.rma1.movies.db.entities.WatchlistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {

    @Transaction
    @RawQuery(observedEntities = [MovieEntity::class, GenreEntity::class, MovieGenreCrossRef::class, ImagePathEntity::class])
    fun observeMovies(
        query: RoomRawQuery
    ): Flow<List<MovieWithGenres>>

    @Transaction
    @RawQuery(observedEntities = [MovieEntity::class, GenreEntity::class, MovieGenreCrossRef::class, ImagePathEntity::class, FavoritesEntity::class])
    fun observeFavorites(
        query: RoomRawQuery
    ): Flow<List<MovieWithGenres>>

    @Transaction
    @RawQuery(observedEntities = [MovieEntity::class, GenreEntity::class, MovieGenreCrossRef::class, ImagePathEntity::class, WatchlistEntity::class])
    fun observeWatchlist(
        query: RoomRawQuery
    ): Flow<List<MovieWithGenres>>

    @Transaction
    @Query("SELECT * FROM movie_details WHERE movieId = :id")
    fun observeMovieDetails(
        id: String,
    ): Flow<MovieDetailsFull?>

    @Transaction
    @Query("SELECT * FROM movie_details WHERE movieId IN (:ids)")
    suspend fun getMovieDetails(
        ids: List<String>,
    ): List<MovieDetailsFull>

    @Upsert
    suspend fun upsertMovies(movies: List<MovieEntity>)

    @Upsert
    suspend fun upsertGenres(genres: List<GenreEntity>)

    @Upsert
    suspend fun upsertMoviesGenres(moviesGenres: List<MovieGenreCrossRef>)

    @Insert
    suspend fun insertImagePaths(imagePaths: List<ImagePathEntity>)

    @Upsert
    suspend fun upsertMovieDetails(movieDetails: MovieDetailsEntity)

    @Query("SELECT COUNT(*) FROM movies")
    fun observeMovieCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM watchlist")
    fun observeWatchlistCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM favorites")
    fun observeFavoritesCount(): Flow<Int>

    @Query("SELECT DISTINCT * FROM genres")
    suspend fun getGenres(): List<GenreEntity>

    @Upsert
    suspend fun upsertCast(cast: List<CastEntity>)

    @Upsert
    suspend fun upsertMovieCast(movieCast: List<MovieCastCrossRef>)

    @Query("DELETE FROM watchlist")
    suspend fun clearWatchlist()

    @Upsert
    suspend fun upsertWatchlist(watchlist: List<WatchlistEntity>)

    @Transaction
    suspend fun replaceWatchlist(watchlist: List<WatchlistEntity>){
        clearWatchlist()
        upsertWatchlist(watchlist)
    }

    @Query("DELETE FROM favorites")
    suspend fun clearFavorites()

    @Upsert
    suspend fun upsertFavorites(favorites: List<FavoritesEntity>)

    @Transaction
    suspend fun replaceFavorites(favorites: List<FavoritesEntity>){
        clearFavorites()
        upsertFavorites(favorites)
    }

    @Query("""
    SELECT EXISTS(
        SELECT 1 FROM watchlist
        WHERE id = :movieId
    )
""")
    fun observeIsInWatchlist(movieId: String): Flow<Boolean>
    @Query("""
    SELECT EXISTS(
        SELECT 1 FROM watchlist
        WHERE id = :movieId
    )
""")
    suspend fun isInWatchlist(movieId: String): Boolean

    @Query("""
    SELECT EXISTS(
        SELECT 1 FROM favorites
        WHERE id = :movieId
    )
""")
    fun observeIsInFavorites(movieId: String): Flow<Boolean>
    @Query("""
    SELECT EXISTS(
        SELECT 1 FROM favorites
        WHERE id = :movieId
    )
""")
   suspend fun isInFavorites(movieId: String): Boolean

    @Query("""
        SELECT id FROM movies
        ORDER BY RANDOM()
        LIMIT (:count)
    """)
    suspend fun getRandomMovieIds(count: Int): List<String>

    @Transaction
    @Query("""
        SELECT * FROM movie_details
        ORDER BY RANDOM()
        LIMIT (:count)
    """)
    suspend fun getRandomMovieDetails(count: Int): List<MovieDetailsFull>

    @Upsert
    suspend fun upsertQuizResult(result: QuizResultEntity)

    @Upsert
    suspend fun upsertQuizResult(results: List<QuizResultEntity>)

    @Query("DELETE FROM quiz_results")
    suspend fun clearQuizResults()

    @Transaction
    suspend fun replaceQuizResults(results: List<QuizResultEntity>){
        clearQuizResults()
        upsertQuizResult(results)
    }

    @Query("SELECT COUNT(*) FROM quiz_results")
    fun observeQuizCount(): Flow<Int>

    @Query("SELECT score FROM quiz_results ORDER BY score DESC LIMIT 1")
    fun observeQuizBestScore(): Flow<Float>

}