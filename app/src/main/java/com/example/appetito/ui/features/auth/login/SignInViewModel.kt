package com.example.appetito.ui.features.auth.login

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appetito.data.FoodApi
import com.example.appetito.data.FoodHubSession
import com.example.appetito.data.auth.GoogleAuthUIProvider
import com.example.appetito.data.models.OAuthRequest
import com.example.appetito.data.models.SignInRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(val foodApi: FoodApi, val session: FoodHubSession) : ViewModel() {

    val googleAuthUIProvider = GoogleAuthUIProvider()
    private val _uiState = MutableStateFlow<SignInEvent>(SignInEvent.Nothing)
    val uiState = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<SignInNavigationEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    fun onEmailChange(email: String){
        _email.value = email
    }

    fun onPasswordChange(password: String){
        _password.value = password
    }

    fun onGoogleSignInClicked(context: Context){
        viewModelScope.launch {
            _uiState.value = SignInEvent.Loading
            val response = googleAuthUIProvider.signIn(
                activityContext = context,
                credentialManager = CredentialManager.create(context)
            )

            if (response != null){

                val request = OAuthRequest(
                    token = response.token,
                    provider = "google"
                )

                val res = foodApi.oAuth(request)

                if(res.token.isNotEmpty()){
                    Log.d("SignInViewModel", "Token: ${res.token}")
                    _uiState.value = SignInEvent.Success
                    session.storeToken(response.token)
                    _navigationEvent.emit(SignInNavigationEvent.NavigationToHome)
                } else {
                    _uiState.value = SignInEvent.Error
                }

            } else {
                _uiState.value = SignInEvent.Error
            }
        }
    }

    fun onSignInClick(){
        viewModelScope.launch {
            _uiState.value = SignInEvent.Loading

            try {
                val response = foodApi.signIn(
                    packageName = "com.example.appetito",
                    request = SignInRequest(
                        email = email.value,
                        password = password.value
                    )
                )
                if(response.token.isNotEmpty()){
                    _uiState.value = SignInEvent.Success
                    _navigationEvent.emit(SignInNavigationEvent.NavigationToHome)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = SignInEvent.Error
            }
        }
    }

    fun onSignUpClicked(){
        viewModelScope.launch {
            _navigationEvent.emit(SignInNavigationEvent.NavigationToSignUp)
        }
    }


    sealed class SignInNavigationEvent{
        object NavigationToSignUp : SignInNavigationEvent()
        object NavigationToHome : SignInNavigationEvent()
    }

    sealed class SignInEvent{
        object Nothing : SignInEvent()
        object Success : SignInEvent()
        object Error : SignInEvent()
        object Loading : SignInEvent()
    }
}