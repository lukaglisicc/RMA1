package com.example.rma1.views.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rma1.movies.quiz.Quiz
import com.example.rma1.movies.quiz.QuizGenerator
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

                        setState {
                            copy(
                                quizState = QuizContract.QuizState.Report(
                                    correctAnswers = score?.correctAnswers ?: 0,
                                    questionCount = score?.questionCount ?: 0,
                                    score = score?.score ?: 0f,
                                )
                            )
                        }
                    }
                }
        }
    }



}