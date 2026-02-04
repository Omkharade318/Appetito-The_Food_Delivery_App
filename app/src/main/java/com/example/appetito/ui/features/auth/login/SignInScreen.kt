package com.example.appetito.ui.features.auth.login

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.appetito.R
import com.example.appetito.ui.FoodHubTextField
import com.example.appetito.ui.GroupSocialButtons
import com.example.appetito.ui.navigation.AuthScreen
import com.example.appetito.ui.navigation.Home
import com.example.appetito.ui.navigation.Login
import com.example.appetito.ui.navigation.SignUp
import com.example.appetito.ui.theme.Orange
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SignInScreen(
    navController: NavController,
    viewModel: SignInViewModel = hiltViewModel()
) {
    Box(modifier = Modifier.fillMaxSize()) {

        val email= viewModel.email.collectAsStateWithLifecycle()
        val password = viewModel.password.collectAsStateWithLifecycle()
        val errorMessage = remember { mutableStateOf<String?>(null) }
        val loading = remember { mutableStateOf(false) }

        var passwordVisible by remember { mutableStateOf(false) }

        val uiState = viewModel.uiState.collectAsState()
        when(uiState.value){
            is SignInViewModel.SignInEvent.Error -> {
                loading.value = false
                errorMessage.value = "Failed"
            }
            is SignInViewModel.SignInEvent.Loading -> {
                loading.value = true
                errorMessage.value = null
            }
            else -> {
                loading.value = false
                errorMessage.value = null
            }
        }

        LaunchedEffect(true) {
            viewModel.navigationEvent.collectLatest { event ->
                when(event){
                    is SignInViewModel.SignInNavigationEvent.NavigationToHome -> {
                        navController.navigate(Home){
                            popUpTo(AuthScreen){
                                inclusive = true
                            }
                        }
                    }

                    is SignInViewModel.SignInNavigationEvent.NavigationToSignUp -> {
                        navController.navigate(SignUp)
                    }


                }

            }
        }

        Image(
            painter = painterResource(id = R.drawable.auth_bg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(modifier = Modifier.weight(1f))

            Text(
                text = stringResource(R.string.sign_in),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.size(20.dp))

            Spacer(modifier = Modifier.height(12.dp))

            FoodHubTextField(
                value = email.value,
                onValueChange = {viewModel.onEmailChange(it)},
                label = {
                    Text(
                        text = stringResource(id = R.string.email),
                        color = Color.Gray,
                        fontWeight = FontWeight.W400,
                        fontSize = 16.sp
                    )
                },
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(12.dp))

            FoodHubTextField(
                value = password.value,
                onValueChange = {viewModel.onPasswordChange(it)},
                label = {
                    Text(text = stringResource(
                        id = R.string.password),
                        color = Color.Gray,
                        fontWeight = FontWeight.W400,
                        fontSize = 16.sp
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation =
                    if (passwordVisible) VisualTransformation.None
                    else PasswordVisualTransformation(),
                trailingIcon = {
                    Icon(
                        painter = painterResource(
                            if (passwordVisible)
                                R.drawable.ic_eye_off
                            else
                                R.drawable.ic_eye
                        ),
                        contentDescription = "Toggle password visibility",
                        tint = Color.LightGray,
                        modifier = Modifier.clickable {
                            passwordVisible = !passwordVisible
                        }
                    )
                }
            )

            Spacer(modifier = Modifier.size(16.dp))

            Text(text = errorMessage.value ?: "", color = Color.Red)
            Button(
                onClick = viewModel::onSignInClick,
                modifier = Modifier
                    .height(60.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Orange)
            ) {
                Box{
                    AnimatedContent(
                        targetState = loading.value,
                        transitionSpec = {
                            fadeIn(animationSpec = tween(300)) +
                                    scaleIn(initialScale = 0.8f) togetherWith
                                    fadeOut(animationSpec = tween(300)) +
                                    scaleOut(targetScale = 0.8f)
                        }
                    ) { target ->
                        if(target) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .padding(horizontal = 48.dp),
                                color = Color.White
                            )
                        }else{
                            Text(
                                text = stringResource(id = R.string.sign_in),
                                modifier = Modifier
                                    .padding(horizontal = 48.dp),
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp
                            )
                        }
                    }


                }


            }


            Spacer(modifier = Modifier.size(16.dp))

            Text(
                text = stringResource(id = R.string.dont_have_account),
                modifier = Modifier
                    .padding(8.dp)
                    .clickable {
                        viewModel.onSignUpClicked()
                    }
                    .fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.padding(16.dp))

            val context = LocalContext.current
            GroupSocialButtons(
                onFacebookClick = { },
                onGoogleClick = {
                    viewModel.onGoogleSignInClicked(context)
                },
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewSignInScreen(){
    val navController = rememberNavController()
    SignInScreen(navController = navController)
}