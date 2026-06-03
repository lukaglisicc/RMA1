package com.example.rma1.movies.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quiz_results")
data class QuizResultEntity (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val score: Float,
    val category: Int,
    val timestamp: Long,
    val ranking: Int,
)