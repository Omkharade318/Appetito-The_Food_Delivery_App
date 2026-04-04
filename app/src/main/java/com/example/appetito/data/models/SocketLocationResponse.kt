package com.example.appetito.data.models

import kotlinx.serialization.Serializable

@Serializable
data class SocketLocationResponse(
    val currentLocation: CurrentLocation,
    val deliveryPhase: String,
    val estimatedTime: Int,
    val finalDestination: FinalDestination,
    val nextStop: NextStop,
    val polyline: String
)