package com.example.appetito.data.models

import kotlinx.serialization.Serializable

@Serializable
data class FoodItem(
    val arModeUrl: String?,
    val createdAt: String,
    val description: String,
    val id: String,
    val imageUrl: String,
    val name: String,
    val price: Double,
    val restaurantId: String
)
