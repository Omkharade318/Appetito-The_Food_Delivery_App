package com.example.appetito.data

import kotlinx.coroutines.flow.Flow

interface SocketService {

    fun connect(
        orderID: String,
        riderID: String,
        lat: Double?,
        lng: Double?,
        role: String
    )

    fun disconnect()

    fun sendMessage(message: String)

    val messages: Flow<String>
}