package com.example.rma1.movies.db

import com.example.rma1.movies.MovieRepository
import com.example.rma1.movies.db.entities.GenreEntity
import com.example.rma1.movies.db.entities.MovieEntity
import com.example.rma1.movies.db.entities.MovieGenreCrossRef
import com.example.rma1.movies.db.entities.MovieWithGenres
import com.example.rma1.movies.network.Genre
import com.example.rma1.movies.network.Movie

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