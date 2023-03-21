package com.example.eventusa.screens.login.model

import com.fasterxml.jackson.annotation.JsonProperty

data class User(
    val Id: Int?,
    @JsonProperty("Name")
    val Name: String?,
    @JsonProperty("Surname")
    val Surname: String?,
    @JsonProperty("Email")
    val Email: String?,
) {
}