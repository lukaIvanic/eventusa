package com.example.eventusa.screens.login.model.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("users_table")
data class RoomUser(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val username: String,
    val password: String
) {
}