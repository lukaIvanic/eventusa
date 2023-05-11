package com.example.eventusa.network

import com.example.eventusa.network.Network.NetworkCore
import com.example.eventusa.network.RequestType.*
import com.example.eventusa.screens.events.data.RINetEvent
import com.example.eventusa.screens.login.model.LoginRequest
import com.example.eventusa.screens.login.model.LoginResponse
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

    private val baseUrl = "https://eventusabackendapptestservice.azurewebsites.net/api/"

    private val CREATE_EVENT = "Events/create"
    private val READ_EVENTS = "/Events"
    private val UPDATE_EVENT = "Events/update/"
    private val DELETE_EVENT = "/Events/delete"
    private val LOGIN_PATH = "/User/login"

    /**
     * Successful return marks successful login. Only indication of a failed authorization is an exception in the catch block.
     * LoginResponse contains a lot of meaningless data that should be removed from server.
     */
    fun attemptLogin(loginRequest: LoginRequest): ResultOf<LoginResponse> {
        try {
            val loginRequestJson = JsonUtils.toJson(loginRequest)
            val responseJson = NetworkCore.sendRequest(LOGIN_PATH, loginRequestJson)

            val loginResponse: LoginResponse = JsonUtils.fromJsonToObject(responseJson)
            return ResultOf.Success(loginResponse)

        } catch (e: Exception) {
            return ResultOf.Error(e)
        }

    }


    fun insertEvent(rinetEvent: RINetEvent): ResultOf<Boolean> {


        try {
            val eventJson = JsonUtils.toJson(rinetEvent.copy(eventId = 0))
            val id = NetworkCore.sendRequest(CREATE_EVENT, eventJson)
            return if (id.isNotEmpty()) {
                ResultOf.Success(true)
            } else {
                ResultOf.Error(Exception("Server didn't accept event"))
            }
        } catch (e: Exception) {
            return ResultOf.Error(e)
        }


    }

    /**
     * Returns events or empty body.
     * Because of the possibility of an empty body a json validity check is neccessary.
     */
    fun getEvents(): MutableList<RINetEvent> {

        val eventsJson = NetworkCore.sendRequest(READ_EVENTS)

        if (eventsJson.first() != '[' && eventsJson.first() != '{') {
            throw Exception(eventsJson)
        }

        return JsonUtils.fromJsonToList(eventsJson)

    }


    fun updateEvent(rinetEvent: RINetEvent): ResultOf<Boolean> {


        try {
            val eventJson = JsonUtils.toJson(rinetEvent)
            val id = NetworkCore.sendRequest("$UPDATE_EVENT/${rinetEvent.eventId}", eventJson)
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
            val responseDelete = NetworkCore.sendRequest("$DELETE_EVENT/$eventId")
            if (responseDelete.isEmpty()) {
                return ResultOf.Success(true)
            } else {
                return ResultOf.Error(Exception("Was not able to delete event."))
            }
        } catch (e: Exception) {
            return ResultOf.Error(Exception(e))
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
                if (requestType == POST || requestType == PUT) RequestBody.create(mediaType, requestBodyString) else null

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
            when (path) {
                READ_EVENTS -> return GET
                CREATE_EVENT, LOGIN_PATH -> return POST
                UPDATE_EVENT -> return PUT
            }
            return null
        }
    }


}

enum class RequestType(val label: String) {
    GET("GET"), POST("POST"), PUT("PUT"), DELETE("DELETE");
}
