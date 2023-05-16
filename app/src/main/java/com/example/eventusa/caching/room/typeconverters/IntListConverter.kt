package com.example.eventusa.caching.room.typeconverters

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.example.eventusa.screens.login.model.User
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@ProvidedTypeConverter
class IntListConverter {

    @TypeConverter
    fun intListToString(usersList: List<Int>): String {
        return Gson().toJson(usersList)
    }


    @TypeConverter
    fun stringToIntList(listJson: String): List<Int> {
        val type = object: TypeToken<List<User>>() {}.type
        return Gson().fromJson(listJson, type)
    }

}