package com.example.rma1.movies.quiz

import com.example.rma1.movies.MovieRepository

class QuizGeneratorDefault (
    private val movieRepository: MovieRepository,
): QuizGenerator {

    var movieCache: List<MovieRepository.MovieDetails> = emptyList()
    var questionIndex = 0


    override suspend fun generateQuiz(): Quiz {
        val questions = try {
            generateQuizQuestions(true)
        } catch (e: NoMoviesException){
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

    private suspend fun generateQuizQuestions(onlyLoaded: Boolean): List<Question> {

        loadMovieCache(onlyLoaded)
        questionIndex = 0

        val maxPerType: Int = 4
        val totalQuestions: Int = 10


        val counts = mutableMapOf(
            QuestionType.MOVIE to 0,
            QuestionType.YEAR to 0,
            QuestionType.ACTOR to 0,
        )

        val selectedTypes = mutableListOf<QuestionType>()
        val usedMovies = mutableListOf<String>()

        repeat(totalQuestions) {

            val previous = selectedTypes.lastOrNull()

            val available = QuestionType.entries.filter { type ->
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

                QuestionType.MOVIE -> {
                    generateMovieQuestion(usedMovies)
                }

                QuestionType.YEAR -> {
                    generateYearQuestion(usedMovies)
                }

                QuestionType.ACTOR -> {
                    generateActorQuestion(usedMovies)
                }
            }
        }
    }

    private fun generateMovieQuestion(usedMovies: MutableList<String>): Question.GuessTheMovie {
        while(true){
            val movieDetails =
                getRandomMovieDetails(usedMovies)
                    ?: break


            val answersIds = mutableListOf(movieDetails.id)
            val answers = mutableListOf(Answer(movieDetails.title, true))
            repeat(3){
                val new = getRandomMovieDetails(answersIds)?: throw NoMoviesException()
                answersIds.add(new.id)
                answers.add(Answer(new.title, false))
            }
            answers.shuffle()

            usedMovies.add(movieDetails.id)

            return Question.GuessTheMovie(
                picturePath = movieDetails.imagePaths.first(),
                answers = answers,
                id = questionIndex++,
            )
        }
        throw NoMoviesException()
    }

    private fun generateYearQuestion(usedMovies: MutableList<String>): Question.GuessTheYear {
        val movieDetails = getRandomMovieDetails(usedMovies)?: throw NoMoviesException()
        val years = generateNearbyNumbers(movieDetails.year, 10)
        val answers = years.map { Answer(it.toString(), false) }.toMutableList()
        answers.add(Answer(movieDetails.year.toString(), true))
        answers.shuffle()

        usedMovies.add(movieDetails.id)
        return Question.GuessTheYear(
            movieName = movieDetails.title,
            picturePath = movieDetails.posterPath,
            answers = answers,
            id = questionIndex++,
        )
    }

    private fun generateActorQuestion(usedMovies: MutableList<String>): Question.GuessTheActor {
        val movieDetails =
            getRandomMovieDetails(usedMovies)
                ?: throw NoMoviesException()

        val correctActor =
            movieDetails.cast.firstOrNull()
                ?: throw NoMoviesException()

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

        val answers = uniqueWrong.map { Answer(it.name, false) }.toMutableList()
        answers.add(Answer(correctActor.name, true))
        answers.shuffle()

        usedMovies.add(movieDetails.id)

        return Question.GuessTheActor(
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