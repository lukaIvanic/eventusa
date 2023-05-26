package com.example.eventusa.repositories

import com.example.eventusa.network.Network
import com.example.eventusa.network.ResultOf
import com.example.eventusa.screens.login.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class UserRepository {

    private val _loginStateFlow: MutableSharedFlow<ResultOf<Unit>> =
        MutableSharedFlow()
    val loginStateFlow = _loginStateFlow.asSharedFlow()
    var loggedInUser: User? = null

    fun attemptLogin(user: User) {
        CoroutineScope(Dispatchers.IO).launch {
            _loginStateFlow.emit(ResultOf.Loading)
            val resultOfLogin = Network.attemptLogin(user)
            _loginStateFlow.emit(resultOfLogin)
        }
    }

    companion object {
        private val allUsers = arrayListOf(
            User(13, "Anja Štefan", "Anja", "Anja123"),
            User(14, "Branko Žuža", "Branko", "Branko123"),
            User(15, "Daniel Pajalić", "Daniel", "Daniel123"),
            User(16, "Marko Andrić", "Marko", "Marko123"),
            User(17, "David Pajo", "David", "David123"),
            User(18, "Ivo Opančar", "Ivo", "Ivo123"),
            User(19, "Luka Ivanić", "Luka", "Luka123"),
            User(20, "Draško Andrić", "Drasko", "Drasko123"),
            User(21, "Borna Budić", "Borna", "Borna123"),
            User(22, "Armando Šculac", "Armando", "Armando123"),
            User(23, "Nevija Samađić", "Nevija", "Nevija123"),
            User(24, "Zvjezdana Anić", "Zvjezdana", "Zvjezdana123")
        )

        fun getAllUsers(): List<User> {
            return allUsers
        }
    }



}