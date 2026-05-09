package com.example.appetito.ui.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appetito.data.FoodApi
import com.example.appetito.data.models.Ad
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
class CreateAdViewModel @Inject constructor(private val foodApi: FoodApi) : ViewModel() {

    private val _uiState = MutableStateFlow<CreateAdState>(CreateAdState.Idle)
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<CreateAdEvent>()
    val events = _events.asSharedFlow()

    fun createAd(title: String, description: String, imageUrl: String) {
        viewModelScope.launch {
            _uiState.value = CreateAdState.Loading
            
            // First get restaurant profile to get ID and Name
            val profileResponse = safeApiCall { foodApi.getRestaurantProfile() }
            if (profileResponse is ApiResponse.Success) {
                val restaurant = profileResponse.data
                val ad = Ad(
                    title = title,
                    description = description,
                    imageUrl = imageUrl,
                    restaurantId = restaurant.id,
                    restaurantName = restaurant.name
                )
                
                val response = safeApiCall { foodApi.createAd(ad) }
                if (response is ApiResponse.Success) {
                    _uiState.value = CreateAdState.Success
                    _events.emit(CreateAdEvent.AdCreated)
                } else {
                    _uiState.value = CreateAdState.Error("Failed to create ad")
                }
            } else {
                _uiState.value = CreateAdState.Error("Failed to fetch restaurant profile")
            }
        }
    }

    sealed class CreateAdState {
        object Idle : CreateAdState()
        object Loading : CreateAdState()
        object Success : CreateAdState()
        data class Error(val message: String) : CreateAdState()
    }

    sealed class CreateAdEvent {
        object AdCreated : CreateAdEvent()
    }
}
