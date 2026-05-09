package com.example.appetito.data.repository

import com.example.appetito.ui.features.orders.LocationUpdateBaseRepository
import com.example.appetito.data.SocketService
import javax.inject.Inject

class CustomerLocationUpdateSocketRepository @Inject constructor(socketService: SocketService) :
    LocationUpdateBaseRepository(socketService) {

    override fun connect(orderID: String, riderID: String) {
        try {
            socketService.connect(
                orderID, riderID, null, null, "CUSTOMER"
            )
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    override fun disconnect() {
        socketService.disconnect()
    }
}