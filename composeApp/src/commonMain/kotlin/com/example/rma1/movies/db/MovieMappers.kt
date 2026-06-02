package com.example.rma1.movies.db

import com.example.rma1.movies.MovieRepository
import com.example.rma1.movies.db.entities.CastEntity
import com.example.rma1.movies.db.entities.GenreEntity
import com.example.rma1.movies.db.entities.MovieDetailsEntity
import com.example.rma1.movies.db.entities.MovieDetailsFull
import com.example.rma1.movies.db.entities.MovieEntity
import com.example.rma1.movies.db.entities.MovieGenreCrossRef
import com.example.rma1.movies.db.entities.MovieWithGenres
import com.example.rma1.movies.network.Cast
import com.example.rma1.movies.network.Genre
import com.example.rma1.movies.network.Movie
import com.example.rma1.movies.network.MovieDetails

fun MovieWithGenres.toRepositoryMovie() : MovieRepository.Movie {
    return MovieRepository.Movie(
        id = movie.id,
        title = movie.title,
        year = movie.year,
        rating = movie.rating,
        votes = movie.votes,
        genres = genres.map { it.toRepositoryGenre() },
        posterPath = movie.posterPath,
    )
}

fun GenreEntity.toRepositoryGenre() : MovieRepository.Genre {
    return MovieRepository.Genre(
        id = id,
        name = name,
    )
}

fun Genre.toGenreEntity() : GenreEntity {
    return GenreEntity(
        id = id,
        name = name,
    )
}

fun Movie.toMovieEntity() : MovieEntity {
    return MovieEntity(
        id = id,
        title = title,
        year = year,
        rating = rating,
        votes = votes,
        posterPath = posterPath,
    )
}

fun Movie.toMoviesGenres() : List<MovieGenreCrossRef> {
    val list = mutableListOf<MovieGenreCrossRef>()

    for (genre in genres){
        list.add(MovieGenreCrossRef(
            movieId = id,
            genreId = genre.id,
        ))
    }

    return list
}

fun MovieDetails.toMovieDetailsEntity() : MovieDetailsEntity {
    return MovieDetailsEntity(
        movieId = id,
        title = title,
        year = year,
        desc = desc,
        budget = budget,
        revenue = revenue,
        languageCode = languageCode,
        popularity = popularity,
        imdbRating = imdbRating,
        posterPath = posterPath,
        backdropPath = backdropPath,
        trailerPath = "",
    )
}

fun MovieDetailsFull.toRepositoryMovieDetails() : MovieRepository.MovieDetails {
    return MovieRepository.MovieDetails(
        id = movieDetails.movieId,
        title = movieDetails.title,
        year = movieDetails.year,
        desc = movieDetails.desc,
        budget = movieDetails.budget,
        revenue = movieDetails.revenue,
        languageCode = movieDetails.languageCode,
        popularity = movieDetails.popularity,
        imdbRating = movieDetails.imdbRating,
        posterPath = movieDetails.posterPath,
        backdropPath = movieDetails.backdropPath,
        genres = genres.map { it.toRepositoryGenre() },
        trailerPath = movieDetails.trailerPath,
        imagePaths = imagePaths.map { it.path },
        cast = cast.map { it.toRepositoryCast() },
    )
}

fun CastEntity.toRepositoryCast() : MovieRepository.Cast {
    return MovieRepository.Cast(
        name = name,
        profilePath = profilePath,
    )
}

fun Filters.toRepositoryFilters() : MovieRepository.Filters {
    return MovieRepository.Filters(
        genreId = genreId,
        query = query,
        minYear = minYear,
        maxYear = maxYear,
        minRating = minRating,
        sortType = sortType.toRepositorySortType(),
    )
}

fun SortType.toRepositorySortType() : MovieRepository.SortType {
    return when(this) {
        SortType.RATING -> MovieRepository.SortType.RATING
        SortType.YEAR -> MovieRepository.SortType.YEAR
        SortType.TITLE -> MovieRepository.SortType.TITLE
        SortType.POPULARITY -> MovieRepository.SortType.POPULARITY
    }
}

fun MovieRepository.Filters.toDatabaseFilters() : Filters {
    return Filters(
        genreId = genreId,
        query = query,
        minYear = minYear,
        maxYear = maxYear,
        minRating = minRating,
        sortType = sortType.toDatabaseSortType(),
    )
}

fun MovieRepository.SortType.toDatabaseSortType() : SortType {
    return when(this) {
        MovieRepository.SortType.RATING -> SortType.RATING
        MovieRepository.SortType.YEAR -> SortType.YEAR
        MovieRepository.SortType.TITLE -> SortType.TITLE
        MovieRepository.SortType.POPULARITY -> SortType.POPULARITY
    }
}

fun Cast.toCastEntity() : CastEntity {
    return CastEntity(
        id = id,
        name = name,
        profilePath = profilePath ?: "",
    )
}