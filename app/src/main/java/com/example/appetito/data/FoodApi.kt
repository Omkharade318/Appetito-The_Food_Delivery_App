package com.example.appetito.data

import com.example.appetito.data.models.AddToCartRequest
import com.example.appetito.data.models.AddToCartResponse
import com.example.appetito.data.models.AuthResponse
import com.example.appetito.data.models.CartResponse
import com.example.appetito.data.models.CategoriesResponse
import com.example.appetito.data.models.FoodItemResponse
import com.example.appetito.data.models.GenericMsgResponse
import com.example.appetito.data.models.OAuthRequest
import com.example.appetito.data.models.RestaurantsResponse
import com.example.appetito.data.models.SignInRequest
import com.example.appetito.data.models.SignUpRequest
import com.example.appetito.data.models.UpdateCartItemRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface FoodApi {

    @GET("/categories") // /food is the endpoint of the API we are trying to access
    suspend fun getCategories(): Response<CategoriesResponse>

    @GET("/restaurants")
    suspend fun getRestaurants(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double
    ): Response<RestaurantsResponse>

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

    @GET("restaurants/{restaurantId}/menu")
    suspend fun getFoodItemForRestaurant(
        @Path("restaurantId") restaurantId: String
    ): Response<FoodItemResponse>

    @POST("/cart")
    suspend fun addToCart(@Body request: AddToCartRequest): Response<AddToCartResponse>

    @GET("/cart")
    suspend fun getCart(): Response<CartResponse>

    @PATCH("/cart")
    suspend fun updateCart(@Body request: UpdateCartItemRequest): Response<GenericMsgResponse>

    @DELETE("/cart/{cartItemId}")
    suspend fun deleteCartItem(@Path("cartItemId") cartItemId: String): Response<GenericMsgResponse>
}

