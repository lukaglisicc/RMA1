package com.example.rma1.movies.db

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import com.example.rma1.movies.db.entities.CastEntity
import com.example.rma1.movies.db.entities.GenreEntity
import com.example.rma1.movies.db.entities.ImagePathEntity
import com.example.rma1.movies.db.entities.MovieCastCrossRef
import com.example.rma1.movies.db.entities.MovieDetailsEntity
import com.example.rma1.movies.db.entities.MovieEntity
import com.example.rma1.movies.db.entities.MovieGenreCrossRef
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

@Database(
    entities = [
        MovieEntity::class,
        MovieDetailsEntity::class,
        CastEntity::class,
        GenreEntity::class,
        ImagePathEntity::class,
        MovieCastCrossRef::class,
        MovieGenreCrossRef::class,
    ],
    version = 1,
    exportSchema = true,
)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
}

@Suppress("NO_ACTUAL_FOR_EXPECT", "EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}

fun buildAppDatabase(
    builder: RoomDatabase.Builder<AppDatabase>,
): AppDatabase {
    return builder
        .fallbackToDestructiveMigrationOnDowngrade(dropAllTables = true)
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}