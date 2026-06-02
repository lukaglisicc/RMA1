package com.example.rma1.views.quiz

import kotlinx.coroutines.flow.StateFlow

interface QuizContract {


    data class UiState(
        val quizState: QuizState = QuizState.Home,
        val isLoading: Boolean = false,
        val error: Throwable? = null,
    )

    sealed class QuizState {
        data object Home: QuizState()

        data class InQuiz(
            val quizState: StateFlow<InQuizState>,
        ): QuizState()

        data class Report(
            val correctAnswers: Int = 0,
            val questionCount: Int = 0,
            val score: Float = 0f,
        ): QuizState()
    }

    sealed class UiEvent {
        data object StartQuiz: UiEvent()
        data object QuitQuiz: UiEvent()
        data class AnswerSelected(
            val answer: Answer
        ): UiEvent()
    }

    enum class QuestionType {
        MOVIE,
        YEAR,
        ACTOR
    }

    data class Answer(
        val text: String,
        val isCorrect: Boolean
    )

    sealed class Question {

        abstract val id: Int
        abstract val picturePath: String
        abstract val answers: List<Answer>
        abstract val isRevealed: Boolean

        data class GuessTheMovie(
            override val id: Int,
            override val picturePath: String,
            override val answers: List<Answer>,
            override val isRevealed: Boolean = false,
        ) : Question()

        data class GuessTheYear(
            override val id: Int,
            val movieName: String,
            override val picturePath: String,
            override val answers: List<Answer>,
            override val isRevealed: Boolean = false,
        ) : Question()

        data class GuessTheActor(
            override val id: Int,
            val movieName: String,
            override val picturePath: String,
            override val answers: List<Answer>,
            override val isRevealed: Boolean = false,
        ) : Question()

        data class EmptyQuestion(
            override val id: Int = 0,
            override val picturePath: String = "",
            override val answers: List<Answer> = emptyList(),
            override val isRevealed: Boolean = false,
        ) : Question()
    }

    data class InQuizState(
        val currentQuestion: Question = Question.EmptyQuestion(),
        val remainingTime: Int = 0,
        val progress: Float = 0f,
    )
}