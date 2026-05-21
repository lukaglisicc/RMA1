package com.example.rma1.movies.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cast")
data class CastEntity (
    @PrimaryKey val id: String,
    val name: String,
    val profilePath: String,
)