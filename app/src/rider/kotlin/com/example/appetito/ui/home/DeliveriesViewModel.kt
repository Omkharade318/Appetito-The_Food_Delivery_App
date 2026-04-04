package com.example.appetito.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appetito.data.FoodApi
import com.example.appetito.data.models.Delieveries
import com.example.appetito.data.models.DelieveriesListResponse
import com.example.appetito.data.models.GenericMsgResponse
import com.example.appetito.data.remote.ApiResponse
import com.example.appetito.data.remote.safeApiCall
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeliveriesViewModel @Inject constructor(val foodApi: FoodApi) : ViewModel() {

    private val _deliveriesState = MutableStateFlow<DeliveriesState>(DeliveriesState.Loading)
    val deliveriesState = _deliveriesState.asStateFlow()

    private val _deliveriesEvent = MutableSharedFlow<DeliveriesEvent>()
    val deliveriesEvent = _deliveriesEvent.asSharedFlow()

    val deliveries = MutableStateFlow<DelieveriesListResponse?>(null)

    init {
        getDeliveries()
    }

    fun deliveryAccepted(delivery: Delieveries) {
        viewModelScope.launch {
            _deliveriesState.value = DeliveriesState.Loading
            try {
                val response = safeApiCall { foodApi.acceptDelivery(delivery.orderId) }
                processDeliveryStateUpdate(response)
            } catch (e: Exception) {
                _deliveriesState.value = DeliveriesState.Success(deliveries.value?.data!!)
            }
        }
    }

    fun deliveryRejected(delivery: Delieveries) {
        viewModelScope.launch {
            viewModelScope.launch {
                _deliveriesState.value = DeliveriesState.Loading
                try {
                    val response = safeApiCall { foodApi.rejectDelivery(delivery.orderId) }
                    processDeliveryStateUpdate(response)
                } catch (e: Exception) {
                    _deliveriesState.value = DeliveriesState.Success(deliveries.value?.data!!)
                }
            }
        }
    }

    private suspend fun processDeliveryStateUpdate(response: ApiResponse<GenericMsgResponse>) {

        when (response) {
            is ApiResponse.Success -> {
                _deliveriesState.value =
                    DeliveriesState.Success(deliveries.value?.data!!)
                getDeliveries()
            }

            is ApiResponse.Error -> {
                _deliveriesState.value =
                    DeliveriesState.Success(deliveries.value?.data!!)
                _deliveriesEvent.emit(DeliveriesEvent.ShowError(response.message))
            }

            else -> {
                _deliveriesState.value =
                    DeliveriesState.Success(deliveries.value?.data!!)
                _deliveriesEvent.emit(DeliveriesEvent.ShowError("An error occurred"))
            }
        }
    }

    fun getDeliveries() {
        viewModelScope.launch {
            try {
                _deliveriesState.value = DeliveriesState.Loading
                val response = safeApiCall { foodApi.getAvailableDeliveries() }
                when (response) {
                    is ApiResponse.Success -> {
                        _deliveriesState.value = DeliveriesState.Success(response.data.data)
                        deliveries.value = response.data
                    }

                    is ApiResponse.Error -> {
                        _deliveriesState.value = DeliveriesState.Error(response.message)
                    }

                    else -> {
                        _deliveriesState.value = DeliveriesState.Error("An error occurred")
                    }
                }
            } catch (e: Exception) {
                _deliveriesState.value = DeliveriesState.Error("An error occurred")
            }
        }

    }

    sealed class DeliveriesState {
        object Loading : DeliveriesState()
        data class Success(val deliveries: List<Delieveries>) : DeliveriesState()
        data class Error(val message: String) : DeliveriesState()
    }

    sealed class DeliveriesEvent {
        object NavigateToOrderDetails : DeliveriesEvent()
        data class ShowError(val message: String) : DeliveriesEvent()
    }
}