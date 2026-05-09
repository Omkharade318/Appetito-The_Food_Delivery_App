package com.example.appetito.ui.navigation

import com.example.appetito.data.models.FoodItem
import kotlinx.serialization.Serializable

interface NavRoutes

@Serializable
object Login : NavRoutes

@Serializable
object SignUp : NavRoutes

@Serializable
object AuthScreen : NavRoutes

@Serializable
object Home : NavRoutes

@Serializable
data class RestaurantDetails(
    val restaurantId: String,
    val restaurantName: String,
    val restaurantImageUrl: String
) : NavRoutes

@Serializable
data class FoodDetails(val foodItems: FoodItem) : NavRoutes

@Serializable
object Cart : NavRoutes

@Serializable
object Notification : NavRoutes

@Serializable
object AddressList: NavRoutes

@Serializable
object AddAddress: NavRoutes

@Serializable
data class OrderSuccess(val orderId: String): NavRoutes

@Serializable
data class OrderDetails(val orderId: String) : NavRoutes

@Serializable
object OrderList : NavRoutes

@Serializable
object MenuList : NavRoutes

@Serializable
object AddMenu : NavRoutes

@Serializable
object ImagePicker : NavRoutes
@Serializable
object CreateAd : NavRoutes
@Serializable
data class AdDetails(val adId: String) : NavRoutes
