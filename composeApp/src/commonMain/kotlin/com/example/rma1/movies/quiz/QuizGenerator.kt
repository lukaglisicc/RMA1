package com.example.rma1.movies.quiz

interface QuizGenerator {
    suspend fun generateQuiz(): Quiz
}