package com.example.rma1.db

import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.example.rma1.movies.db.AppDatabase
import java.io.File

fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {
    val appDir = File(System.getProperty("user.home"), ".rma")
    if (!appDir.exists()) appDir.mkdirs()
    val dbFile = File(appDir, "app_database.db")
    return Room.databaseBuilder<AppDatabase>(name = dbFile.absolutePath,)
        .setDriver(BundledSQLiteDriver())
}