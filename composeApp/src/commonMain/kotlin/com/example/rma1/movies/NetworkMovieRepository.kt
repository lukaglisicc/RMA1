package com.example.rma1.movies

import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.serialization.json.Json
import com.example.rma1.movies.MovieRepository.MoviesState
import kotlin.Int


class NetworkMovieRepository : MovieRepository{

    private var pageSize: Int = 30
    private var sortBy: MovieRepository.SortType = MovieRepository.SortType.RATING
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

    private val _filters = MutableStateFlow(Filters())

    private var config: List<ConfigPair>? = null


    override fun observeMovies(): Flow<MoviesState> = _movies.asStateFlow()

    override fun observeFilters(): Flow<Filters> = _filters.asStateFlow()

    override suspend fun setQueryFilters(
        genreId: Int?,
        query: String?,
        minYear: Int?,
        maxYear: Int?,
        minRating: Float?
    ) {
        this.genreId = genreId
        this.query = query
        this.minYear = minYear
        this.maxYear = maxYear
        this.minRating = minRating
        updateFilters()
    }

    override suspend fun setQuerySorting(
        sortBy: MovieRepository.SortType,
        sortOrder: String
    ) {
        this.sortBy = sortBy
        this.sortOrder = sortOrder
    }

    override suspend fun queryMovies() {
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
        val trailerPath = api.getMovieTrailers(id)[0].key
        return MovieDetailsFull(movieDetails, imagePaths, cast, trailerPath)
    }

    override suspend fun getGenres(): List<Genre> {
        return api.getGenres()
    }

    suspend fun loadMovies(
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
        try {
            val movieResponse = getMovies(
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
                    movieResponse = movieResponse,
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

    private suspend fun getMovies(
        pageSize: Int = 30,
        sortBy: String = "imdb_rating",
        sortOrder: String = "desc",
        genreId: Int? = null,
        query: String? = null,
        minYear: Int? = null,
        maxYear: Int? = null,
        minRating: Float? = null
    ): MovieResponse {
        var response = api.getMovies(
            pageSize = pageSize,
            sortBy = sortBy,
            sortOrder = sortOrder,
            genreId = genreId,
            query = query,
            minYear = minYear,
            maxYear = maxYear,
            minRating = minRating,
        )
        response = response.copy(
            items = response
                .items
                .map{ movie ->
                    movie.copy(
                        posterPath = getImageUrl(movie.posterPath, 1)
                    )
                }
        )
        return response
    }

    private fun updateFilters(){
        _filters.update {
            it.copy(
                genreId = genreId,
                query = query,
                minYear = minYear,
                maxYear = maxYear,
                minRating = minRating,
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

    private fun mapSort(sortType: MovieRepository.SortType) : String{
        return when(sortType){
            MovieRepository.SortType.RATING -> "imdb_rating"
            MovieRepository.SortType.POPULARITY -> "popularity"
            MovieRepository.SortType.YEAR -> "year"
            MovieRepository.SortType.TITLE -> "title"
        }
    }

}