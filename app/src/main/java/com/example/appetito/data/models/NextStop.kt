package com.example.appetito.data.models

import kotlinx.serialization.Serializable

@Serializable
data class NextStop(
    val address: String,
    val latitude: Double,
    val longitude: Double
)