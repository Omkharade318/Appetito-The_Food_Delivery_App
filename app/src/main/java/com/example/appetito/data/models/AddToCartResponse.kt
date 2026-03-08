package com.example.appetito.data.models

import kotlinx.serialization.Serializable

@Serializable
data class AddToCartResponse(
    val id: String,
    val message: String
)
