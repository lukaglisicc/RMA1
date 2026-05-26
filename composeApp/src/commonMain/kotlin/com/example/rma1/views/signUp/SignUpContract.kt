package com.example.rma1.views.signUp

interface SignUpContract {

    data class UiState(
        val usernameError: String? = null,
        val passwordError: String? = null,
        val fullNameError: String? = null,
        val isLoading: Boolean = false,
        val error: Throwable? = null
    )


    sealed class UiEvent {
        data class SignUp(
            val username: String,
            val fullName: String,
            val password: String,
        ) : UiEvent()

        data object UsernameUpdate : UiEvent()
        data object FullNameUpdate : UiEvent()
        data object PasswordUpdate : UiEvent()
    }
}