package com.example.rma1.movies.quiz

import com.example.rma1.views.quiz.QuizContract
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

class Quiz (
    private val questions: MutableList<QuizContract.Question>,
    private val totalTime: Int,
    private val scoring: (correctAnswers: Int, questionCount: Int, remainingTime: Int, totalTime: Int) -> Float,
){
    private val questionCount = questions.size
    private var currentQuestionIndex = 0
    private var correctAnswers = 0
    private var timerJob: Job? = null
    private val _state = MutableStateFlow(QuizState(
        currentQuestion = questions.first(),
        remainingTime = totalTime,
        progress = 1f / questionCount.toFloat(),
        isFinished = false,
    ))
    val state = _state.asStateFlow()
    private fun setState(reducer: QuizState.() -> QuizState){
        _state.getAndUpdate (reducer)
    }

    fun startQuiz(scope: CoroutineScope){
        timerJob?.cancel()

        timerJob = scope.launch {
            runTimer()
        }
    }

    suspend fun selectAnswer(answer: QuizContract.Answer){

        if(questions[currentQuestionIndex].isRevealed) return

        questions[currentQuestionIndex] = questions[currentQuestionIndex].reveal()
        setState { copy(
            currentQuestion = questions[currentQuestionIndex],
            progress = (currentQuestionIndex + 2) / questionCount.toFloat()
        ) }


        delay(1.seconds)


        if(answer.isCorrect){
            correctAnswers++
        }

        currentQuestionIndex++

        if(currentQuestionIndex > questions.size - 1) {
            finishQuiz()
        } else {
            setState { copy(
                currentQuestion = questions[currentQuestionIndex],
            ) }
        }
    }

    private fun finishQuiz(){
        timerJob?.cancel()

        setState { copy(
            isFinished = true,
        ) }


    }

    fun getResults(): QuizScoring{
        return QuizScoring(
            score = scoring(correctAnswers, questionCount, state.value.remainingTime, totalTime),
            correctAnswers = correctAnswers,
            questionCount = questionCount,
            totalTime = totalTime,
            remainingTime = state.value.remainingTime,
        )
    }

    private suspend fun runTimer() {
        for (seconds in totalTime downTo 0) {

            setState {
                copy(
                    remainingTime = seconds,
                )
            }

            delay(1.seconds)
        }

        finishQuiz()
    }
}

data class QuizState(
    val currentQuestion: QuizContract.Question,
    val remainingTime: Int,
    val progress: Float,
    val isFinished: Boolean,
)

fun QuizContract.Question.reveal(): QuizContract.Question =
    when (this) {
        is QuizContract.Question.GuessTheActor ->
            copy(isRevealed = true)

        is QuizContract.Question.GuessTheMovie ->
            copy(isRevealed = true)

        is QuizContract.Question.GuessTheYear ->
            copy(isRevealed = true)
    }

data class QuizScoring(
    val score: Float,
    val correctAnswers: Int,
    val questionCount: Int,
    val totalTime: Int,
    val remainingTime: Int,
)