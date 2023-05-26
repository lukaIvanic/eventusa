package com.example.eventusa.screens.login.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fasterxml.jackson.annotation.JsonProperty

@Entity
data class User(

    @PrimaryKey(autoGenerate = true)
    val userId: Int = 0,
    val displayName: String = "",
    @JsonProperty("Name")
    val username: String = "",
    @JsonProperty("Pass")
    val pass: String = ""

    )