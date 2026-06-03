package com.example.rma1.views.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rma1.movies.MovieRepository
import com.example.rma1.movies.quiz.Quiz
import com.example.rma1.movies.quiz.QuizGenerator
import com.example.rma1.movies.quiz.QuizScoring
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class QuizViewModel (
    private val quizGenerator: QuizGenerator,
    private val movieRepository: MovieRepository,
): ViewModel() {

    private var quiz: Quiz? = null
    private var quizJob: Job? = null
    private val _state = MutableStateFlow(QuizContract.UiState())

    val state = _state.asStateFlow()

    private fun setState(reducer: QuizContract.UiState.() -> QuizContract.UiState){
        _state.getAndUpdate(reducer)
    }

    private val events = MutableSharedFlow<QuizContract.UiEvent>()
    fun setEvent(event: QuizContract.UiEvent){
        viewModelScope.launch { events.emit(event) }
    }

    init {
        observeEvents()
    }

    private fun observeEvents(){
        viewModelScope.launch {
            events.collect{ event ->
                when(event){
                    is QuizContract.UiEvent.QuitQuiz -> {

                        quizJob?.cancel()
                        quiz = null

                        setState { copy(
                            quizState = QuizContract.QuizState.Home,
                        ) }
                    }

                    is QuizContract.UiEvent.StartQuiz -> {
                        viewModelScope.launch (Dispatchers.IO){

                            runCatching {

                                setState { copy(isLoading = true) }

                                quiz = quizGenerator.generateQuiz()
                            }.fold(
                                onSuccess = {

                                    val quizAsserted = quiz!!

                                    setState { copy(

                                        quizState = QuizContract.QuizState.InQuiz(
                                            quizState = quizAsserted.state.map { it.toUiInQuizState() }
                                                .stateIn(
                                                    viewModelScope,
                                                    SharingStarted.WhileSubscribed(5000),
                                                    QuizContract.InQuizState()
                                                )
                                        ),
                                        isLoading = false,
                                    ) }

                                    observeQuiz()

                                    quizAsserted.startQuiz(viewModelScope)

                                },
                                onFailure = {
                                    setState { copy(
                                        error = it,
                                        isLoading = false,
                                    ) }
                                }
                            )
                        }
                    }

                    is QuizContract.UiEvent.AnswerSelected -> {

                        quiz?.selectAnswer(event.answer.toModelAnswer())

                    }
                }
            }
        }
    }

    private fun observeQuiz() {
        quizJob?.cancel()

        quizJob = viewModelScope.launch {
            quiz?.state
                ?.collect { state ->

                    if (state.isFinished) {
                        quizJob?.cancel()

                        val score = quiz?.getResults()

                        if (score != null) {
                            finishQuiz(score)
                        } else {
                            setState { copy(
                                quizState = QuizContract.QuizState.Home,
                            ) }
                        }
                    }
                }
        }
    }

    private fun finishQuiz(
        score: QuizScoring,
    ) {
        setState { copy(
            isLoading = true,
        ) }

        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                movieRepository.submitQuizResult(
                    MovieRepository.QuizResult(
                        score = score.score,
                        category = 1,
                    )
                )
            }.fold(
                onSuccess = { rank ->
                    setState {
                        copy(
                            quizState = QuizContract.QuizState.Report(
                                correctAnswers = score.correctAnswers,
                                questionCount = score.questionCount,
                                score = score.score,
                                rank = rank,
                            ),
                            isLoading = false,
                        )
                    }
                },
                onFailure = {
                    setState { copy(
                        error = it,
                        isLoading = false,
                        quizState = QuizContract.QuizState.Report(
                            correctAnswers = score.correctAnswers,
                            questionCount = score.questionCount,
                            score = score.score,
                        ),
                    ) }
                },
            )
        }
    }



}