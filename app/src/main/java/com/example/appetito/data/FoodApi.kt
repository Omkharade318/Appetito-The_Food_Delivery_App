package com.example.appetito.data

import com.example.appetito.data.models.AuthResponse
import com.example.appetito.data.models.SignUpRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface FoodApi {
    @GET("/food") // /food is the endpoint of the API we are trying to access
    suspend fun getFood(): List<String>

    // request
    @POST("/auth/signup")
    suspend fun signUp(@Body request: SignUpRequest): AuthResponse

    // response

    // endpoint
}

