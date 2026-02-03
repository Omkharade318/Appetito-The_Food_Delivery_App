package com.example.appetito

import android.animation.ObjectAnimator
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.appetito.data.FoodApi
import com.example.appetito.ui.features.auth.AuthScreen
import com.example.appetito.ui.features.auth.login.SignInScreen
import com.example.appetito.ui.features.auth.signup.SignUpScreen
import com.example.appetito.ui.navigation.AuthScreen
import com.example.appetito.ui.navigation.Home
import com.example.appetito.ui.navigation.Login
import com.example.appetito.ui.navigation.SignUp
import com.example.appetito.ui.theme.AppetitoTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    // Controls whether to keep the splash screen visible
    var showSplashScreen = true

    @Inject
    lateinit var foodApi: FoodApi

    override fun onCreate(savedInstanceState: Bundle?) {
        // Initialize the splash screen before the super call
        installSplashScreen().apply {
            // Keep the splash screen visible as long as this condition returns true
            setKeepOnScreenCondition {
                showSplashScreen
            }

            // Customize the exit animation for the splash screen
            setOnExitAnimationListener { screen ->
                // Scale down animation on X axis
                val zoomX = ObjectAnimator.ofFloat(
                    screen.iconView,
                    View.SCALE_X,
                    0.5f,
                    0f
                )

                // Scale down animation on Y axis
                val zoomY = ObjectAnimator.ofFloat(
                    screen.iconView,
                    View.SCALE_Y,
                    0.5f,
                    0f
                )

                // Set animation duration
                zoomX.duration = 500
                zoomY.duration = 500

                // Use an overshoot interpolator for a bounce effect
                zoomX.interpolator = OvershootInterpolator()
                zoomY.interpolator = OvershootInterpolator()

                // Remove the splash screen view once animation ends
                zoomX.doOnEnd {
                    screen.remove()
                }
                zoomY.doOnEnd {
                    screen.remove()
                }

                // Start the animations
                zoomX.start()
                zoomY.start()
            }
        }

        super.onCreate(savedInstanceState)

        // Enables edge-to-edge layout (status and nav bars transparent)
        enableEdgeToEdge()

        setContent {
            AppetitoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = AuthScreen,
                        modifier = Modifier.padding(innerPadding),

                        enterTransition = {
                            slideIntoContainer(
                                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                                animationSpec = tween(300),
                            ) + fadeIn(animationSpec = tween(300))
                        },

                        exitTransition = {
                            slideOutOfContainer(
                                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                                animationSpec = tween(300),
                            ) + fadeOut(animationSpec = tween(300))
                        },

                        popEnterTransition = {
                            slideIntoContainer(
                                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                                animationSpec = tween(300),
                            ) + fadeIn(animationSpec = tween(300))
                        },

                        popExitTransition = {
                            slideOutOfContainer(
                                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                                animationSpec = tween(300),
                            ) + fadeOut(animationSpec = tween(300))
                        }
                    ){

                        composable<SignUp>() {
                            SignUpScreen(navController)
                        }

                        composable<AuthScreen>() {
                            AuthScreen(navController)
                        }

                        composable<Login>(
//                            enterTransition = {
//                                slideIntoContainer(
//                                    towards = AnimatedContentTransitionScope.SlideDirection.Up,
//                                    animationSpec = tween(300),
//                                ) + fadeIn(animationSpec = tween(300))
//                            },
//
//                            exitTransition = {
//                                slideOutOfContainer(
//                                    towards = AnimatedContentTransitionScope.SlideDirection.Up,
//                                    animationSpec = tween(300),
//                                ) + fadeOut(animationSpec = tween(300))
//                            },
//
//                            popEnterTransition = {
//                                slideIntoContainer(
//                                    towards = AnimatedContentTransitionScope.SlideDirection.Down,
//                                    animationSpec = tween(300),
//                                ) + fadeIn(animationSpec = tween(300))
//                            },
//
//                            popExitTransition = {
//                                slideOutOfContainer(
//                                    towards = AnimatedContentTransitionScope.SlideDirection.Down,
//                                    animationSpec = tween(300),
//                                ) + fadeOut(animationSpec = tween(300))
//                            }
                        ) {
                            SignInScreen(navController)
                        }

                        composable<Home>() {
                            Greeting("Home")
                        }

                    }
                }
            }
        }

        if(::foodApi.isInitialized){
            Log.d("MainActivity", "FoodApi is initialized")
        }

        CoroutineScope(Dispatchers.Main).launch {
            delay(3000) // adds a delay of 3secs
            showSplashScreen = false
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Hello $name!",
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AppetitoTheme {
        Greeting("Android")
    }
}
