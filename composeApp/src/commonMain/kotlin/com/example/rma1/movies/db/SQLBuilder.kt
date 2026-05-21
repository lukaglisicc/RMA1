package com.example.rma1.movies.db

import androidx.room.RoomRawQuery

fun buildMovieQuery(
    filters: Filters,
): RoomRawQuery {

    val sql = StringBuilder(
        """
        SELECT DISTINCT movies.*
        FROM movies
        """.trimIndent()
    )

    val bindings = mutableListOf<Any>()

    if (filters.genreId != null) {
        sql.append(
            """
            
            INNER JOIN movies_genres mg
                ON movies.id = mg.movieId
            """.trimIndent()
        )
    }

    sql.append("\nWHERE 1 = 1")

    filters.minRating?.let {
        sql.append("\nAND rating >= ?")
        bindings += it
    }

    filters.minYear?.let {
        sql.append("\nAND year >= ?")
        bindings += it
    }

    filters.maxYear?.let {
        sql.append("\nAND year <= ?")
        bindings += it
    }

    filters.genreId?.let {
        sql.append("\nAND mg.genreId = ?")
        bindings += it
    }

    filters.query
        ?.takeIf { it.isNotBlank() }
        ?.let {
            sql.append("\nAND title LIKE ?")
            bindings += "%$it%"
        }

    val orderBy = when (filters.sortType) {
        SortType.RATING -> "rating DESC"
        SortType.YEAR -> "year DESC"
        SortType.TITLE -> "title ASC"
        SortType.POPULARITY -> "votes DESC"
    }

    sql.append("\nORDER BY $orderBy")

    return RoomRawQuery(
        sql = sql.toString(),
        onBindStatement = { stmt ->
            bindings.forEachIndexed { index, value ->
                when (value) {
                    is String -> stmt.bindText(index + 1, value)
                    is Int -> stmt.bindLong(index + 1, value.toLong())
                    is Long -> stmt.bindLong(index + 1, value)
                    is Float -> stmt.bindDouble(index + 1, value.toDouble())
                    is Double -> stmt.bindDouble(index + 1, value)
                    else -> error("Unsupported type: ${value::class}")
                }
            }
        }
    )
}