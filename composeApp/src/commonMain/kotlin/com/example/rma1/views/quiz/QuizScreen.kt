package com.example.rma1.views.quiz

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import io.ktor.client.plugins.ResponseException
import okio.IOException

@Composable
fun QuizScreen(
    viewModel: QuizViewModel,
    onClose: () -> Unit,
) {
    val state by viewModel.state.collectAsState()
    val eventPublisher = viewModel::setEvent

    when(val quizState = state.quizState){
        QuizContract.QuizState.Home -> {
            HomeScreen(
                eventPublisher = eventPublisher,
                state = state,
                onClose = onClose,
            )
        }

        is QuizContract.QuizState.InQuiz -> {
            MainScreen(
                eventPublisher = eventPublisher,
                quizState = quizState,
            )
        }

        is QuizContract.QuizState.Report -> {
            ReportScreen(
                eventPublisher = eventPublisher,
                state = state,
                onClose = onClose,
                quizState = quizState,
            )
        }
    }

}

@Composable
private fun HomeScreen(
    onClose: () -> Unit,
    state: QuizContract.UiState,
    eventPublisher: (QuizContract.UiEvent) -> Unit,
) {
    ScreenBase(
        onBack = onClose,
        title = "Quiz",
    ){ paddingValues ->

        if(state.error != null) {
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
                        Text(text = "Error: ${state.error.message}")
                    }
                }
            }
        }

        else if(state.isLoading){
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
            HomeScreen(
                paddingValues = paddingValues,
                eventPublisher = eventPublisher,
            )
        }
    }
}

@Composable
private fun HomeScreen(
    paddingValues: PaddingValues,
    eventPublisher: (QuizContract.UiEvent) -> Unit,
){
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 24.dp)
    ) {
        ElevatedCard(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Quiz,
                    contentDescription = null,
                    modifier = Modifier.size(96.dp),
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(Modifier.height(24.dp))

                Text(
                    text = "Movie Quiz",
                    style = MaterialTheme.typography.headlineMedium
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "Test your movie knowledge and compete for the highest score.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(40.dp))

                Button(
                    onClick = {
                        eventPublisher(QuizContract.UiEvent.StartQuiz)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Icon(
                        Icons.Default.PlayArrow,
                        contentDescription = null
                    )

                    Spacer(Modifier.width(8.dp))

                    Text(
                        text = "Start Quiz",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun ReportScreen(
    onClose: () -> Unit,
    state: QuizContract.UiState,
    eventPublisher: (QuizContract.UiEvent) -> Unit,
    quizState: QuizContract.QuizState.Report,
) {
    ScreenBase(
        onBack = onClose,
        title = "Quiz",
    ){ paddingValues ->

        if(state.error != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center,
            ) {
                when (state.error) {


                    is IOException, is ResponseException -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.TopCenter,
                        ) {
                            Text(
                                text = "A network error has occurred, score couldn't be saved.",
                                style = MaterialTheme.typography.bodyMediumEmphasized
                            )
                        }
                        ReportScreen(
                            paddingValues = paddingValues,
                            quizState = quizState,
                            eventPublisher = eventPublisher,
                        )
                    }

                    else -> {
                        Text(text = "Error: ${state.error.message}")
                    }
                }
            }
        }

        else if(state.isLoading){
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
            ReportScreen(
                paddingValues = paddingValues,
                quizState = quizState,
                eventPublisher = eventPublisher,
            )
        }
    }
}

@Composable
private fun ReportScreen(
    paddingValues: PaddingValues,
    quizState: QuizContract.QuizState.Report,
    eventPublisher: (QuizContract.UiEvent) -> Unit
) {

    val score = remember { Animatable(0f) }

    LaunchedEffect(quizState) {
        score.snapTo(0f)
        score.animateTo(
            quizState.score,
            tween(1000)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                modifier = Modifier.size(96.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = "Quiz Complete!",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = score.value.truncate(1).toString(),
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "Final Score",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(32.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ResultCard(
                    title = "Correct",
                    value = "${quizState.correctAnswers}/${quizState.questionCount}",
                    modifier = Modifier.weight(1f)
                )

                ResultCard(
                    title = "Rank",
                    value = "#${quizState.rank}",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(40.dp))

            Button(
                onClick = { eventPublisher(QuizContract.UiEvent.StartQuiz) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Icon(
                    Icons.Default.Refresh,
                    contentDescription = null
                )

                Spacer(Modifier.width(8.dp))

                Text("Play Again")
            }
        }
    }
}

@Composable
private fun ResultCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    ElevatedCard(modifier = modifier) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall
            )

            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
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

        QuizProgressBar(
            progress = state.progress,
            modifier = Modifier.padding(horizontal = 24.dp),
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
                            .weight(1f)
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
                            .weight(1f)
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
        Column(
            modifier = Modifier
                .weight(1.5f),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {


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
                        colors =
                            if(currentQuestion.isRevealed)
                                ButtonDefaults.buttonColors(
                                containerColor = color,
                                disabledContainerColor = color,
                                disabledContentColor = Color.Black,
                            )
                            else
                                ButtonDefaults.buttonColors(),
                        enabled = !currentQuestion.isRevealed,
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

@Composable
private fun QuizProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(
            durationMillis = 500,
            easing = FastOutSlowInEasing
        ),
        label = "quiz_progress"
    )

    LinearProgressIndicator(
        progress = { animatedProgress },
        modifier = modifier.fillMaxWidth()
    )
}