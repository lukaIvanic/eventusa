package com.example.eventusa.screens.login.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.eventusa.network.ResultOf
import com.example.eventusa.repository.UserRepository
import com.example.eventusa.screens.login.model.LoginRequest
import com.example.eventusa.screens.login.model.LoginResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginViewModel(val userRepository: UserRepository) : ViewModel() {

    private val _loginStateFlow = MutableSharedFlow<ResultOf<LoginResponse>>()
    val loginState = _loginStateFlow.asSharedFlow()

    fun login(username: String, password: String) {


        viewModelScope.launch {
            withContext(Dispatchers.IO){

                _loginStateFlow.emit(ResultOf.Loading)

                val loginRequest = LoginRequest(username, password)
                val resultLogin: ResultOf<LoginResponse> = userRepository.attemptLogin(loginRequest)
                var uiResultLogin: ResultOf<LoginResponse>
                when (resultLogin){
                    is ResultOf.Error, is ResultOf.Loading -> uiResultLogin = resultLogin
                    is ResultOf.Success -> {
                        uiResultLogin = when(resultLogin.data.Item1){
                            0 -> {

                                resultLogin
                            }
                            -1 -> ResultOf.Error(Exception("Internal server error."))
                            -2 -> ResultOf.Error(Exception("Wrong credentials"))
                            else -> ResultOf.Error(Exception("Internal server error."))
                        }
                    }
                }

                _loginStateFlow.emit(uiResultLogin)
            }
        }



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