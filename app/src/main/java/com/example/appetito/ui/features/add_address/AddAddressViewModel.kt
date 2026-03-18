package com.example.appetito.ui.features.add_address

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appetito.data.FoodApi
import com.example.appetito.data.models.Address
import com.example.appetito.data.models.ReverseGeoCodeRequest
import com.example.appetito.data.remote.ApiResponse
import com.example.appetito.data.remote.safeApiCall
import com.example.appetito.location.LocationManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddAddressViewModel @Inject constructor(
    val foodApi: FoodApi,
    private val locationManager: LocationManager
): ViewModel() {

    private val _uiState = MutableStateFlow<AddAddressState>(AddAddressState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<AddAddressEvent>()
    val event = _event.asSharedFlow()

    private val _address = MutableStateFlow<Address?>(null)
    val address = _address.asStateFlow()

    fun getLocation() = locationManager.getLocation()

    fun reverseGeocode(lat: Double, lon: Double){
        viewModelScope.launch {
            _address.value = null
            val address = safeApiCall {
                foodApi.reverseGeocode(ReverseGeoCodeRequest(lat, lon))
            }

            when(address){
                is ApiResponse.Success ->{
                    _address.value = address.data
                    _uiState.value = AddAddressState.Success
                }

                else -> {
                    _address.value = null
                    _uiState.value = AddAddressState.Error("Failed to reverse geocode")
                }
            }

        }
    }


    fun onAddAddressClicked(){
        viewModelScope.launch {
            _uiState.value = AddAddressState.AddressStoring
            val result = safeApiCall { foodApi.storeAddress(address.value!!) }

            when(result){
                is ApiResponse.Success -> {
                    _uiState.value = AddAddressState.Success
                    _event.emit(AddAddressEvent.NavigateToAddressList)
                }

                else -> {
                    _uiState.value = AddAddressState.Error("Failed to store address")
                }
            }
        }
    }

    sealed class AddAddressEvent{
        object NavigateToAddressList: AddAddressEvent()
    }

    sealed class AddAddressState{
        object Loading: AddAddressState()
        object Success: AddAddressState()
        object AddressStoring: AddAddressState()
        data class Error(val message: String): AddAddressState()
    }
}