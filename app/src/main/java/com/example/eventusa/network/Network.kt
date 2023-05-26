package com.example.eventusa.network

import com.example.eventusa.exceptions.EventusaExceptions
import com.example.eventusa.exceptions.EventusaExceptions.*
import com.example.eventusa.exceptions.ExceptionResponse
import com.example.eventusa.network.Network.NetworkCore
import com.example.eventusa.network.RequestType.*
import com.example.eventusa.repositories.UserRepository
import com.example.eventusa.screens.events.data.RINetEvent
import com.example.eventusa.screens.login.model.User
import com.example.eventusa.utils.jsonutils.JsonUtils
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody

/**
 * Provides abstraction for network requests.
 * Inner object NetworkCore sets up boilerplate for network requests.
 * Other functions are specific api calls.
 * @see NetworkCore
 */
object Network {

    private val baseUrl = "https://eventusamobile-production-api.azurewebsites.net/api/"

    private val LOGIN_PATH = "Users/login"
    private val READ_USERS = "Users"
    private val CREATE_EVENT = "Events/create"
    private val READ_EVENTS = "Events"
    private val READ_ONE_EVENT = "Events"
    private val UPDATE_EVENT = "Events/update"
    private val DELETE_EVENT = "Events/delete"

    /**
     * Successful return marks successful login. Only indication of a failed authorization is an exception in the catch block.
     * LoginResponse contains a lot of meaningless data that should be removed from server.
     */
    fun attemptLogin(inputUser: User): ResultOf<Unit> {

        var responseJson = ""
        try {
            val inputUserJson = JsonUtils.toJson(inputUser)
            responseJson = NetworkCore.sendRequest(LOGIN_PATH, inputUserJson)

        } catch (e: Exception) {
            return NETWORK_EXCEPTION()
        }

        try {
            val errorResponse = JsonUtils.fromJsonToObject<ExceptionResponse>(responseJson)
            if (errorResponse.status == EventusaExceptions.getStatusCode(NOT_FOUND_EXCEPTION)) {
                return ResultOf.Error(Exception("User not found."))
            }
            return errorResponse.getException()
        } catch (ignore: Exception) {

        }


        return ResultOf.Success(Unit)


    }


    fun insertEvent(rinetEvent: RINetEvent): ResultOf<RINetEvent> {
        var eventReturnJson = ""
        try {
            val eventJson = JsonUtils.toJson(rinetEvent.copy(eventId = 0))
            eventReturnJson = NetworkCore.sendRequest(CREATE_EVENT, eventJson)
        } catch (e: Exception) {


            try {
                val errorResponse = JsonUtils.fromJsonToObject<ExceptionResponse>(eventReturnJson)
                return errorResponse.getException()
            } catch (ignore: Exception) {
            }

            return NETWORK_EXCEPTION(e.localizedMessage)
        }

        try {
            val returnedEvent = JsonUtils.fromJsonToObject<RINetEvent>(eventReturnJson)
            return ResultOf.Success(returnedEvent)
        } catch (e: Exception) {

            try {
                val errorResponse = JsonUtils.fromJsonToObject<ExceptionResponse>(eventReturnJson)
                return ResultOf.Error(Exception("Can't create an event that already ended"))
            } catch (ignore: Exception) {
            }

            return JSON_PARSE_EXCEPTION(e.localizedMessage)
        }

    }

    fun getEvent(eventId: Int): ResultOf<RINetEvent> {

        var eventJson = ""

        try {
            eventJson = NetworkCore.sendRequest("$READ_ONE_EVENT/$eventId")
            if (eventJson.isEmpty()) throw Exception()
        } catch (e: Exception) {
            return NETWORK_EXCEPTION(e.localizedMessage)
        }

        try {
            val event = JsonUtils.fromJsonToObject<RINetEvent>(eventJson)

            return ResultOf.Success(event)
        } catch (e: Exception) {
            return JSON_PARSE_EXCEPTION(e.localizedMessage)
        }


    }

