package com.example.eventusa.screens.login.model

import com.fasterxml.jackson.annotation.JsonProperty

data class User(
    val Id: Int? = null,
    @JsonProperty("Name")
    val Name: String? = null,
    @JsonProperty("Surname")
    val Surname: String? = null,
    @JsonProperty("Email")
    val Email: String? = null,
) {
}