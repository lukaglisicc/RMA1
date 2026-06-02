package com.example.rma1.di

import com.example.rma1.movies.quiz.QuizGenerator
import com.example.rma1.movies.quiz.QuizGeneratorDefault
import org.koin.dsl.bind
import org.koin.dsl.module

val quizModule = module {

    single { QuizGeneratorDefault(movieRepository = get()) } bind QuizGenerator::class

}