package com.example.appetito.ui.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appetito.data.FoodApi
import com.example.appetito.data.FoodHubSession
import com.example.appetito.data.models.Restaurant
import com.example.appetito.data.remote.ApiResponse
import com.example.appetito.data.remote.safeApiCall
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(val foodApi: FoodApi, val session: FoodHubSession) :
    ViewModel() {

    private val _uiState = MutableStateFlow<HomeScreenState>(HomeScreenState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        getRestaurantProfile()
    }

    fun getRestaurantProfile() {
        viewModelScope.launch {
            _uiState.value = HomeScreenState.Loading
            val response = safeApiCall { foodApi.getRestaurantProfile() }
            when (response) {
                is ApiResponse.Success -> {
                    session.storeRestaurantId(response.data.id)
                    fetchAds(response.data)
                }
                is ApiResponse.Error -> _uiState.value = HomeScreenState.Failed
                is ApiResponse.Exception -> _uiState.value = HomeScreenState.Failed
            }
        }
    }

    private fun fetchAds(restaurant: Restaurant) {
        viewModelScope.launch {
            val response = safeApiCall { foodApi.getAdsByRestaurant(restaurant.id) }
            when (response) {
                is ApiResponse.Success -> {
                    _uiState.value = HomeScreenState.Success(restaurant, response.data.data)
                }
                else -> {
                    _uiState.value = HomeScreenState.Success(restaurant, emptyList())
                }
            }
        }
    }

    fun deleteAd(adId: String) {
        viewModelScope.launch {
            val currentState = _uiState.value
            if (currentState is HomeScreenState.Success) {
                val response = safeApiCall { foodApi.deleteAd(adId) }
                if (response is ApiResponse.Success) {
                    // Refetch ads
                    fetchAds(currentState.data)
                }
            }
        }
    }

    fun retry() {
        getRestaurantProfile()
    }

    sealed class HomeScreenState {
        object Loading : HomeScreenState()
        object Failed : HomeScreenState()
        data class Success(val data: Restaurant, val ads: List<com.example.appetito.data.models.Ad>) : HomeScreenState()
    }
}