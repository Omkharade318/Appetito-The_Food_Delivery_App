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
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.appetito.data.FoodApi
import com.example.appetito.data.FoodHubSession
import com.example.appetito.data.models.FoodItem
import com.example.appetito.ui.features.add_address.AddAddressScreen
import com.example.appetito.ui.features.address_list.AddressListScreen
import com.example.appetito.ui.features.auth.AuthScreen
import com.example.appetito.ui.features.auth.login.SignInScreen
import com.example.appetito.ui.features.auth.signup.SignUpScreen
import com.example.appetito.ui.features.cart.CartScreen
import com.example.appetito.ui.features.cart.CartViewModel
import com.example.appetito.ui.features.food_item_details.FoodDetailsScreen
import com.example.appetito.ui.features.home.HomeScreen
import com.example.appetito.ui.features.restaurant_details.RestaurantDetailsScreen
import com.example.appetito.ui.navigation.AddAddress
import com.example.appetito.ui.navigation.AddressList
import com.example.appetito.ui.navigation.AuthScreen
import com.example.appetito.ui.navigation.Cart
import com.example.appetito.ui.navigation.FoodDetails
import com.example.appetito.ui.navigation.Home
import com.example.appetito.ui.navigation.Login
import com.example.appetito.ui.navigation.NavRoutes
import com.example.appetito.ui.navigation.Notification
import com.example.appetito.ui.navigation.RestaurantDetails
import com.example.appetito.ui.navigation.SignUp
import com.example.appetito.ui.navigation.foodItemNavType
import com.example.appetito.ui.theme.AppetitoTheme
import com.example.appetito.ui.theme.Mustard
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.reflect.typeOf

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    // Controls whether to keep the splash screen visible
    var showSplashScreen = true

    @Inject
    lateinit var foodApi: FoodApi
    @Inject
    lateinit var session: FoodHubSession

    sealed class BottomNavItem(val route: NavRoutes, val  icon: Int){
        object Home: BottomNavItem(com.example.appetito.ui.navigation.Home, R.drawable.ic_home)
        object Cart: BottomNavItem(com.example.appetito.ui.navigation.Cart, R.drawable.ic_cart2)
        object Notification: BottomNavItem(com.example.appetito.ui.navigation.Notification, R.drawable.ic_notification)
    }

    @OptIn(ExperimentalSharedTransitionApi::class)
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

                val shouldShowBottomNav = remember {
                    mutableStateOf(false)
                }
                val navItems= listOf(
                    BottomNavItem.Home,
                    BottomNavItem.Cart,
                    BottomNavItem.Notification
                )

                val navController = rememberNavController()
                val cartViewModal: CartViewModel = hiltViewModel()

                val cartItemSize = cartViewModal.cartItemCount.collectAsStateWithLifecycle()

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {

                        val currentRoute = navController.currentBackStackEntryAsState().value?.destination

                        AnimatedVisibility(visible = shouldShowBottomNav.value) {
                            NavigationBar(
                                containerColor = Color.White
                            ) {
                                navItems.forEach { item ->

                                    val  selected = currentRoute?.hierarchy?.any { it.route == item.route::class.qualifiedName } == true

                                    NavigationBarItem(
                                        selected = selected,
                                        onClick = {
                                            navController.navigate(item.route)
                                        },
                                        icon = {
                                            Box{

                                                Icon(
                                                    painter = painterResource(id = item.icon),
                                                    contentDescription = null,
                                                    tint = if (selected) MaterialTheme.colorScheme.primary else Color.Gray,
                                                    modifier = Modifier.align(Alignment.Center)
                                                )

                                                if (item.route== Cart && cartItemSize.value > 0) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(16.dp)
                                                            .clip(CircleShape)
                                                            .background(Mustard)
                                                            .align(Alignment.TopEnd)
                                                    ) {
                                                        Text(
                                                            text = "${cartItemSize.value}",
                                                            modifier = Modifier
                                                                .align(Alignment.Center),
                                                            color = Color.White,
                                                            style = TextStyle(fontSize = 10.sp)
                                                        )
                                                    }
                                                }

                                            }
                                        }
                                    )
                                }
                            }
                        }

                    }
                ) { innerPadding ->

                    SharedTransitionLayout {
                        NavHost(
                            navController = navController,
                            startDestination = if(session.getToken() != null) Home else AuthScreen,
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
                                shouldShowBottomNav.value = false
                                SignUpScreen(navController)
                            }

                            composable<AuthScreen>() {
                                shouldShowBottomNav.value = false
                                AuthScreen(navController)
                            }

                            composable<Login>() {
                                shouldShowBottomNav.value = false
                                SignInScreen(navController)
                            }

                            composable<Home>() {
                                shouldShowBottomNav.value = true
                                HomeScreen(navController, this)
                            }

                            composable<RestaurantDetails> {
                                shouldShowBottomNav.value = false
                                val route = it.toRoute<RestaurantDetails>()

                                RestaurantDetailsScreen(
                                    navController,
                                    name = route.restaurantName,
                                    imageUrl = route.restaurantImageUrl,
                                    restaurantId = route.restaurantId,
                                    this
                                )
                            }

                            composable<FoodDetails>(
                                typeMap = mapOf(typeOf<FoodItem>() to foodItemNavType)
                            ) {
                                shouldShowBottomNav.value = false
                                val route = it.toRoute<FoodDetails>()

                                FoodDetailsScreen(
                                    navController = navController,
                                    foodItem = route.foodItems,
                                    this,
                                    onItemAddedToCart = {
                                        cartViewModal.getCart()
                                    }
                                )
                            }

                            composable<Cart>() {
                                shouldShowBottomNav.value = true
                                CartScreen(navController, cartViewModal)
                            }

                            composable<Notification>{
                                shouldShowBottomNav.value = true
                                Box{

                                }
                            }

                            composable<AddressList>{
                                shouldShowBottomNav.value = false
                                AddressListScreen(navController)
                            }

                            composable<AddAddress>{
                                shouldShowBottomNav.value = false
                                AddAddressScreen(navController)
                            }

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
