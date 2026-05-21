package com.example.rma1.movies.db

import com.example.rma1.movies.MovieRepository
import com.example.rma1.movies.MovieRepository.MoviesState
import com.example.rma1.movies.network.MovieResponse
import com.example.rma1.movies.network.NetworkMovieApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DatabaseMovieRepository(
    private val appDatabase: AppDatabase,
    private val movieApi: NetworkMovieApi,
) : MovieRepository {



    private val filters = Filters()

    private val _movies = MutableStateFlow(MoviesState())

    private val _filters = MutableStateFlow(MovieRepository.Filters())

    init {
        CoroutineScope(context = Dispatchers.IO).launch { observeDBMovies() }
    }

    override fun observeMovies(): Flow<MoviesState> = _movies.asStateFlow()

    private suspend fun observeDBMovies(){
        appDatabase.movieDao()
            .observeMovies(buildMovieQuery(filters))
            .distinctUntilChanged()
            .map { value -> value.map { it.toRepositoryMovie() } }
            .collect { movies ->
                _movies.update {
                    it.copy(
                        movieResponse = MovieRepository.MovieResponse(
                            items = movies,
                            totalItems = appDatabase.movieDao().getMovieCount(),
                        )
                    )
                }
            }
    }



    override fun observeFilters(): Flow<MovieRepository.Filters> {
        return _filters.asStateFlow()
    }

    override suspend fun setQueryFilters(
        genreId: Int?,
        query: String?,
        minYear: Int?,
        maxYear: Int?,
        minRating: Float?
    ) {
        //
    }

    override suspend fun setQuerySorting(
        sortBy: MovieRepository.SortType,
        sortOrder: String
    ) {
        //
    }

    override suspend fun queryMovies() {
        _movies.update {
            it.copy(
                isLoading = true,
                error = null,
            )
        }
        try {
            val response = movieApi.getMovies(filters)
            appDatabase.movieDao().upsertMovies(response.items.map { it.toMovieEntity() })
            response.items.forEach { movie ->
                appDatabase.movieDao().upsertGenres(movie.genres.map { it.toGenreEntity() })
                appDatabase.movieDao().upsertMoviesGenres(movie.toMoviesGenres())

            }

            _movies.update {
                it.copy(
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

    override suspend fun getMovieDetails(id: String): MovieRepository.MovieDetails {
        return MovieRepository.MovieDetails(
            title = "",
            desc = "",
            budget = 0,
            revenue = 0,
            languageCode = "",
            popularity = 0f,
            imdbRating = 0f,
            posterPath = "",
            backdropPath = "",
            genres = emptyList(),
            trailerPath = "",
            imagePaths = emptyList(),
            cast = emptyList(),
        )
    }

    override suspend fun getGenres(): List<MovieRepository.Genre> {
        return emptyList()
    }
}

private suspend fun NetworkMovieApi.getMovies(filters: Filters): MovieResponse {
    return getMovies()
}