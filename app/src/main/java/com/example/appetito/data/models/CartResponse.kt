package com.example.appetito.data.models

data class CartResponse(
    val checkoutDetails: CheckOutDetails,
    val items: List<CartItem>
)