package com.example.eventusa.screens.login.model

import com.fasterxml.jackson.annotation.JsonProperty

data class LoginRequest(
    @JsonProperty("username")
    val username: String,
    @JsonProperty("pass")
    val pass: String
)