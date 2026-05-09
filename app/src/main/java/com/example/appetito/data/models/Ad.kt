package com.example.appetito.data.models

data class Ad(
    val id: String = "",
    val imageUrl: String = "",
    val restaurantId: String = "",
    val restaurantName: String = "",
    val title: String = "",
    val description: String = "",
    val cost: Double = 0.0,
    val createdAt: String = ""
)

data class AdListResponse(
    val data: List<Ad>
)
