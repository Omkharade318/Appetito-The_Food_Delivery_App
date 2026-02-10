package com.example.appetito.data

import com.example.appetito.data.models.AuthResponse
import com.example.appetito.data.models.CategoriesResponse
import com.example.appetito.data.models.OAuthRequest
import com.example.appetito.data.models.SignInRequest
import com.example.appetito.data.models.SignUpRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface FoodApi {
    @GET("/categories") // /food is the endpoint of the API we are trying to access
    suspend fun getCategories(): Response<CategoriesResponse>


    // ✅ Signup
    @POST("/auth/signup")
    suspend fun signUp(
        @Body request: SignUpRequest
    ): AuthResponse

    // ✅ Login
    @POST("/auth/login")
    suspend fun signIn(
        @Header("X-Package-Name") packageName: String,
        @Body request: SignInRequest
    ): AuthResponse

    @POST("/auth/oauth")
    suspend fun oAuth(
        @Body request: OAuthRequest
    ): AuthResponse

}

