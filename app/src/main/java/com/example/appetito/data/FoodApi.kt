package com.example.appetito.data

import retrofit2.http.GET

interface FoodApi {
    @GET("/food") // /food is the endpoint of the API we are trying to access
    suspend fun getFood(): List<String>
}

