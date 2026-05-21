package com.example.rma1.movies.db

import com.example.rma1.movies.MovieRepository
import kotlinx.coroutines.flow.Flow

class DatabaseMovieRepository(
    private val appDatabase: AppDatabase,
) : MovieRepository {

    override fun observeMovies(): Flow<MovieRepository.MoviesState> {
        TODO("Not yet implemented")
    }

    override fun observeFilters(): Flow<MovieRepository.Filters> {
        TODO("Not yet implemented")
    }

    override suspend fun setQueryFilters(
        genreId: Int?,
        query: String?,
        minYear: Int?,
        maxYear: Int?,
        minRating: Float?
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun setQuerySorting(
        sortBy: MovieRepository.SortType,
        sortOrder: String
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun queryMovies() {
        TODO("Not yet implemented")
    }

    override suspend fun getMovieDetails(id: String): MovieRepository.MovieDetails {
        TODO("Not yet implemented")
    }

    override suspend fun getGenres(): List<MovieRepository.Genre> {
        TODO("Not yet implemented")
    }
}