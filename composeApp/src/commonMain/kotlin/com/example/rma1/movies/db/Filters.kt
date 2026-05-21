package com.example.rma1.movies.db

data class Filters(
    val genreId: Int? = null,
    val query: String? = null,
    val minYear: Int? = 1920,
    val maxYear: Int? = 2025,
    val minRating: Float? = 0F,
    val sortType: SortType = SortType.RATING,
)

enum class SortType {
    RATING,
    YEAR,
    TITLE,
    POPULARITY,
}

