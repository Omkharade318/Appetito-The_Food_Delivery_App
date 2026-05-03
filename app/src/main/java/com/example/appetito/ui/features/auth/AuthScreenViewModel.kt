package com.example.appetito.ui.features.auth


import androidx.lifecycle.viewModelScope
import com.example.appetito.data.FoodApi
import com.example.appetito.data.FoodHubSession
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthScreenViewModel @Inject constructor(
    override val foodApi: FoodApi,
    val session: FoodHubSession
) :
    BaseAuthViewModel(foodApi) {

    private val _uiState = MutableStateFlow<AuthEvent>(AuthEvent.Nothing)
    val uiState = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<AuthNavigationEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()


    sealed class AuthNavigationEvent {
        object NavigateToSignUp : AuthNavigationEvent()
        object NavigateToHome : AuthNavigationEvent()
        object ShowErrorDialog : AuthNavigationEvent()
    }

    sealed class AuthEvent {
        object Nothing : AuthEvent()
        object Success : AuthEvent()
        object Error : AuthEvent()
        object Loading : AuthEvent()
    }

    override fun loading() {
        viewModelScope.launch {
            _uiState.value = AuthEvent.Loading
        }
    }

    override fun onGoogleError(msg: String) {
        viewModelScope.launch {
            errorDescription = msg
            error = "Google Sign In Failed"
            _uiState.value = AuthEvent.Error
            _navigationEvent.emit(AuthNavigationEvent.ShowErrorDialog)
        }
    }

    override fun onFacebookError(msg: String) {
        viewModelScope.launch {
            errorDescription = msg
            error = "Facebook Sign In Failed"
            _navigationEvent.emit(AuthNavigationEvent.ShowErrorDialog)
            _uiState.value = AuthEvent.Error
        }
    }

    override fun onSocialLoginSuccess(token: String) {
        viewModelScope.launch {
            session.storeToken(token)
            _uiState.value = AuthEvent.Success
            _navigationEvent.emit(AuthNavigationEvent.NavigateToHome)
        }
    }
}