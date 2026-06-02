package com.example.rma1.movies.quiz


import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

class Quiz (
    private val questions: MutableList<Question>,
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

    suspend fun selectAnswer(answer: Answer){

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
    val currentQuestion: Question,
    val remainingTime: Int,
    val progress: Float,
    val isFinished: Boolean,
)

fun Question.reveal(): Question =
    when (this) {
        is Question.GuessTheActor ->
            copy(isRevealed = true)

        is Question.GuessTheMovie ->
            copy(isRevealed = true)

        is Question.GuessTheYear ->
            copy(isRevealed = true)
    }

data class QuizScoring(
    val score: Float,
    val correctAnswers: Int,
    val questionCount: Int,
    val totalTime: Int,
    val remainingTime: Int,
)

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
}

class NoMoviesException() : Exception()