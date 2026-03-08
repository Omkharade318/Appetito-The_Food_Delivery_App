package com.example.appetito.data.models

data class UpdateCartItemRequest(
    val cartItemId: String,
    val quantity: Int
)
