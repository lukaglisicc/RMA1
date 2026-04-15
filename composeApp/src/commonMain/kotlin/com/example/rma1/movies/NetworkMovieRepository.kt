package com.example.rma1.movies

import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import com.example.rma1.movies.MovieRepository.MoviesState
import io.ktor.client.plugins.api.SetupRequest
import kotlin.Int


class NetworkMovieRepository : MovieRepository{

    private var pageSize: Int = 30
    private var sortBy: MovieRepository.sortType = MovieRepository.sortType.RATING
    private var sortOrder: String = "desc"
    private var genreId: Int? = null
    private var query: String? = null
    private var minYear: Int? = null
    private var maxYear: Int? = null
    private var minRating: Float? = null

    enum class ImageType(val id: Int){
        POSTER(1),
        BACKDROP(2),
        PROFILE(3),
        LOGO(4),
    }

    //Network setup
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                }
            )
        }
    }
    private val ktorfit = Ktorfit.Builder()
    .baseUrl("https://rma.finlab.rs/")
    .httpClient(client)
    .build()
    private val api = ktorfit.create<MovieApi>()
    //Flow setup
    private val _movies = MutableStateFlow(MoviesState())

    private var config: List<ConfigPair>? = null

    //Load movies at init
    init {
        loadMovies()
    }


    override fun observeMovies(): Flow<MoviesState> = _movies.asStateFlow()

    override fun setQueryParams(
        pageSize: Int?,
        sortBy: MovieRepository.sortType?,
        sortOrder: String?,
        genreId: Int?,
        query: String?,
        minYear: Int?,
        maxYear: Int?,
        minRating: Float?
    ) {
        pageSize?.let { this.pageSize = it }
        sortBy?.let { this.sortBy = it }
        sortOrder?.let { this.sortOrder = it }
        genreId?.let { this.genreId = it }
        query?.let { this.query = it }
        minYear?.let { this.minYear = it }
        maxYear?.let { this.maxYear = it }
        minRating?.let { this.minRating = it }
        queryMovies()
    }

    override fun resetQueryParams() {
        pageSize = 30
        genreId = null
        query = null
        minYear = null
        maxYear = null
        minRating = null
        queryMovies()
    }

    override fun resetQuerySort() {
        sortBy = MovieRepository.sortType.RATING
        sortOrder = "desc"
        queryMovies()
    }

    override fun queryMovies() {
        loadMovies(
            pageSize = pageSize,
            sortBy = mapSort(sortBy),
            sortOrder = sortOrder,
            genreId = genreId,
            query = query,
            minYear = minYear,
            maxYear = maxYear,
            minRating = minRating,
        )
    }

    override suspend fun getMovieDetails(id: String): MovieDetailsFull {
        var movieDetails =  api.getMovieDetails(id)
        movieDetails = movieDetails.copy(
            posterPath = getImageUrl(path = movieDetails.posterPath, 2),
            backdropPath = getImageUrl(path = movieDetails.backdropPath, quality = 2, imageType = ImageType.BACKDROP)
        )
        val imagePaths = getMovieImages(id)
        val cast = getMovieCast(id)
        return MovieDetailsFull(movieDetails, imagePaths, cast)
    }

    fun loadMovies(
        pageSize: Int = 30,
        sortBy: String = "imdb_rating",
        sortOrder: String = "desc",
        genreId: Int? = null,
        query: String? = null,
        minYear: Int? = null,
        maxYear: Int? = null,
        minRating: Float? = null
    ) {
        _movies.update {
            it.copy(
                isLoading = true,
                error = null,
            )
        }
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val movies = getMovies(
                    pageSize = pageSize,
                    sortBy = sortBy,
                    sortOrder = sortOrder,
                    genreId = genreId,
                    query = query,
                    minYear = minYear,
                    maxYear = maxYear,
                    minRating = minRating,
                )
                _movies.update {
                    it.copy(
                        movies = movies,
                        isLoading = false,
                    )
                }
            } catch (e: Exception){
                _movies.update {
                    it.copy(
                        isLoading = false,
                        error = e,
                    )
                }
            }
        }
    }

    private suspend fun getMovies(
        pageSize: Int = 30,
        sortBy: String = "imdb_rating",
        sortOrder: String = "desc",
        genreId: Int? = null,
        query: String? = null,
        minYear: Int? = null,
        maxYear: Int? = null,
        minRating: Float? = null
    ): List<Movie> {
        return api.getMovies(
            pageSize = pageSize,
            sortBy = sortBy,
            sortOrder = sortOrder,
            genreId = genreId,
            query = query,
            minYear = minYear,
            maxYear = maxYear,
            minRating = minRating,
        )
            .items
            .map{ movie ->
                movie.copy(
                    posterPath = getImageUrl(movie.posterPath, 1)
                )
            }
    }

    private suspend fun getImageUrl(path: String?, quality: Int, imageType: ImageType = ImageType.POSTER): String {
        path ?: return ""

        config = config ?: api.getImageConfig()

        val cfg = config ?: error("Config not loaded")


        val baseUrl = cfg[0].value
        val sizeList = cfg[imageType.id].value.split(",")

        val size = sizeList.getOrNull(quality)
            ?: sizeList.first()


        return "$baseUrl$size$path"
    }

    private suspend fun getMovieImages(movieId: String): List<String> {
        val backdrops = api.getMovieImages(movieId)
        return backdrops.backdrops.map { image ->
            getImageUrl(image.filePath, 0, ImageType.BACKDROP)
        }
    }

    private suspend fun getMovieCast(movieId: String): List<Cast> {
        return api.getMovieCast(movieId)
            .items
            .map { cast ->
                cast.copy(
                    profilePath = getImageUrl(cast.profilePath, 1, ImageType.PROFILE)
                )
            }
    }

    private fun mapSort(sortType: MovieRepository.sortType) : String{
        return when(sortType){
            MovieRepository.sortType.RATING -> "imdb_rating"
            MovieRepository.sortType.POPULARITY -> "popularity"
            MovieRepository.sortType.YEAR -> "year"
            MovieRepository.sortType.TITLE -> "title"
        }
    }

}