package com.example.appetito.ui.features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appetito.data.FoodApi
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
class HomeViewModel @Inject constructor(private val foodApi: FoodApi): ViewModel() {

    private val _uiState = MutableStateFlow<HomeScreenState>(HomeScreenState.Loading)
    val uiState: StateFlow<HomeScreenState> =  _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<HomeScreenNavigationEvents?>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    var categories = emptyList<Category>()
    var restaurants = emptyList<Restaurant>()

    init {
       viewModelScope.launch {
           categories = getCategories()
           restaurants = getPopularRestaurants()

           if(categories.isNotEmpty() && restaurants.isNotEmpty()){
               _uiState.value = HomeScreenState.Success
           }
           else{
               _uiState.value = HomeScreenState.Empty
           }
       }
    }


    private suspend fun getCategories(): List<Category>{

        var list = emptyList<Category>()

            val response = safeApiCall {
                foodApi.getCategories()
            }
            when(response){
                is ApiResponse.Success ->{
                    list = response.data.data
                }

                is ApiResponse.Error ->{
                }
                else -> {
                }
            }

        return list
    }

    private suspend fun getPopularRestaurants(): List<Restaurant>{

        var list = emptyList<Restaurant>()

        val response = safeApiCall {
            foodApi.getRestaurants(lat=40.7128, lon=-74.0060)
        }
        when(response){
            is ApiResponse.Success ->{
                list = response.data.data
                _uiState.value = HomeScreenState.Success
            }

            is ApiResponse.Error ->{
            }
            else -> {
            }
        }

        return list;
    }

    sealed class HomeScreenState{
        object Loading : HomeScreenState()
        object Empty : HomeScreenState()
        object Success : HomeScreenState()
    }

    sealed class HomeScreenNavigationEvents {
        object NavigateToDetail : HomeScreenNavigationEvents()
    }
}