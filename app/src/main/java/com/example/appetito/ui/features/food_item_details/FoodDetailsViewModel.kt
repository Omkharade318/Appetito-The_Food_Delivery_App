package com.example.appetito.ui.features.food_item_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appetito.data.FoodApi
import com.example.appetito.data.models.AddToCartRequest
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
class FoodDetailsViewModel @Inject constructor(val foodApi: FoodApi) : ViewModel(){

    private val _uiState = MutableStateFlow<FoodDetailsUiState>(FoodDetailsUiState.Nothing)
    val uiState = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<FoodDetailsEvent>()
    val event = _event.asSharedFlow()

    private val _quantity = MutableStateFlow(1)
    val quantity = _quantity.asStateFlow()

    fun incrementQuantity(){
        _quantity.value++
    }

    fun decrementQuantity() {
        if (_quantity.value > 1) {
            _quantity.value--
        }
    }

    fun addToCart(restaurantId: String, foodItemId: String){
        viewModelScope.launch {
            _uiState.value = FoodDetailsUiState.Loading
            val response = safeApiCall{
                foodApi.addToCart(
                    request = AddToCartRequest(
                        restaurantId  = restaurantId,
                        menuItemId = foodItemId,
                        quantity = _quantity.value
                    )
                )

            }

            when(response){
                is ApiResponse.Success ->{
                    _uiState.value = FoodDetailsUiState.Nothing
                            _event.emit(FoodDetailsEvent.onAddToCart)
                }

                is ApiResponse.Error -> {
                    val msg = response.message
                    _uiState.value = FoodDetailsUiState.Error(msg)
                    _event.emit(FoodDetailsEvent.showErrorDialog(msg))
                }

                is ApiResponse.Exception -> {
                    val msg = response.exception.message ?: "An unexpected error occurred"
                    _uiState.value = FoodDetailsUiState.Error(msg)
                    _event.emit(FoodDetailsEvent.showErrorDialog(msg))
                }
            }
        }
    }

    fun goToCart(){
        viewModelScope.launch {
            _event.emit(FoodDetailsEvent.goToCart)
        }
    }

    sealed class FoodDetailsUiState{
        object Nothing : FoodDetailsUiState()
        object Loading : FoodDetailsUiState()
        object Success : FoodDetailsUiState()
        data class Error(val message: String) : FoodDetailsUiState()
    }

    sealed class FoodDetailsEvent{
        data class showErrorDialog(val message: String) : FoodDetailsEvent()
        object onAddToCart: FoodDetailsEvent()
        object goToCart : FoodDetailsEvent()
    }
}