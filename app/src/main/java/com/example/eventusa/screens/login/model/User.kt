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

    ) {

    companion object {

        private val allUsers = arrayListOf(
            "Luka Ivanic",
            "Anja Stefan",
            "Armando Sćulac",
            "Branko Kojić",
            "Branko Zuza",
            "Danijel Pajalic",
            "Draško Andrić",
            "Nevija",
            "Zvjezdana",
            "David Pajo",
            "Marko Andrić",
            "Ivo Opancar"
        ).map { User(displayName = it) }

        fun getAllUsers(): List<User> {
            return allUsers
        }
    }

}