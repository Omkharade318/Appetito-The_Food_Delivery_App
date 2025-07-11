package com.example.appetito

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.appetito.ui.theme.AppetitoTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    // Controls whether to keep the splash screen visible
    var showSplashScreen = true

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

        // Call super.onCreate after splash setup
        super.onCreate(savedInstanceState)

        // Enables edge-to-edge layout (status and nav bars transparent)
        enableEdgeToEdge()

        setContent {
            AppetitoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }

        CoroutineScope(Dispatchers.Main).launch {
            delay(3000)
            showSplashScreen = false
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AppetitoTheme {
        Greeting("Android")
    }
}
