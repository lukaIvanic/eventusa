package com.example.eventusa.repository

import com.example.eventusa.network.Network
import com.example.eventusa.network.ResultOf
import com.example.eventusa.screens.login.model.LoginRequest
import com.example.eventusa.screens.login.model.LoginResponse
import com.example.eventusa.screens.login.model.User

class UserRepository {

    var user: User? = null

    fun attemptLogin(loginRequest: LoginRequest): ResultOf<LoginResponse> {
        val resultOf = Network.attemptLogin(loginRequest)
        if(resultOf is ResultOf.Success && resultOf.data.Item1 == 0){
            user = resultOf.data.Item2
        }
        return resultOf
    }




}