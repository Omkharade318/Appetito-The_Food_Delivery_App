package com.example.appetito.data.models

import kotlinx.serialization.Serializable

@Serializable
data class AddToCartRequest(
    val restaurantId: String,
    val menuItemId: String,
    val quantity: Int
)
