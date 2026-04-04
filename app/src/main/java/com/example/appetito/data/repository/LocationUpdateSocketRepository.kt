package com.example.appetito.data.repository

import android.util.Log
import com.example.appetito.data.SocketService
import com.example.appetito.data.models.SocketLocationModel
import com.example.appetito.location.LocationManager
import com.example.appetito.ui.features.orders.LocationUpdateBaseRepository
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

open class LocationUpdateSocketRepository @Inject constructor(
    socketService: SocketService, private val locationManager: LocationManager
) : LocationUpdateBaseRepository(socketService) {

    private val _socketConnection =
        MutableStateFlow<SocketConnection>(SocketConnection.Disconnected)

    val socketConnection = _socketConnection.asStateFlow()

    override val messages = socketService.messages

    override fun connect(orderID: String, riderID: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val currentLocation = getUserLocation()
                socketService.connect(
                    orderID, riderID, currentLocation.latitude, currentLocation.longitude
                )
                _socketConnection.value = SocketConnection.Connected
                locationManager.startLocationUpdate()

                while (socketConnection.value == SocketConnection.Connected) {
                    locationManager.locationUpdate.collectLatest {
                        if (it != null) {
                            val item = SocketLocationModel(
                                orderID, riderID, it.latitude, it.longitude
                            )
                            Log.d("LocationUpdate", "Location: $item")
                            socketService.sendMessage(Json.encodeToString(item))
                        }
                    }

                }
            } catch (e: Exception) {
                _socketConnection.value = SocketConnection.Disconnected
                locationManager.stopLocationUpdate()
                e.printStackTrace()
            }

        }
    }

    override fun disconnect() {
        try {
            locationManager.stopLocationUpdate()
            socketService.disconnect()
            _socketConnection.value = SocketConnection.Disconnected
        } catch (e: Exception) {
            e.printStackTrace()
        }
        // disconnect from socket
    }

    fun sendMessage(message: String) {
        socketService.sendMessage(message)
    }

    suspend fun getUserLocation(): LatLng {
        return LatLng(0.0, 0.0)
    }

}

sealed class SocketConnection {
    object Connected : SocketConnection()
    object Disconnected : SocketConnection()
}