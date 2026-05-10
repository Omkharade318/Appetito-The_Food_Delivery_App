package com.example.appetito.ui.features.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.appetito.R
import com.example.appetito.ui.BasicDialog
import com.example.appetito.ui.GroupSocialButtons
import com.example.appetito.ui.navigation.Home
import com.example.appetito.ui.navigation.Login
import com.example.appetito.ui.navigation.SignUp
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

val PrimaryOrange = Color(0xFFFE724C)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    navController: NavController,
    isCustomer: Boolean = true,
    viewModel: AuthScreenViewModel = hiltViewModel()
) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showDialog by remember { mutableStateOf(false) }

    // A smoother gradient that darkens towards the bottom for perfect text readability
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            Color.Black.copy(alpha = 0.2f),
            Color.Black.copy(alpha = 0.6f),
            Color.Black.copy(alpha = 0.9f)
        )
    )

    LaunchedEffect(true) {
        viewModel.navigationEvent.collectLatest { event ->
            when (event) {
                is AuthScreenViewModel.AuthNavigationEvent.NavigateToHome -> {
                    navController.navigate(Home) {
                        popUpTo(com.example.appetito.ui.navigation.AuthScreen) { inclusive = true }
                    }
                }
                is AuthScreenViewModel.AuthNavigationEvent.NavigateToSignUp -> {
                    navController.navigate(SignUp)
                }
                is AuthScreenViewModel.AuthNavigationEvent.ShowErrorDialog -> {
                    showDialog = true
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // 1. Background Image
        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop // Crop usually looks better than FillBounds for photos
        )

        // 2. Gradient Overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(brush = backgroundGradient)
        )

        // 3. Frosted Skip Button
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 48.dp, end = 24.dp)
                .clip(RoundedCornerShape(30.dp))
                .background(Color.White.copy(alpha = 0.2f)) // Glassmorphism effect
                .clickable { /* Handle Skip */ }
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(text = stringResource(id = R.string.skip), color = Color.White, fontWeight = FontWeight.SemiBold)
        }

        // 4. Welcome Text Area
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 140.dp)
                .padding(horizontal = 24.dp)
        ) {
            Text(
                text = "Welcome to",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = stringResource(id = R.string.app_name),
                color = PrimaryOrange,
                fontSize = 44.sp, // Reduced slightly to fit better
                lineHeight = 52.sp, // Added explicitly to prevent text overlapping
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(top = 4.dp) // Little breathing room
            )
            Text(
                text = stringResource(id = R.string.app_description),
                color = Color(0xFFEAEAEC),
                fontSize = 18.sp,
                lineHeight = 24.sp,
                modifier = Modifier.padding(top = 16.dp, bottom = 16.dp)
            )
        }

        // 5. Actions / Bottom Section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isCustomer) {

                GroupSocialButtons(viewModel = viewModel)

                Spacer(modifier = Modifier.height(24.dp))

                // Solid Primary Button instead of transparent outline
                Button(
                    onClick = { navController.navigate(SignUp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.15f)),
                    shape = RoundedCornerShape(30.dp),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.5f)) // Frosty white outline
                ) {
                    Text(text = stringResource(id = R.string.sign_up) + " with Email", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Stylized Login Text
            Text(
                text = buildAnnotatedString {
                    append(stringResource(id = R.string.already_have_account) + " ")
                    withStyle(style = SpanStyle(color = PrimaryOrange, fontWeight = FontWeight.Bold, textDecoration = TextDecoration.Underline)) {
                        append("Log In")
                    }
                },
                color = Color.White,
                fontSize = 15.sp,
                modifier = Modifier
                    .padding(8.dp)
                    .clickable { navController.navigate(Login) }
            )
        }
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

@Preview(showBackground = true)
@Composable
fun AuthScreenPreview() {
    AuthScreen(rememberNavController())
}