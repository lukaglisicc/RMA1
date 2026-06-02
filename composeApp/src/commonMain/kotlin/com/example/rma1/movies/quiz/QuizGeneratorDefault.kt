package com.example.rma1.movies.quiz

import com.example.rma1.movies.MovieRepository
import com.example.rma1.views.quiz.QuizContract

class QuizGeneratorDefault (
    private val movieRepository: MovieRepository,
): QuizGenerator {

    var movieCache: List<MovieRepository.MovieDetails> = emptyList()
    var questionIndex = 0


    override suspend fun generateQuiz(): Quiz {
        val questions = try {
            generateQuizQuestions(true)
        } catch (e: QuizContract.NoMoviesException){
            generateQuizQuestions(false)
        }

        return Quiz(
            questions = questions.toMutableList(),
            totalTime = 60,
            scoring = ::scoring,
        )
    }

    private fun scoring(correctAnswers: Int, questionCount: Int, remainingTime: Int, totalTime: Int): Float {
        return (correctAnswers.toFloat() * (9f + remainingTime.toFloat() / totalTime.toFloat())).coerceIn(0f, 100f)
    }

    private suspend fun generateQuizQuestions(onlyLoaded: Boolean): List<QuizContract.Question> {

        loadMovieCache(onlyLoaded)
        questionIndex = 0

        val maxPerType: Int = 4
        val totalQuestions: Int = 10


        val counts = mutableMapOf(
            QuizContract.QuestionType.MOVIE to 0,
            QuizContract.QuestionType.YEAR to 0,
            QuizContract.QuestionType.ACTOR to 0,
        )

        val selectedTypes = mutableListOf<QuizContract.QuestionType>()
        val usedMovies = mutableListOf<String>()

        repeat(totalQuestions) {

            val previous = selectedTypes.lastOrNull()

            val available = QuizContract.QuestionType.entries.filter { type ->
                counts[type]!! < maxPerType
            }

            // Prefer alternating
            val preferred = available.filter { it != previous }

            val chosen = when {
                preferred.isNotEmpty() -> preferred.random()
                else -> available.random()
            }

            counts[chosen] = counts[chosen]!! + 1
            selectedTypes += chosen
        }


        return selectedTypes.map { type ->

            when(type) {

                QuizContract.QuestionType.MOVIE -> {
                    generateMovieQuestion(usedMovies)
                }

                QuizContract.QuestionType.YEAR -> {
                    generateYearQuestion(usedMovies)
                }

                QuizContract.QuestionType.ACTOR -> {
                    generateActorQuestion(usedMovies)
                }
            }
        }
    }

    private fun generateMovieQuestion(usedMovies: MutableList<String>): QuizContract.Question.GuessTheMovie {
        while(true){
            val movieDetails =
                getRandomMovieDetails(usedMovies)
                    ?: break


            val answersIds = mutableListOf(movieDetails.id)
            val answers = mutableListOf(QuizContract.Answer(movieDetails.title, true))
            repeat(3){
                val new = getRandomMovieDetails(answersIds)?: throw QuizContract.NoMoviesException()
                answersIds.add(new.id)
                answers.add(QuizContract.Answer(new.title, false))
            }
            answers.shuffle()

            usedMovies.add(movieDetails.id)

            return QuizContract.Question.GuessTheMovie(
                picturePath = movieDetails.imagePaths.first(),
                answers = answers,
                id = questionIndex++,
            )
        }
        throw QuizContract.NoMoviesException()
    }

    private fun generateYearQuestion(usedMovies: MutableList<String>): QuizContract.Question.GuessTheYear {
        val movieDetails = getRandomMovieDetails(usedMovies)?: throw QuizContract.NoMoviesException()
        val years = generateNearbyNumbers(movieDetails.year, 10)
        val answers = years.map { QuizContract.Answer(it.toString(), false) }.toMutableList()
        answers.add(QuizContract.Answer(movieDetails.year.toString(), true))
        answers.shuffle()

        usedMovies.add(movieDetails.id)
        return QuizContract.Question.GuessTheYear(
            movieName = movieDetails.title,
            picturePath = movieDetails.posterPath,
            answers = answers,
            id = questionIndex++,
        )
    }

    private fun generateActorQuestion(usedMovies: MutableList<String>): QuizContract.Question.GuessTheActor {
        val movieDetails =
            getRandomMovieDetails(usedMovies)
                ?: throw QuizContract.NoMoviesException()

        val correctActor =
            movieDetails.cast.firstOrNull()
                ?: throw QuizContract.NoMoviesException()

        val usedActors = movieDetails.cast.toSet()

        val wrongAnswers = mutableListOf<MovieRepository.Cast>()

        repeat(3) {
            val movie =
                getRandomMovieDetails(
                    listOf(movieDetails.id)
                ) ?: return@repeat

            wrongAnswers += movie.cast.filter { it !in usedActors }
        }

        val uniqueWrong = wrongAnswers
            .distinctBy { it.name }
            .shuffled()
            .take(3)

        val answers = uniqueWrong.map { QuizContract.Answer(it.name, false) }.toMutableList()
        answers.add(QuizContract.Answer(correctActor.name, true))
        answers.shuffle()

        usedMovies.add(movieDetails.id)

        return QuizContract.Question.GuessTheActor(
            movieName = movieDetails.title,
            picturePath = movieDetails.posterPath,
            answers = answers,
            id = questionIndex++,
        )
    }

    private fun generateNearbyNumbers(original: Int, range: Int): MutableList<Int> {

        return ((original - range)..(original + range))
            .filter { it != original }
            .shuffled()
            .take(3)
            .toMutableList()
    }

    private suspend fun loadMovieCache(onlyLoaded: Boolean){
        movieCache = movieRepository.getMovieCache(30, onlyLoaded)
            .filter { it.posterPath.isNotBlank() && !(it.imagePaths.isEmpty())}

    }

    private fun getRandomMovieDetails(excludedIds: List<String>): MovieRepository.MovieDetails? {
        return movieCache.filter { it.id !in excludedIds }.shuffled().firstOrNull()
    }
}