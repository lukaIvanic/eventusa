package com.example.eventusa.screens.login.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(

    @PrimaryKey(autoGenerate = true)
    val userId: Int = 0,
    val displayName: String = "",
    val username: String = "",
    val pass: String = ""

    )