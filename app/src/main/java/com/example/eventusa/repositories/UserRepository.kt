package com.example.eventusa.repositories

import com.example.eventusa.caching.sharedprefs.LocalStorageManager
import com.example.eventusa.network.Network
import com.example.eventusa.network.ResultOf
import com.example.eventusa.screens.login.model.LoginRequest
import com.example.eventusa.screens.login.model.LoginResponse
import com.example.eventusa.screens.login.model.ResponseCodes
import com.example.eventusa.screens.login.model.User

class UserRepository {

    var user: User? = null
    private var alreadyLoggedIn = false
    var lastSuccessLoginRequest: LoginRequest? = null


    fun isUserAlreadyLoggedIn(): Boolean {
        return LocalStorageManager.readRememberMe()
    }
    fun setUserIsLoggedIn() {
        alreadyLoggedIn = true
    }

    fun setUserisLoggedOut() {
        alreadyLoggedIn = false
    }

    fun attemptLogin(loginRequest: LoginRequest): ResultOf<LoginResponse> {

        if (alreadyLoggedIn && loginRequest == lastSuccessLoginRequest) {
            return ResultOf.Success(
                LoginResponse(
                    Item1 = ResponseCodes.SUCCESS.value,
                    Item2 = User(name = loginRequest.username)
                )
            )
        }


        val resultOf = Network.attemptLogin(loginRequest)
        if (resultOf is ResultOf.Success && resultOf.data.Item1 == 0) {
            user = resultOf.data.Item2
        }

        if (resultOf is ResultOf.Success) {
            if (ResponseCodes.get(resultOf.data.Item1) == ResponseCodes.SUCCESS) {
                alreadyLoggedIn = true
                lastSuccessLoginRequest = loginRequest
            }
        }

        return resultOf
    }


}