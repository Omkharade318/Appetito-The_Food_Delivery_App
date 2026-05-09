package com.example.appetito.ui.features.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appetito.data.models.SocketLocation
import com.example.appetito.data.models.SocketLocationResponse
import com.google.maps.android.PolyUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

abstract class OrderDetailsBaseViewModel (open val repository: LocationUpdateBaseRepository) :
    ViewModel() {
    private val _locationUpdate = MutableStateFlow<SocketLocation?>(null)
    val locationUpdate = _locationUpdate.asStateFlow()

    private fun processLocation() {
        viewModelScope.launch {
            repository.messages.collectLatest {
                if (it.isEmpty())
                    return@collectLatest
                val response = Json.decodeFromString(SocketLocationResponse.serializer(), it)
                _locationUpdate.value = SocketLocation(
                    response.currentLocation,
                    response.deliveryPhase,
                    response.estimatedTime,
                    response.finalDestination,
                    response.nextStop,
                    PolyUtil.decode(response.polyline)
                )
            }
        }
    }

    protected fun connectSocket(orderID: String, riderId: String) {
        repository.connect(orderID, riderId)
        processLocation()
    }

    protected fun disconnectSocket() {
        repository.disconnect()
    }

}