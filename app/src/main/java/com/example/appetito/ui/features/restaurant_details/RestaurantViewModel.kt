package com.example.appetito.ui.features.restaurant_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appetito.data.FoodApi
import com.example.appetito.data.models.FoodItem
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
class RestaurantViewModel @Inject constructor(val foodApi: FoodApi): ViewModel() {

    var errorMsg = ""
    var errorDescription = ""

    private val _uiState = MutableStateFlow<RestaurantEvent>(RestaurantEvent.Nothing)
    val uiState = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<RestaurantNavigationEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    fun getFoodItem(id: String){
        viewModelScope.launch {
            _uiState.value = RestaurantEvent.Loading
            try {
                val response = safeApiCall {
                    foodApi.getFoodItemForRestaurant(id)
                }

                when(response){
                    is ApiResponse.Success -> {
                        _uiState.value = RestaurantEvent.Success(response.data.foodItems)
                    }
                    is ApiResponse.Error -> {
                        val error = (response as? ApiResponse.Error)?.code
                        when(error){

                            401 ->{
                                errorMsg = "Unauthorized"
                                errorDescription = "You are not authorized to access this content"
                            }

                            404 ->{
                                errorMsg = "Not Found"
                                errorDescription = "The Restaurant was not found"
                            }

                            else -> {
                                errorMsg = "Unknown Error"
                                errorDescription = "An unknown error occurred"
                            }
                        }
                        _uiState.value =RestaurantEvent.Error
                        _navigationEvent.emit(RestaurantNavigationEvent.ShowErrorDialog)
                    }
                    is ApiResponse.Exception -> {

                    }
                }

            }catch (e: Exception){
                _uiState.value = RestaurantEvent.Error
                _navigationEvent.emit(RestaurantNavigationEvent.ShowErrorDialog)
            }
        }
    }

    sealed class RestaurantNavigationEvent{
        data object GoBack: RestaurantNavigationEvent()
        data object ShowErrorDialog: RestaurantNavigationEvent()
        data class NavigateToProduceDetails(val productId: String): RestaurantNavigationEvent()
    }

    sealed class RestaurantEvent{
        data object Nothing: RestaurantEvent()
        data class Success(val foodItems: List<FoodItem>): RestaurantEvent()
        data object Error: RestaurantEvent()
        data object Loading: RestaurantEvent()
    }
}