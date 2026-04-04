package com.example.appetito.data.models

import kotlinx.serialization.Serializable

@Serializable
data class SocketLocationModel(
    val orderId: String,
    val riderId: String,
    val latitude: Double,
    val longitude: Double,
    val type: String = "LOCATION_UPDATE"
)
