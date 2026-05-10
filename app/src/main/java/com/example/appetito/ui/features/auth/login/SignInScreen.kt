package com.example.appetito.ui.features.auth.login

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.appetito.ui.navigation.AuthScreen
import com.example.appetito.ui.navigation.Home
import com.example.appetito.ui.navigation.SignUp
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.example.appetito.R
import com.example.appetito.ui.BasicDialog
import com.example.appetito.ui.FoodHubTextField
import com.example.appetito.ui.GroupSocialButtons

val PrimaryOrange = Color(0xFFFE724C)
val TextGray = Color(0xFF9796A1)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignInScreen(
    navController: NavController,
    isCutomer: Boolean = true,
    viewModel: SignInViewModel = hiltViewModel()
) {
    val email = viewModel.email.collectAsStateWithLifecycle()
    val password = viewModel.password.collectAsStateWithLifecycle()

    // UI States
    val errorMessage = remember { mutableStateOf<String?>(null) }
    val loading = remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) } // State for password toggle

    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(errorMessage.value) {
        if (errorMessage.value != null) {
            scope.launch { showDialog = true }
        }
    }

    val uiState = viewModel.uiState.collectAsState()
    when (uiState.value) {
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
            when (event) {
                is SignInViewModel.SigInNavigationEvent.NavigateToHome -> {
                    navController.navigate(Home) {
                        popUpTo(AuthScreen) { inclusive = true }
                    }
                }
                is SignInViewModel.SigInNavigationEvent.NavigateToSignUp -> {
                    navController.navigate(SignUp)
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        // 1. Background Image (Top Half)
        Image(
            painter = painterResource(id = R.drawable.food_bg),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp), // Fixed height so it stays at the top
            contentScale = ContentScale.Crop
        )

        // 2. White Card Container (Bottom Half)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()) // Prevents crash when keyboard opens
        ) {
            Spacer(modifier = Modifier.height(280.dp)) // Pushes the card down

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 600.dp)
                    .background(Color.White, RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                    .padding(horizontal = 24.dp, vertical = 32.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {

                    // Header Text
                    Text(
                        text = stringResource(id = R.string.sign_in),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF323643),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Start
                    )
                    Text(
                        text = "Welcome back!",
                        fontSize = 16.sp,
                        color = TextGray,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, bottom = 24.dp),
                        textAlign = TextAlign.Start
                    )

                    // Input Fields
                    FoodHubTextField(
                        value = email.value,
                        onValueChange = { viewModel.onEmailChange(it) },
                        label = { Text(text = stringResource(id = R.string.email), color = TextGray, fontSize = 14.sp) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    FoodHubTextField(
                        value = password.value,
                        onValueChange = { viewModel.onPasswordChange(it) },
                        label = { Text(text = stringResource(id = R.string.password), color = TextGray, fontSize = 14.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Image(
                                    painter = painterResource(id = if (passwordVisible) R.drawable.ic_eye else R.drawable.ic_eye_off), // Swap icon if you have eye-slash
                                    contentDescription = "Toggle Password Visibility",
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    )

                    // Error Message
                    if (errorMessage.value != null) {
                        Text(
                            text = errorMessage.value ?: "",
                            color = Color(0xFFFF4B4B),
                            fontSize = 14.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            textAlign = TextAlign.Start
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Sign In Button
                    Button(
                        onClick = viewModel::onSignInClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .shadow(elevation = 16.dp, shape = RoundedCornerShape(30.dp), spotColor = PrimaryOrange.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(30.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange)
                    ) {
                        AnimatedContent(
                            targetState = loading.value,
                            transitionSpec = {
                                fadeIn(animationSpec = tween(300)) + scaleIn(initialScale = 0.8f) togetherWith
                                        fadeOut(animationSpec = tween(300)) + scaleOut(targetScale = 0.8f)
                            }, label = "Loading State"
                        ) { targetLoading ->
                            if (targetLoading) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                            } else {
                                Text(
                                    text = stringResource(id = R.string.sign_in).uppercase(),
                                    color = Color.White,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Stylized "Don't have an account" link
                    if(isCutomer) {
                        Text(
                            text = buildAnnotatedString {
                                append(stringResource(id = R.string.dont_have_account) + " ")
                                withStyle(style = SpanStyle(color = PrimaryOrange, fontWeight = FontWeight.Bold, textDecoration = TextDecoration.Underline)) {
                                    append("Sign Up")
                                }
                            },
                            fontSize = 14.sp,
                            color = Color(0xFF323643),
                            modifier = Modifier
                                .padding(8.dp)
                                .clickable { viewModel.onSignUpClicked() }
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        GroupSocialButtons(
                            color = TextGray,
                            viewModel = viewModel,
                        )

                        Spacer(modifier = Modifier.height(24.dp)) // Bottom padding
                    }
                }
            }
        }
    }

    // Back Button Over Image
    Box(
        modifier = Modifier
            .padding(top = 40.dp, start = 24.dp)
            .size(44.dp)
            .background(Color.White.copy(alpha = 0.8f), RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .clickable { navController.popBackStack() },
        contentAlignment = Alignment.Center
    ) {
        Image(painter = painterResource(id = R.drawable.ic_back), contentDescription = "Back")
    }

    if (showDialog) {
        ModalBottomSheet(onDismissRequest = { showDialog = false }, sheetState = sheetState) {
            BasicDialog(
                title = viewModel.error,
                description = viewModel.errorDescription,
                onClick = {
                    scope.launch {
                        sheetState.hide()
                        showDialog = false
                    }
                }
            )
        }
    }
}