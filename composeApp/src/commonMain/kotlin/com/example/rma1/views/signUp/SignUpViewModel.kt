package com.example.rma1.views.signUp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rma1.auth.AuthManager
import com.example.rma1.movies.network.SignUpInfo
import com.example.rma1.networking.auth.NetworkAuthApi
import io.ktor.client.plugins.ResponseException
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SignUpViewModel(
    private val authApi: NetworkAuthApi,
    private val authManager: AuthManager,
): ViewModel() {

    private val _state = MutableStateFlow(SignUpContract.UiState())

    val state = _state.asStateFlow()

    private fun setState(reducer: SignUpContract.UiState.() -> SignUpContract.UiState){
        _state.getAndUpdate(reducer)
    }

    private val events = MutableSharedFlow<SignUpContract.UiEvent>()
    fun setEvent(event: SignUpContract.UiEvent){
        viewModelScope.launch { events.emit(event) }
    }

    init {
        observeEvents()
    }

    private fun observeEvents(){
        viewModelScope.launch {
            events.collect { event ->
                when(event){
                    is SignUpContract.UiEvent.SignUp -> {
                        if(checkInput(event.username,event.fullName, event.password)){
                            setState { copy(isLoading = true) }
                            withContext(Dispatchers.IO){
                                runCatching {
                                    authApi.signUp(
                                        SignUpInfo(
                                            username = event.username,
                                            password = event.password,
                                            name = event.fullName,
                                        )
                                    )
                                }.fold(
                                    onSuccess = {authToken ->
                                        authManager.logIn(authToken)
                                        setState { copy(isLoading = false) }
                                    },
                                    onFailure = {error ->
                                        when(error){
                                            is ResponseException -> {
                                                if(error.response.status == HttpStatusCode.Conflict){
                                                    setState { copy(
                                                        usernameError = "Username already in use",
                                                        isLoading = false,
                                                    ) }
                                                } else if (error.response.status == HttpStatusCode.BadRequest){
                                                    setState { copy(
                                                        fullNameError = "Invalid input",
                                                        usernameError = "Invalid input",
                                                        passwordError = "Invalid input",
                                                        isLoading = false,
                                                    ) }
                                                } else {
                                                    setState { copy(
                                                        error = error,
                                                        isLoading = false,
                                                    ) }
                                                }
                                            }

                                            else -> {
                                                setState { copy(error = error, isLoading = false) }
                                            }
                                        }
                                    },
                                )
                            }
                        }
                    }
                    is SignUpContract.UiEvent.FullNameUpdate -> setState { copy(fullNameError = null) }
                    is SignUpContract.UiEvent.PasswordUpdate -> setState { copy(passwordError = null) }
                    is SignUpContract.UiEvent.UsernameUpdate -> setState { copy(usernameError = null) }
                }
            }
        }
    }

    private fun checkInput(username: String, fullName: String, password: String) : Boolean{

        var isValid = true

        if (fullName.isBlank()) {
            setState {
                copy(fullNameError = "Full name is required!")
            }
            isValid = false
        }

        if (username.isBlank()) {
            setState {
                copy(usernameError = "Username is required!")
            }
            isValid = false
        } else if (username.length < 3) {
            setState {
                copy(usernameError = "Username must be at least 3 characters!")
            }
            isValid = false
        } else if (!username.matches(Regex("^[A-Za-z0-9_]+$"))) {
            setState {
                copy(
                    usernameError =
                        "Username may only contain letters, numbers and underscores!"
                )
            }
            isValid = false
        }

        if (password.isBlank()) {
            setState {
                copy(passwordError = "Password is required!")
            }
            isValid = false
        } else if (password.length < 8) {
            setState {
                copy(passwordError = "Password must be at least 8 characters!")
            }
            isValid = false
        }

        return isValid
    }

}