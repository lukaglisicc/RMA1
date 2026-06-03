package com.example.rma1.views.profile


interface ProfileContract {

    data class UiState(
        val username: String = "",
        val realName: String = "",
        val bestScore: Float = 0f,
        val quizCount: Int = 0,
        val favoritesCount: Int = 0,
        val watchlistCount: Int = 0,
        val isLoading: Boolean = false,
        val error: Throwable? = null,
    )


    sealed class UiEvent{
        data object LogOut: UiEvent()
    }
}