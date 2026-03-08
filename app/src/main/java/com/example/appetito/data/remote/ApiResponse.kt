package com.example.appetito.data.remote

import retrofit2.Response

sealed class ApiResponse<out T> {
    data class Success<out T>(val data: T) : ApiResponse<T>()
    data class Error(val code: Int, val message: String): ApiResponse<Nothing>(){
        fun formatMsg(): String {
            return "Error: $code - $message"
        }
    }

    data class Exception(val exception: kotlin.Exception): ApiResponse<Nothing>()
}

suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>): ApiResponse<T> {
    return try {
        val res = apiCall.invoke()
        if (res.isSuccessful) {
            val body = res.body()
            if (body != null) {
                ApiResponse.Success(body)
            } else {
                ApiResponse.Error(res.code(), "Empty response body")
            }
        } else {
            val errorMsg = try {
                val json = org.json.JSONObject(res.errorBody()?.string() ?: "")
                json.optString("error", "Unknown error (${res.code()})")
            } catch (e: Exception) {
                "Unknown error (${res.code()})"
            }
            ApiResponse.Error(res.code(), errorMsg)
        }
    } catch (e: Exception) {
        ApiResponse.Exception(e)
    }
}