package com.example.eventusa.caching.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.eventusa.caching.room.daos.EventsDao
import com.example.eventusa.caching.room.daos.NotifsDao
import com.example.eventusa.caching.room.daos.UsersDao
import com.example.eventusa.caching.room.extraentities.EventNotification
import com.example.eventusa.caching.room.typeconverters.LocalDateTimeConverter
import com.example.eventusa.caching.room.typeconverters.UsersListConverter
import com.example.eventusa.screens.events.data.RINetEvent
import com.example.eventusa.screens.login.model.User
import com.example.eventusa.screens.login.model.room.RoomUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.time.LocalDateTime

@Database(version = 10, entities = [RINetEvent::class, RoomUser::class, EventNotification::class, User::class])
@TypeConverters(LocalDateTimeConverter::class, UsersListConverter::class)
abstract class EventusaDatabase : RoomDatabase() {

    abstract fun getEventsDao(): EventsDao
    abstract fun getUsersDao(): UsersDao
    abstract fun getEventNotificationsDao(): NotifsDao


    companion object {

        private lateinit var INSTANCE: EventusaDatabase

        private var populateDbCallback = object : Callback() {
            override fun onOpen(db: SupportSQLiteDatabase) {
                populateDb()
                populateUsers()
                super.onOpen(db)
            }
        }

        fun setupInstance(context: Context) {
            if(this::INSTANCE.isInitialized) return

            INSTANCE = Room.databaseBuilder(
                context.applicationContext,
                EventusaDatabase::class.java,
                "eventusa_database"
            )
                .addCallback(populateDbCallback)
                .fallbackToDestructiveMigration()
                .addTypeConverter(LocalDateTimeConverter())
                .addTypeConverter(UsersListConverter())
                .build()

        }

        public fun getInstance(): EventusaDatabase {
            return INSTANCE
        }

        private fun populateUsers() {
            CoroutineScope(Dispatchers.IO + Job()).launch {

                if (!INSTANCE.getUsersDao().isEmpty()) return@launch

                INSTANCE.getUsersDao().insert(
                    arrayListOf(
                        RoomUser(0, "Luka", "luka123"),
                        RoomUser(0, "Anja", "anja123"),
                        RoomUser(0, "Armando", "armando123"),
                        RoomUser(0, "Branko", "branko123"),
                        RoomUser(0, "Danijel", "danijel123"),
                        RoomUser(0, "Drasko", "drasko123"),
                        RoomUser(0, "Nevija", "nevija123"),
                        RoomUser(0, "Zvjezdana", "zvjezdana123"),
                        RoomUser(0, "David", "david123"),
                        RoomUser(0, "Marko", "marko123"),
                        RoomUser(0, "Ivo", "ivo123"),
                    )
                )

            }
        }


        private fun populateDb() {
            CoroutineScope(Dispatchers.IO + Job()).launch {

                if (INSTANCE.getEventsDao().getAllEvents().isNotEmpty()) return@launch

                INSTANCE.getEventsDao().insert(
                    arrayListOf(
                        RINetEvent(
                            eventId = 10,
                            title = "Sastanak",
                            startDateTime = LocalDateTime.now().plusMonths(0)
                                .plusDays(0)
                                .withHour(11)
                                .withMinute(30),
                            endDateTime = LocalDateTime.now().plusMonths(0)
                                .plusDays(0)
                                .withHour(12)
                                .withMinute(30),
                            location = "Ured",
                            description = "Neki description"
                        ),

                        RINetEvent(
                            eventId = 11,
                            title = "Nakon sastanka",
                            startDateTime = LocalDateTime.now().plusMonths(0)
                                .plusDays(0)
                                .withHour(12)
                                .withMinute(30),
                            endDateTime = LocalDateTime.now().plusMonths(0)
                                .plusDays(0)
                                .withHour(13)
                                .withMinute(30),
                            location = "Ured",
                            description = "Neki description",
                            isFirstInDate = false
                        ),

                        RINetEvent(
                            eventId = 12,
                            title = "Luka test",
                            startDateTime = LocalDateTime.now().plusMonths(0)
                                .plusDays(1)
                                .withHour(12)
                                .withMinute(30),
                            endDateTime = LocalDateTime.now().plusMonths(0)
                                .plusDays(1)
                                .withHour(13)
                                .withMinute(30),
                            location = "Ured",
                            description = "Neki description"
                        ),

                        RINetEvent(
                            eventId = 13,
                            title = "Luka test",
                            startDateTime = LocalDateTime.now().plusMonths(0)
                                .plusDays(7)
                                .withHour(18)
                                .withMinute(0),
                            endDateTime = LocalDateTime.now().plusMonths(0)
                                .plusDays(7)
                                .withHour(18)
                                .withMinute(0),
                            location = "Ured",
                            description = "Neki description"
                        ),

                        )
                )
            }
        }


    }


}