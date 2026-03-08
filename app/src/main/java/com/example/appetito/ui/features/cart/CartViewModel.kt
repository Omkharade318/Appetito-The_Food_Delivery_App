package com.example.appetito.ui.features.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appetito.data.FoodApi
import com.example.appetito.data.models.CartItem
import com.example.appetito.data.models.CartResponse
import com.example.appetito.data.models.UpdateCartItemRequest
import com.example.appetito.data.remote.ApiResponse
import com.example.appetito.data.remote.safeApiCall
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(val foodApi: FoodApi): ViewModel(){

    private val _uiState = MutableStateFlow<CartUiState>(CartUiState.Loading)
    val uiState = _uiState

    private val _event = MutableSharedFlow<CartEvent>()
    val event = _event.asSharedFlow()

    private var cartResponse: CartResponse? = null

    var errorTitle: String = ""
    var errorMessage: String = ""

    init {
        getCart()
    }

    fun getCart(){
        viewModelScope.launch {
            _uiState.value= CartUiState.Loading
            val res = safeApiCall { foodApi.getCart() }

            when(res){
                is ApiResponse.Success -> {
                    cartResponse = res.data
                    _uiState.value = CartUiState.Success(res.data)
                }

                is ApiResponse.Error -> {
                    _uiState.value = CartUiState.Error(res.message)
                }

                else -> {
                    _uiState.value = CartUiState.Error("Something went wrong")
                }
            }
        }

    }

    fun incrementQuantity(cartItem: CartItem){
        if(cartItem.quantity == 10){
            return
        }
        updateItemQuantity(cartItem, cartItem.quantity + 1)
    }

    fun decrementQuantity(cartItem: CartItem){
        if(cartItem.quantity == 1){
            return
        }
        updateItemQuantity(cartItem, cartItem.quantity - 1)
    }

    private fun updateItemQuantity(cartItem: CartItem, quantity: Int){
        viewModelScope.launch {

            _uiState.value = CartUiState.Loading
            val res =
                safeApiCall { foodApi.updateCart(UpdateCartItemRequest(cartItem.id, quantity)) }

            when(res){
                is ApiResponse.Success -> {
                    getCart()
                }

                else -> {
                    cartResponse?.let {
                        _uiState.value = CartUiState.Success(cartResponse!!)
                    }
                    errorTitle = "Cannot Update Quantity"
                    errorMessage = "An Error occurred while updating the quantity of the item"
                    _event.emit(CartEvent.onQuantityUpdateError)
                }
            }
        }
    }

    fun removeItem(cartItem: CartItem){
        viewModelScope.launch {

            _uiState.value = CartUiState.Loading
            val res =
                safeApiCall { foodApi.deleteCartItem(cartItem.id) }

            when(res){
                is ApiResponse.Success -> {
                    getCart()
                }

                else -> {
                    cartResponse?.let {
                        _uiState.value = CartUiState.Success(cartResponse!!)
                    }
                    errorTitle = "Cannot Delete Item"
                    errorMessage = "An Error occurred while deleting the item"
                    _event.emit(CartEvent.onItemRemoveError)
                }
            }
        }
    }

    fun checkout(){

    }

    sealed class CartUiState{
        object Nothing: CartUiState()
        object Loading: CartUiState()
        data class Success(val data: CartResponse): CartUiState()
        data class Error(val message: String) : CartUiState()
    }

    sealed class CartEvent {
        object showErrorDialog: CartEvent()
        object OnCheckout: CartEvent()
        object onQuantityUpdateError: CartEvent()
        object onItemRemoveError: CartEvent()
    }
}