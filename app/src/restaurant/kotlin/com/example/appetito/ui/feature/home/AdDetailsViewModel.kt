package com.example.appetito.ui.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appetito.data.FoodApi
import com.example.appetito.data.models.AdAnalytics
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdDetailsViewModel @Inject constructor(
    private val foodApi: FoodApi
) : ViewModel() {

    private val _uiState = MutableStateFlow<AdDetailsState>(AdDetailsState.Loading)
    val uiState: StateFlow<AdDetailsState> = _uiState

    fun loadAnalytics(adId: String) {
        viewModelScope.launch {
            _uiState.value = AdDetailsState.Loading
            try {
                val response = foodApi.getAdAnalytics(adId)
                if (response.isSuccessful && response.body() != null) {
                    _uiState.value = AdDetailsState.Success(response.body()!!)
                } else {
                    _uiState.value = AdDetailsState.Error("Failed to fetch analytics: ${response.message()}")
                }
            } catch (e: Exception) {
                _uiState.value = AdDetailsState.Error(e.message ?: "Unknown error")
            }
        }
    }

    sealed class AdDetailsState {
        object Loading : AdDetailsState()
        data class Success(val data: AdAnalytics) : AdDetailsState()
        data class Error(val message: String) : AdDetailsState()
    }
}
