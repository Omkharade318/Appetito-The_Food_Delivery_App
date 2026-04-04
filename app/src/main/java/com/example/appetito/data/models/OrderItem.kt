package com.example.appetito.data.models

data class OrderItem(
    val id: String,
    val menuItemId: String,
    val orderId: String,
    val quantity: Int,
    val menuItemName: String? = null,
)