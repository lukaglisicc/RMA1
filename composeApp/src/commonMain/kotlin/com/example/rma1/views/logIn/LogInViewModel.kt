package com.example.rma1.views.logIn

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rma1.auth.AuthManager
import com.example.rma1.movies.network.LogInInfo
import com.example.rma1.networking.auth.NetworkAuthApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LogInViewModel(
    private val authApi: NetworkAuthApi,
    private val authManager: AuthManager,
): ViewModel() {

    private val _state = MutableStateFlow(LogInContract.UiState())

    val state = _state.asStateFlow()

    private fun setState(reducer: LogInContract.UiState.() -> LogInContract.UiState){
        _state.getAndUpdate(reducer)
    }

    private val events = MutableSharedFlow<LogInContract.UiEvent>()
    fun setEvent(event: LogInContract.UiEvent){
        viewModelScope.launch { events.emit(event) }
    }

    init {
        observeEvents()
    }

    private fun observeEvents(){
        viewModelScope.launch {
            events.collect { event ->
                when(event){
                    is LogInContract.UiEvent.LogIn -> {
                        if(checkInput(event.username, event.password)){
                            setState { copy(isLoading = true) }
                            withContext(Dispatchers.IO){
                                runCatching {
                                    authApi.logIn(
                                        LogInInfo(
                                            username = event.username,
                                            password = event.password,
                                        )
                                    )
                                }.fold(
                                    onSuccess = {authToken ->
                                        authManager.logIn(authToken)
                                        setState { copy(isLoading = false) }
                                    },
                                    onFailure = {error ->
                                        setState { copy(
                                            isLoading = false,
                                            usernameError = "",
                                            passwordError = "Invalid username or password",
                                        ) }
                                    },
                                )
                            }
                        }
                    }

                    LogInContract.UiEvent.PasswordUpdate -> setState { copy(passwordError = null) }
                    LogInContract.UiEvent.UsernameUpdate -> setState { copy(usernameError = null) }
                }
            }
        }
    }

    private fun checkInput(username: String, password: String) : Boolean{

        setState {
            copy(
                usernameError = null,
                passwordError = null,
            )
        }

        var isValid = true

        if (username.isBlank()){
            setState { copy(usernameError = "Username is required!") }
            isValid = false
        }
        if (password.isBlank()){
            setState { copy(passwordError = "Password is required!") }
            isValid = false
        }
        return isValid
    }
}