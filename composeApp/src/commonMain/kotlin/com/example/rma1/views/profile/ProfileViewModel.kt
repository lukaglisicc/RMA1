package com.example.rma1.views.profile

import androidx.lifecycle.ViewModel
import com.example.rma1.auth.AuthManager
import com.example.rma1.movies.MovieRepository

class ProfileViewModel(
    private val movieRepository: MovieRepository,
    private val authManager: AuthManager,
) : ViewModel() {

}