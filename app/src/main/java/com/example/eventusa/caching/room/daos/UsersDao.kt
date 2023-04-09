package com.example.eventusa.caching.room.daos

import androidx.room.*
import com.example.eventusa.screens.login.model.room.RoomUser

@Dao
interface UsersDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(roomUsers: List<RoomUser>)

    @Query("SELECT * FROM users_table WHERE username= :username AND password= :password LIMIT 1")
    suspend fun getRoomUser(username: String, password: String): RoomUser?

    @Query("SELECT (SELECT COUNT(*) FROM users_table) == 0")
    suspend fun isEmpty(): Boolean

}