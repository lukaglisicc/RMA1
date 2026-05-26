package com.example.rma1.di

import com.example.rma1.views.favorites.FavoritesViewModel
import com.example.rma1.views.filters.FiltersViewModel
import com.example.rma1.views.logIn.LogInViewModel
import com.example.rma1.views.movieDetails.MovieDetailsViewModel
import com.example.rma1.views.movieList.MovieListViewModel
import com.example.rma1.views.profile.ProfileViewModel
import com.example.rma1.views.quiz.QuizViewModel
import com.example.rma1.views.signUp.SignUpViewModel
import com.example.rma1.views.watchlist.WatchlistViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val uiModule = module {
    viewModelOf(::MovieListViewModel)
    viewModelOf(::MovieDetailsViewModel)
    viewModelOf(::FiltersViewModel)
    viewModelOf(::FavoritesViewModel)
    viewModelOf(::ProfileViewModel)
    viewModelOf(::QuizViewModel)
    viewModelOf(::WatchlistViewModel)
    viewModelOf(::LogInViewModel)
    viewModelOf(::SignUpViewModel)
}