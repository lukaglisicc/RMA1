package com.example.rma1.views.quiz

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.rma1.movies.quiz.NoMoviesException
import com.example.rma1.views.core.shared.PlatformBackHandler
import com.example.rma1.views.core.shared.ScreenBase
import com.example.rma1.views.core.shared.truncate
import okio.IOException

@Composable
fun QuizScreen(
    viewModel: QuizViewModel,
    onClose: () -> Unit,
) {
    val state by viewModel.state.collectAsState()
    val eventPublisher = viewModel::setEvent

    if(state.error == null) {
        when(val quizState = state.quizState){
            QuizContract.QuizState.Home -> {
                ScreenBase(
                    onBack = onClose,
                    title = "Quiz",
                ){ paddingValues ->

                    if(state.isLoading){
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(paddingValues),
                            contentAlignment = Alignment.Center,
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    else{
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .padding(paddingValues)
                                .padding(horizontal = 24.dp)
                                .fillMaxSize()
                        ){
                            Button(
                                onClick = { eventPublisher(QuizContract.UiEvent.StartQuiz) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                            ) {
                                Text("Start quiz")
                            }
                        }
                    }
                }
            }

            is QuizContract.QuizState.InQuiz -> {
                MainScreen(
                    eventPublisher = eventPublisher,
                    quizState = quizState,
                )
            }

            is QuizContract.QuizState.Report -> {
                ScreenBase(
                    onBack = onClose,
                    title = "Quiz",
                ){ paddingValues ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(text = "Correct answers: ${quizState.correctAnswers}/${quizState.questionCount}")
                        Text(text = "Score: ${quizState.score.truncate(1)}")
                    }
                }
            }
        }
    } else {
        ScreenBase(
            onBack = onClose,
            title = "Quiz",
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center,
            ) {
                when (state.error) {
                    is NoMoviesException -> {
                        Text(text = "Browse the catalog first to populate your quiz pool.")
                    }

                    is IOException -> {
                        Text(text = "Your device is offline, cannot generate quiz.")
                    }

                    else -> {
                        Text(text = "Error: ${state.error!!.message}")
                    }
                }

            }
        }
    }

}

@Composable
private fun MainScreen(
    eventPublisher: (QuizContract.UiEvent) -> Unit,
    quizState: QuizContract.QuizState.InQuiz,
) {

    val state by quizState.quizState.collectAsState()

    ExitDialog(eventPublisher)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeContentPadding(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        Text(
            text = "${state.remainingTime}s",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        AnimatedContent(
            targetState = state.currentQuestion.id,
            transitionSpec = {
                slideInHorizontally { it } + fadeIn() togetherWith
                        slideOutHorizontally { -it } + fadeOut()
            }
        ){
            QuestionCard(
                currentQuestion = state.currentQuestion,
                eventPublisher = eventPublisher,
            )
        }


    }
}

@Composable
private fun QuestionCard(
    currentQuestion: QuizContract.Question,
    eventPublisher: (QuizContract.UiEvent) -> Unit,
){
    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 30.dp),
        verticalArrangement = Arrangement.SpaceBetween,
    ){
        Column(
            modifier = Modifier
                .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            when (currentQuestion) {

                is QuizContract.Question.GuessTheMovie -> {

                    AsyncImage(
                        model = currentQuestion.picturePath,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "What movie is on the image?",
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center,
                    )
                }

                is QuizContract.Question.GuessTheActor -> {

                    AsyncImage(
                        model = currentQuestion.picturePath,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                    )

                    Text(
                        text = currentQuestion.movieName,
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center,
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Which actor/actress plays in this movie?",
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center,
                    )
                }

                is QuizContract.Question.GuessTheYear -> {

                    AsyncImage(
                        model = currentQuestion.picturePath,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                    )

                    Text(
                        text = currentQuestion.movieName,
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center,
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "What year did this movie come out in?",
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center,
                    )
                }

                is QuizContract.Question.EmptyQuestion -> {}
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Answers
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {


            currentQuestion.answers.forEach { answer ->
                AnimatedContent(
                    targetState = currentQuestion.isRevealed,
                    transitionSpec = {

                        fadeIn(
                            animationSpec = tween(300)
                        ) togetherWith fadeOut(
                            animationSpec = tween(300)
                        )
                    },
                ){
                    val color = when {

                        answer.isCorrect -> Color.Green

                        !(answer.isCorrect) -> Color.Red

                        else -> MaterialTheme.colorScheme.surface
                    }

                    Button(
                        onClick = {
                            eventPublisher(
                                QuizContract.UiEvent.AnswerSelected(answer)
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = if(currentQuestion.isRevealed) ButtonDefaults.buttonColors(containerColor = color) else ButtonDefaults.buttonColors()
                    ) {
                        Text(answer.text)
                    }
                }

            }
        }
    }
}

@Composable
private fun ExitDialog(
    eventPublisher: (QuizContract.UiEvent) -> Unit,
) {
    var showExitDialog by remember { mutableStateOf(false) }

    PlatformBackHandler {
        showExitDialog = true
    }

    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = {
                showExitDialog = false
            },
            title = {
                Text("Quit quiz?")
            },
            text = {
                Text("Your current progress will be lost.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showExitDialog = false
                        eventPublisher(QuizContract.UiEvent.QuitQuiz)
                    }
                ) {
                    Text("Quit")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showExitDialog = false
                    }
                ) {
                    Text("Continue")
                }
            }
        )
    }
}