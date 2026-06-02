package com.example.rma1.movies.db

import com.example.rma1.movies.MovieRepository
import com.example.rma1.movies.db.entities.FavoritesEntity
import com.example.rma1.movies.db.entities.WatchlistEntity
import com.example.rma1.movies.network.ConfigPair
import com.example.rma1.movies.network.ImageType
import com.example.rma1.movies.network.MovieResponse
import com.example.rma1.movies.network.NetworkMovieApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class DatabaseMovieRepository(
    private val appDatabase: AppDatabase,
    private val movieApi: NetworkMovieApi,
) : MovieRepository {

    private val _filters = MutableStateFlow(Filters())

    private val _filtersWatchlist = MutableStateFlow(Filters())

    private val _filtersFavorites = MutableStateFlow(Filters())

    private var config: List<ConfigPair>? = null



    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeMovies(): Flow<List<MovieRepository.Movie>> =
        _filters.flatMapLatest { filters ->
            appDatabase.movieDao().observeMovies(buildMovieQuery(filters))
        }
            .distinctUntilChanged()
            .map { value -> value.map { it.toRepositoryMovie() } }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeWatchlist(): Flow<List<MovieRepository.Movie>> =
        _filtersWatchlist.flatMapLatest { filters ->
            appDatabase.movieDao().observeWatchlist(buildWatchlistQuery(filters))
        }
            .distinctUntilChanged()
            .map { value -> value.map { it.toRepositoryMovie() } }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeFavorites(): Flow<List<MovieRepository.Movie>> =
        _filtersFavorites.flatMapLatest { filters ->
            appDatabase.movieDao().observeFavorites(buildFavoritesQuery(filters))
        }
            .distinctUntilChanged()
            .map { value -> value.map { it.toRepositoryMovie() } }


    override fun observeMovieDetails(id: String): Flow<MovieRepository.MovieDetails?> =
        appDatabase.movieDao().observeMovieDetails(id)
            .map{ value -> value?.toRepositoryMovieDetails()}

    override fun observeMovieCount(): Flow<Int> =
        appDatabase.movieDao().observeMovieCount()

    override fun observeWatchlistCount(): Flow<Int> =
        appDatabase.movieDao().observeWatchlistCount()

    override fun observeFavoritesCount(): Flow<Int> =
        appDatabase.movieDao().observeFavoritesCount()

    override suspend fun setFilters(filters: MovieRepository.Filters) {
        _filters.update { filters.toDatabaseFilters() }
        queryMovies()
    }

    override suspend fun setWatchlistFilters(filters: MovieRepository.Filters) {
        _filtersWatchlist.update { filters.toDatabaseFilters() }
        queryMovies()
    }

    override suspend fun setFavoritesFilters(filters: MovieRepository.Filters) {
        _filtersFavorites.update { filters.toDatabaseFilters() }
        queryMovies()
    }

    override fun observeFilters(): Flow<MovieRepository.Filters> {
        return _filters.asStateFlow().map { it.toRepositoryFilters() }
    }

    override fun observeWatchlistFilters(): Flow<MovieRepository.Filters> {
        return _filtersWatchlist.asStateFlow().map { it.toRepositoryFilters() }
    }

    override fun observeFavoritesFilters(): Flow<MovieRepository.Filters> {
        return _filtersFavorites.asStateFlow().map { it.toRepositoryFilters() }
    }


    override suspend fun queryMovies() {
            val response = movieApi.getMovies(_filters.value)
            appDatabase.movieDao().upsertMovies(response.items
                .map {
                    it.toMovieEntity().copy(
                    posterPath = getImageUrl(it.posterPath, 1),
                ) }
            )
            response.items.forEach { movie ->
                appDatabase.movieDao().upsertGenres(movie.genres.map { it.toGenreEntity() })
                appDatabase.movieDao().upsertMoviesGenres(movie.toMoviesGenres())
            }
    }

    override suspend fun syncWatchlist() {
        val movies = movieApi.getWatchlist()
        appDatabase.movieDao().replaceWatchlist(
            movies.map { WatchlistEntity(it.id) }
        )
    }

    override suspend fun syncFavorites() {
        val movies = movieApi.getFavorites()
        appDatabase.movieDao().replaceFavorites(
            movies.map { FavoritesEntity(it.id) }
        )
    }

    override suspend fun refreshMovieDetails(id: String) {
        val details = movieApi.getMovieDetails(id)
        appDatabase.movieDao().upsertMovieDetails(
            details
                .toMovieDetailsEntity()
                .let{
                    it.copy(
                        posterPath = getImageUrl(it.posterPath, 2),
                        backdropPath = getImageUrl(it.backdropPath, 2, ImageType.BACKDROP),
                        trailerPath = movieApi.getMovieTrailers(it.movieId)[0].key,
                    )
                }
        )
        val cast = movieApi.getMovieCast(id).items.map {
            it.copy(
                profilePath = getImageUrl(it.profilePath, 1, ImageType.PROFILE)
            ).toCastEntity()
        }
        appDatabase.movieDao().upsertCast(cast)
    }

    override suspend fun getGenres(): List<MovieRepository.Genre> =
        appDatabase.movieDao().getGenres().map { it.toRepositoryGenre() }

    override suspend fun addToWatchlist(id: String) {
        movieApi.addToWatchlist(id)
        syncWatchlist()
    }

    override suspend fun addToFavorites(id: String) {
        movieApi.addFavorite(id)
        syncFavorites()
    }

    override suspend fun removeFromWatchlist(id: String) {
        movieApi.deleteFromWatchlist(id)
        syncWatchlist()
    }

    override suspend fun removeFromFavorites(id: String) {
        movieApi.deleteFavorite(id)
        syncFavorites()
    }

    override suspend fun observeIsInWatchlist(id: String): Flow<Boolean> {
        return appDatabase.movieDao().observeIsInWatchlist(id)
    }

    override suspend fun observeIsInFavorites(id: String): Flow<Boolean> {
        return appDatabase.movieDao().observeIsInFavorites(id)
    }

    override suspend fun isInWatchlist(id: String): Boolean {
        return appDatabase.movieDao().isInWatchlist(id)
    }

    override suspend fun isInFavorites(id: String): Boolean {
        return appDatabase.movieDao().isInFavorites(id)
    }

    override suspend fun clearFavorites() {
        appDatabase.movieDao().clearFavorites()
    }

    override suspend fun clearWatchlist() {
        appDatabase.movieDao().clearWatchlist()
    }

    override suspend fun getMovieCache(count: Int, onlyLoaded: Boolean): List<MovieRepository.MovieDetails> {
        if (onlyLoaded){
            return appDatabase.movieDao().getRandomMovieDetails(count).map { it.toRepositoryMovieDetails() }
        } else {
            val ids = appDatabase.movieDao().getRandomMovieIds(count)
            for(id in ids){
                refreshMovieDetails(id)
            }
            return appDatabase.movieDao().getMovieDetails(ids).map { it.toRepositoryMovieDetails() }
        }
    }

    private suspend fun getImageUrl(path: String?, quality: Int, imageType: ImageType = ImageType.POSTER): String {
        path ?: return ""

        config = config ?: movieApi.getImageConfig()

        val cfg = config ?: error("Config not loaded")


        val baseUrl = cfg[0].value
        val sizeList = cfg[imageType.id].value.split(",")

        val size = sizeList.getOrNull(quality)
            ?: sizeList.first()


        return "$baseUrl$size$path"
    }

}

private suspend fun NetworkMovieApi.getMovies(filters: Filters): MovieResponse {
    return getMovies(
        pageSize = 30,
        sortBy = mapSort(filters.sortType),
        sortOrder = mapSortOrder(filters.sortType),
        genreId = filters.genreId,
        query = filters.query,
        minYear = filters.minYear,
        maxYear = filters.maxYear,
        minRating = filters.minRating,
    )
}

private fun mapSort(sortType: SortType) : String{
    return when(sortType){
        SortType.RATING -> "imdb_rating"
        SortType.POPULARITY -> "popularity"
        SortType.YEAR -> "year"
        SortType.TITLE -> "title"
    }
}

private fun mapSortOrder(sortType: SortType) : String{
    return when(sortType){
        SortType.RATING -> "desc"
        SortType.POPULARITY -> "desc"
        SortType.YEAR -> "desc"
        SortType.TITLE -> "asc"
    }
}