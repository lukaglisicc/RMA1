package com.example.rma1.movies

import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query


interface MovieApi {

    @GET("movies")
    suspend fun getMovies(
        @Query("page_size") pageSize: Int = 30,
        @Query("sort_by") sortBy: String = "imdb_rating",
        @Query("sort_order") sortOrder: String = "desc",
        @Query("genre_id") genreId: Int? = null,
        @Query("query") query: String? = null,
        @Query("min_year") minYear: Int? = null,
        @Query("max_year") maxYear: Int? = null,
        @Query("min_rating") minRating: Float? = null
    ): MovieResponse

    @GET("movies/{id}/cast")
    suspend fun getMovieCast(
        @Path("id") id: String,
        @Query("page_size") pageSize: Int = 10
    ): CastResponse

    @GET("movies/{id}")
    suspend fun getMovieDetails(
        @Path("id") id: String
    ): MovieDetails

    @GET("genres")
    suspend fun getGenres(): List<Genre>

    @GET("config")
    suspend fun getImageConfig(): List<ConfigPair>

    @GET("movies/{id}/images?type=backdrop")
    suspend fun getMovieImages(
        @Path("id") id: String
    ): Backdrops

    @GET("movies/{id}/videos?type=Trailer")
    suspend fun getMovieTrailers(
        @Path("id") id: String
    ): List<Trailer>
}