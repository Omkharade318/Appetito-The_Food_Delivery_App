package com.example.appetito.ui.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appetito.data.FoodApi
import com.example.appetito.data.models.Ad
import com.example.appetito.data.models.Category
import com.example.appetito.data.models.Restaurant
import com.example.appetito.data.remote.ApiResponse
import com.example.appetito.data.remote.safeApiCall
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val foodApi: FoodApi,
    private val session: com.example.appetito.data.FoodHubSession
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeScreenState>(HomeScreenState.Loading)
    val uiState: StateFlow<HomeScreenState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<HomeScreenNavigationEvents?>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    var categories = emptyList<Category>()
    var restaurants = emptyList<Restaurant>()
    var ads = emptyList<Ad>()

    init {
        viewModelScope.launch {
            categories = getCategories()
            restaurants = getPopularRestaurants()
            ads = getAds()

            if (categories.isNotEmpty() && restaurants.isNotEmpty()) {
                _uiState.value = HomeScreenState.Success
            } else {
                _uiState.value = HomeScreenState.Empty
            }
        }

    }

    private suspend fun getCategories(): List<Category> {

        var list = emptyList<Category>()
        val response = safeApiCall {
            foodApi.getCategories()
        }
        when (response) {
            is com.example.appetito.data.remote.ApiResponse.Success -> {
                list = response.data.data
            }

            else -> {
            }
        }
        return list

    }

    private suspend fun getAds(): List<Ad> {
        var list = emptyList<Ad>()
        val response = safeApiCall {
            foodApi.getAds()
        }
        when (response) {
            is com.example.appetito.data.remote.ApiResponse.Success -> {
                list = response.data.data
            }
            else -> {}
        }
        return list
    }

    private suspend fun getPopularRestaurants(): List<Restaurant> {
        var list = emptyList<Restaurant>()
        val response = safeApiCall {
            foodApi.getRestaurants(40.7128, -74.0060)
        }
        when (response) {
            is com.example.appetito.data.remote.ApiResponse.Success -> {
                list = response.data.data

            }

            else -> {
            }
        }
        return list
    }

    fun onRestaurantSelected(it: Restaurant) {
        viewModelScope.launch {
            _navigationEvent.emit(
                HomeScreenNavigationEvents.NavigateToDetail(
                    it.name,
                    it.imageUrl,
                    it.id
                )
            )
        }
    }

    fun onAdSelected(ad: Ad) {
        viewModelScope.launch {
            foodApi.recordAdClick(ad.id)
            session.storeAdId(ad.id)
            _navigationEvent.emit(HomeScreenNavigationEvents.NavigateToAdDetail(ad))
        }
    }

    sealed class HomeScreenState {
        object Loading : HomeScreenState()
        object Empty : HomeScreenState()
        object Success : HomeScreenState()
    }

    sealed class HomeScreenNavigationEvents {
        data class NavigateToDetail(val name: String, val imageUrl: String, val id: String) :
            HomeScreenNavigationEvents()
        data class NavigateToAdDetail(val ad: Ad) : HomeScreenNavigationEvents()
    }
}