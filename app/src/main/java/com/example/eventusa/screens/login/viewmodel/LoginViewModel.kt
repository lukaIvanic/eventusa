package com.example.eventusa.screens.login.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.eventusa.network.ResultOf
import com.example.eventusa.repositories.LoggedInState
import com.example.eventusa.repositories.UserRepository
import com.example.eventusa.screens.login.model.ResponseCodes.*
import com.example.eventusa.screens.login.model.User
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class LoginViewModel(val userRepository: UserRepository) : ViewModel() {

    private val _loginStateFlow = MutableSharedFlow<ResultOf<User>>()
    val loginState = _loginStateFlow.asSharedFlow()

    init {
        viewModelScope.launch {
            userRepository.loginStateFlow.collect { loggedInState ->

                    if (loggedInState is LoggedInState.Idle) return@collect


                    when (loggedInState) {
                        is LoggedInState.Error -> {
                            _loginStateFlow.emit(ResultOf.Error(loggedInState.exception))
                        }
                        is LoggedInState.Loading -> {
                            _loginStateFlow.emit(  ResultOf.Loading)
                        }
                        is LoggedInState.Success -> {
                            _loginStateFlow.emit(ResultOf.Success(loggedInState.user))
                        }
                        else -> {}
                    }

            }
        }
    }

    fun login(username: String, password: String) {

        userRepository.attemptLogin(User(username = username, pass = password))
    }

}

class LoginViewModelFactory(val userRepository: UserRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(userRepository) as T
        }

        throw java.lang.IllegalArgumentException("UNKNOWN VIEW MODEL CLASS")
    }


}