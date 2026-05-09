package com.example.appetito.data.models

import kotlinx.serialization.Serializable

@Serializable
data class FoodItem(
    val arModelUrl: String? = null,
    val createdAt: String? = null,
    val description: String,
    val id: String? = null,
    val imageUrl: String? = null,
    val name: String,
    val price: Double,
    val restaurantId: String
)