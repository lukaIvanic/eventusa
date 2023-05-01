package com.example.eventusa.caching.room.typeconverters

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.example.eventusa.screens.login.model.User
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@ProvidedTypeConverter
class UsersListConverter {

    @TypeConverter
    fun usersListToString(usersList: List<User>): String {
        return Gson().toJson(usersList)
    }


    @TypeConverter
    fun stringToUsersList(listJson: String): List<User> {
        val type = object: TypeToken<List<User>>() {}.type
        return Gson().fromJson(listJson, type)
    }

}