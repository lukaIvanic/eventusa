package com.example.eventusa.screens.login.model

import com.fasterxml.jackson.annotation.JsonProperty

// {"Item1":0,"Item2":{"Id":16,"Name":"Luka","Surname":null,"Email":""}}
data class LoginResponse(
    @JsonProperty("Item1")
    val Item1: Int,
    @JsonProperty("Item2")
    val Item2: User
) {
}