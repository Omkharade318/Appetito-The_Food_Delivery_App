package com.example.appetito.data

import com.example.appetito.data.models.Ad
import com.example.appetito.data.models.AdListResponse
import com.example.appetito.data.models.AddToCartRequest
import com.example.appetito.data.models.AddToCartResponse
import com.example.appetito.data.models.Address
import com.example.appetito.data.models.AddressListResponse
import com.example.appetito.data.models.AuthResponse
import com.example.appetito.data.models.CartResponse
import com.example.appetito.data.models.CategoriesResponse
import com.example.appetito.data.models.ConfirmPaymentRequest
import com.example.appetito.data.models.ConfirmPaymentResponse
import com.example.appetito.data.models.DelieveriesListResponse
import com.example.appetito.data.models.FCMRequest
import com.example.appetito.data.models.FoodItem
import com.example.appetito.data.models.FoodItemListResponse
import com.example.appetito.data.models.FoodItemResponse
import com.example.appetito.data.models.GenericMsgResponse
import com.example.appetito.data.models.ImageUploadResponse
import com.example.appetito.data.models.NotificationListResponse
import com.example.appetito.data.models.OAuthRequest
import com.example.appetito.data.models.Order
import com.example.appetito.data.models.OrderListResponse
import com.example.appetito.data.models.PaymentIntentRequest
import com.example.appetito.data.models.PaymentIntentResponse
import com.example.appetito.data.models.Restaurant
import com.example.appetito.data.models.RestaurantsResponse
import com.example.appetito.data.models.ReverseGeoCodeRequest
import com.example.appetito.data.models.RiderDeliveryOrderListResponse
import com.example.appetito.data.models.SignInRequest
import com.example.appetito.data.models.SignUpRequest
import com.example.appetito.data.models.UpdateCartItemRequest
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
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

    @POST("/auth/signup")
    suspend fun signUp(@Body request: SignUpRequest): Response<AuthResponse>

    @POST("/auth/login")
    suspend fun signIn(@Body request: SignInRequest): Response<AuthResponse>

    @POST("/auth/oauth")
    suspend fun oAuth(@Body request: OAuthRequest): Response<AuthResponse>

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

    @GET("addresses")
    suspend fun getUserAddress(): Response<AddressListResponse>

    @POST("addresses/reverse-geocode")
    suspend fun reverseGeocode(@Body request: ReverseGeoCodeRequest): Response<Address>

    @POST("addresses")
    suspend fun storeAddress(@Body address: Address): Response<GenericMsgResponse>

    @POST("/payments/create-intent")
    suspend fun getPaymentIntent(@Body request: PaymentIntentRequest): Response<PaymentIntentResponse>

    @POST("/payments/confirm/{paymentIntentId}")
    suspend fun verifyPurchase(
        @Body request: ConfirmPaymentRequest, @Path("paymentIntentId") paymentIntentId: String
    ): Response<ConfirmPaymentResponse>

    @GET("/orders")
    suspend fun getOrders(): Response<OrderListResponse>

    @GET("/orders/{orderId}")
    suspend fun getOrderDetails(@Path("orderId") orderId: String): Response<Order>

    @PUT("/notifications/fcm-token")
    suspend fun updateToken(@Body request: FCMRequest): Response<GenericMsgResponse>

    @POST("/notifications/{id}/read")
    suspend fun readNotification(@Path("id") id: String): Response<GenericMsgResponse>

    @GET("/notifications")
    suspend fun getNotifications(): Response<NotificationListResponse>

    // add restaurant endpoints
    @GET("/restaurant-owner/profile")
    suspend fun getRestaurantProfile(): Response<Restaurant>

    @GET("/restaurant-owner/orders")
    suspend fun getRestaurantOrders(@Query("status") status: String): Response<OrderListResponse>

    @PATCH("orders/{orderId}/status")
    suspend fun updateOrderStatus(
        @Path("orderId") orderId: String,
        @Body map: Map<String, String>
    ): Response<GenericMsgResponse>

    @GET("/restaurants/{id}/menu")
    suspend fun getRestaurantMenu(@Path("id") restaurantId: String): Response<FoodItemListResponse>

    @POST("/restaurants/{id}/menu")
    suspend fun addRestaurantMenu(
        @Path("id") restaurantId: String,
        @Body foodItem: FoodItem
    ): Response<GenericMsgResponse>

    @POST("/images/upload")
    @Multipart
    suspend fun uploadImage(@Part image: MultipartBody.Part): Response<ImageUploadResponse>

    @GET("/rider/deliveries/available")
    suspend fun getAvailableDeliveries(): Response<DelieveriesListResponse>

    @POST("/rider/deliveries/{orderId}/reject")
    suspend fun rejectDelivery(@Path("orderId") orderId: String): Response<GenericMsgResponse>

    @POST("/rider/deliveries/{orderId}/accept")
    suspend fun acceptDelivery(@Path("orderId") orderId: String): Response<GenericMsgResponse>

    @GET("/rider/deliveries/active")
    suspend fun getActiveDeliveries(): Response<RiderDeliveryOrderListResponse>

    @GET("/ads")
    suspend fun getAds(): Response<AdListResponse>

    @POST("/ads")
    suspend fun createAd(@Body ad: Ad): Response<GenericMsgResponse>

    @GET("/ads/restaurant/{restaurantId}")
    suspend fun getAdsByRestaurant(@Path("restaurantId") restaurantId: String): Response<AdListResponse>

    @DELETE("/ads/{id}")
    suspend fun deleteAd(@Path("id") id: String): Response<GenericMsgResponse>
}

