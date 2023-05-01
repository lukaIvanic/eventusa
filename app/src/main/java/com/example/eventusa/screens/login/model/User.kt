package com.example.eventusa.screens.login.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fasterxml.jackson.annotation.JsonProperty

@Entity
data class User(

    @PrimaryKey(autoGenerate = true)
    val userId: Int = 0,

    @JsonProperty("Name")
    val name: String? = null,

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
        ).map { User(name = it) }

        fun getAllUsers(): List<User> {
            return allUsers
        }
    }

}