    fun getEvents(): ResultOf<MutableList<RINetEvent>> {

        var eventsJson = ""

        try {
            eventsJson = NetworkCore.sendRequest(READ_EVENTS)
            if (eventsJson.isEmpty()) throw Exception()
        } catch (e: Exception) {
            return NETWORK_EXCEPTION(e.localizedMessage)
        }

        try {

            if (eventsJson.first() == '<') {
                return SERVER_DOWN()
            }

            val events = JsonUtils.fromJsonToList<RINetEvent>(eventsJson)

            return ResultOf.Success(parseUserIdsStringList(events) as MutableList<RINetEvent>)
        } catch (e: Exception) {
            return JSON_PARSE_EXCEPTION(e.localizedMessage)
        }


    }

    private fun parseUserIdsStringList(events: List<RINetEvent>): List<RINetEvent> {

        return events.mapIndexed { index, event ->

            val users = UserRepository.getAllUsers()

            var attendingUsers: MutableList<User> = ArrayList()

            if (event.userIdsStringList.isNullOrEmpty() || event.userIdsStringList == "null") return@mapIndexed event

            var userIdsList: List<Int>?

            try {
                userIdsList =
                    event.userIdsStringList?.split(",")?.map { stringId -> stringId.trim().toInt() }
            } catch (e: NumberFormatException) {
                return@mapIndexed event
            }


            users.forEach { user ->
                if (userIdsList?.contains(user.userId) == true) {
                    attendingUsers.add(user)
                }
            }

            return@mapIndexed event.copy(
                usersAttending = attendingUsers
            )
        }


    }


    fun updateEvent(rinetEvent: RINetEvent): ResultOf<Boolean> {


        try {
            val eventJson = JsonUtils.toJson(rinetEvent)
            val path = "$UPDATE_EVENT/${rinetEvent.eventId}"
            val responseJson =
                NetworkCore.sendRequest(path, eventJson)
            if (responseJson.isEmpty()) {
                return ResultOf.Success(true)
            } else {

                return try {
                    val errorResponse = JsonUtils.fromJsonToObject<ExceptionResponse>(responseJson)
                    errorResponse.getException()
                } catch (e: Exception) {
                    if (e.localizedMessage.isNullOrEmpty()) GENERAL_EXCEPTION() else ResultOf.Error(
                        e
                    )
                }

            }
        } catch (e: Exception) {
            return NETWORK_EXCEPTION(e.localizedMessage)
        }

    }

    fun deleteEvent(eventId: Int): ResultOf<Unit> {

        try {
            val responseDelete = NetworkCore.sendRequest("$DELETE_EVENT/$eventId")
            if (responseDelete.isEmpty()) {
                return ResultOf.Success(Unit)
            } else {

                return try {
                    val errorResponse =
                        JsonUtils.fromJsonToObject<ExceptionResponse>(responseDelete)
                    errorResponse.getException()
                } catch (e: Exception) {
                    GENERAL_EXCEPTION(e.localizedMessage)
                }

            }
        } catch (e: Exception) {
            return NETWORK_EXCEPTION(e.localizedMessage)
        }

    }

    /**
     * Abstraction from boilerplate request code.
     * Uses getRequestMethodForUrlPath method to adjust http request type.
     */
    private object NetworkCore {
        fun sendRequest(urlPath: String, requestBodyString: String = ""): String {

            val requestType: RequestType =
                getRequestMethodForUrlPath(urlPath)
                    ?: throw Exception("Request method not set.")


            val client: OkHttpClient = OkHttpClient().newBuilder().build()
            val mediaType: MediaType? = MediaType.parse("application/json")

            var requestBody: RequestBody? =
                if (requestType == POST || requestType == PUT) RequestBody.create(
                    mediaType,
                    requestBodyString
                ) else null

            val request: Request = Request.Builder()
                .url(baseUrl + urlPath)
                .method(requestType.label, requestBody)
                .addHeader("accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .build()

            val response: okhttp3.Response = client.newCall(request).execute()

            return response.body()?.string() ?: throw Exception("Recieved empty data")
        }

        private fun getRequestMethodForUrlPath(path: String): RequestType? {
            if (path.contains(DELETE_EVENT)) return DELETE
            if (path.contains(UPDATE_EVENT)) return PUT
            when (path) {
                READ_EVENTS, READ_USERS -> return GET
                CREATE_EVENT, LOGIN_PATH -> return POST
            }

            if (path.contains(READ_ONE_EVENT)) return GET
            return null
        }
    }


}

enum class RequestType(val label: String) {
    GET("GET"), POST("POST"), PUT("PUT"), DELETE("DELETE");
}
