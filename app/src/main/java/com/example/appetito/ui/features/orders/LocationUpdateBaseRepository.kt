package com.example.appetito.ui.features.orders

import com.example.appetito.data.SocketService

abstract class LocationUpdateBaseRepository (val socketService: SocketService)
{
    open val messages = socketService.messages
    abstract fun connect(orderID: String, riderID: String)
    abstract fun disconnect()
}