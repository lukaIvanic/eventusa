package com.example.eventusa.repositories

import com.example.eventusa.network.Network
import com.example.eventusa.network.ResultOf
import com.example.eventusa.screens.login.model.User
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class UserRepository {

    private val _loginStateFlow: MutableStateFlow<LoggedInState> =
        MutableStateFlow(LoggedInState.Idle)
    val loginStateFlow = _loginStateFlow.asStateFlow()

    var loggedInUser: User? = null

    private var cachedUsers: List<User>? = null

    fun attemptLogin(user: User) {

        CoroutineScope(Dispatchers.IO).launch {

            _loginStateFlow.value = LoggedInState.Loading

            val resultOfLogin = Network.attemptLogin(user)

            _loginStateFlow.value = when (resultOfLogin) {
                is ResultOf.Error -> LoggedInState.Error(resultOfLogin.exception)
                is ResultOf.Loading -> LoggedInState.Loading
                is ResultOf.Success -> {
                    loggedInUser = resultOfLogin.data
                    LoggedInState.Success(resultOfLogin.data)
                }
            }

            if(resultOfLogin is ResultOf.Success){
                delay(300)
                _loginStateFlow.value = LoggedInState.Idle
            }


        }

    }



    suspend fun getAllUsers(): ResultOf<List<User>> = withContext(Dispatchers.IO){

        if(cachedUsers != null) return@withContext ResultOf.Success(cachedUsers!!)

        val resultOfUsers = Network.getAllUsers()

        if(resultOfUsers is ResultOf.Success){
            cachedUsers = resultOfUsers.data
        }

        return@withContext resultOfUsers
    }

}

sealed class LoggedInState {
    object Idle : LoggedInState()
    object Loading : LoggedInState()
    data class Success(val user: User) : LoggedInState()
    data class Error(val exception: Exception) : LoggedInState()
}
