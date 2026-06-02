package com.example.rma1.views.quiz

import com.example.rma1.movies.quiz.Answer
import com.example.rma1.movies.quiz.Question
import com.example.rma1.movies.quiz.QuizState

fun QuizContract.Answer.toModelAnswer(): Answer{
    return Answer(
        text = text,
        isCorrect = isCorrect,
    )
}

fun Answer.toUiAnswer(): QuizContract.Answer{
    return QuizContract.Answer(
        text = text,
        isCorrect = isCorrect,
    )
}

fun QuizState.toUiInQuizState(): QuizContract.InQuizState{
    return QuizContract.InQuizState(
        currentQuestion = currentQuestion.toUiQuestion(),
        remainingTime = remainingTime,
        progress = progress,
    )
}

fun Question.toUiQuestion(): QuizContract.Question{
    when(this){
        is Question.GuessTheActor -> return QuizContract.Question.GuessTheActor(
            id = id,
            movieName = movieName,
            picturePath = picturePath,
            answers = answers.map { it.toUiAnswer() },
            isRevealed = isRevealed,
        )
        is Question.GuessTheMovie -> return QuizContract.Question.GuessTheMovie(
            id = id,
            picturePath = picturePath,
            answers = answers.map { it.toUiAnswer() },
            isRevealed = isRevealed,
        )
        is Question.GuessTheYear -> return QuizContract.Question.GuessTheYear(
            id = id,
            movieName = movieName,
            picturePath = picturePath,
            answers = answers.map { it.toUiAnswer() },
            isRevealed = isRevealed,
        )
    }
}