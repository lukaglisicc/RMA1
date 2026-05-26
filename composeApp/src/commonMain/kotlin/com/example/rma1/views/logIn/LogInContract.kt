package com.example.rma1.views.logIn



interface LogInContract {

    data class UiState(
        val usernameError: String? = null,
        val passwordError: String? = null,
        val isLoading: Boolean = false,
        val error: Throwable? = null
    )


    sealed class UiEvent {
        data class LogIn(
            val username: String,
            val password: String,
        ) : UiEvent()

        data object UsernameUpdate : UiEvent()
        data object PasswordUpdate : UiEvent()
    }

}