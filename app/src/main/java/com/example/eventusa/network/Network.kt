package com.example.eventusa.network

import com.example.eventusa.network.RequestType.GET
import com.example.eventusa.network.RequestType.POST
import com.example.eventusa.screens.login.model.LoginRequest
import com.example.eventusa.screens.login.model.LoginResponse
import com.example.eventusa.screens.events.data.RINetEvent
import com.example.eventusa.utils.jsonutils.JsonUtils
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody

object Network {

    private val baseUrl = "https://eventusamobile-production-api.azurewebsites.net"

    private val CREATE_EVENT = "/Event/new-event"
    private val READ_EVENTS = "/Event/event-list"
    private val UPDATE_EVENT = "/Event/update-event"
    private val DELETE_EVENT = "/Event/delete-event"
    private val LOGIN_PATH = "/User/login"

    fun attemptLogin(loginRequest: LoginRequest): ResultOf<LoginResponse> {
        try {
            val loginRequestJson = JsonUtils.toJson(loginRequest)
            val responseJson = NetworkCore.sendRequest(LOGIN_PATH, loginRequestJson)

            val loginResponse: LoginResponse = JsonUtils.fromJsonToT(responseJson)
            return ResultOf.Success(loginResponse)

        } catch (e: Exception) {
            return ResultOf.Error(e)
        }

    }

    fun insertEvent(rinetEvent: RINetEvent): ResultOf<Boolean> {


        try {
            val eventJson = JsonUtils.toJson(rinetEvent.copy(eventId = 0))
            val id = NetworkCore.sendRequest(CREATE_EVENT, eventJson)
            if (id.isNotEmpty()) {
                return ResultOf.Success(true)
            } else {
                return ResultOf.Error(Exception("Server didn't accept event"))
            }
        } catch (e: Exception) {
            return ResultOf.Error(e)
        }


    }


    fun getEvents(): MutableList<RINetEvent> {

        val eventsJson = NetworkCore.sendRequest(READ_EVENTS)

        return JsonUtils.fromJsonToList(eventsJson)

    }


    fun updateEvent(rinetEvent: RINetEvent): ResultOf<Boolean> {


        try {
            val eventJson = JsonUtils.toJson(rinetEvent)
            val id = NetworkCore.sendRequest(UPDATE_EVENT, eventJson)
            if (id == "true") {
                return ResultOf.Success(true)
            } else {
                return ResultOf.Error(Exception("Server didn't accept event"))
            }
        } catch (e: Exception) {
            return ResultOf.Error(e)
        }

    }

    fun deleteEvent(eventId: Int): ResultOf<Boolean> {

        try {
            val responseDelete = NetworkCore.sendRequest("$DELETE_EVENT?id=$eventId")
            if (responseDelete == "true") {
                return ResultOf.Success(true)
            } else {
                return ResultOf.Error(Exception("Was not able to delete event."))
            }
        } catch (e: Exception) {
            return ResultOf.Error(Exception(e))
        }

    }


    private object NetworkCore {
        fun sendRequest(urlPath: String, requestBodyString: String = ""): String {

            val requestType: RequestType =
                getRequestMethodForPath(urlPath)
                    ?: throw Exception("Request method not set.")


            val client: OkHttpClient = OkHttpClient().newBuilder().build()
            val mediaType: MediaType? = MediaType.parse("application/json")

            var requestBody: RequestBody? =
                if (requestType === POST) RequestBody.create(mediaType, requestBodyString) else null

            val request: Request = Request.Builder()
                .url(baseUrl + urlPath)
                .method(requestType.label, requestBody)
                .addHeader("accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .build()

            val response: okhttp3.Response = client.newCall(request).execute()

            return response.body()?.string() ?: throw Exception("Recieved empty data")
        }

        private fun getRequestMethodForPath(path: String): RequestType? {
            if (path.contains(DELETE_EVENT)) return POST
            when (path) {
                READ_EVENTS -> return GET
                CREATE_EVENT, UPDATE_EVENT, LOGIN_PATH -> return POST
            }
            return null
        }
    }


}

enum class RequestType(val label: String) {
    GET("GET"), POST("POST");
}
