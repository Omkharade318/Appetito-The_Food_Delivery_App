package com.example.appetito

import android.animation.ObjectAnimator
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.OvershootInterpolator
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
import androidx.compose.foundation.layout.BoxScope
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
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
import com.example.appetito.ui.feature.add_address.AddAddressScreen
import com.example.appetito.ui.feature.address_list.AddressListScreen
import com.example.appetito.ui.feature.cart.CartScreen
import com.example.appetito.ui.feature.cart.CartViewModel
import com.example.appetito.ui.feature.food_item_details.FoodDetailsScreen
import com.example.appetito.ui.feature.home.HomeScreen
import com.example.appetito.ui.feature.order_details.OrderDetailsScreen
import com.example.appetito.ui.feature.restaurant_details.RestaurantDetailsScreen
import com.example.appetito.ui.features.auth.AuthScreen
import com.example.appetito.ui.features.auth.login.SignInScreen
import com.example.appetito.ui.features.auth.signup.SignUpScreen
import com.example.appetito.ui.features.notifications.NotificationsList
import com.example.appetito.ui.features.notifications.NotificationsViewModel
import com.example.appetito.ui.feature.orders.OrderListScreen
import com.example.appetito.ui.navigation.AddAddress
import com.example.appetito.ui.navigation.AddressList
import com.example.appetito.ui.navigation.AuthScreen
import com.example.appetito.ui.navigation.Cart
import com.example.appetito.ui.navigation.FoodDetails
import com.example.appetito.ui.navigation.Home
import com.example.appetito.ui.navigation.Login
import com.example.appetito.ui.navigation.NavRoutes
import com.example.appetito.ui.navigation.Notification
import com.example.appetito.ui.navigation.OrderDetails
import com.example.appetito.ui.navigation.OrderList
import com.example.appetito.ui.feature.order_success.OrderSuccess
import com.example.appetito.ui.navigation.OrderSuccess
import com.example.appetito.ui.navigation.RestaurantDetails
import com.example.appetito.ui.navigation.SignUp
import com.example.appetito.ui.navigation.foodItemNavType
import com.example.appetito.ui.theme.AppetitoTheme
import com.example.appetito.ui.theme.Mustard
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.reflect.typeOf


@AndroidEntryPoint
class MainActivity : BaseFoodHubActivity() {
    var showSplashScreen = true

    @Inject
    lateinit var foodApi: FoodApi

    @Inject
    lateinit var session: FoodHubSession


    sealed class BottomNavItem(val route: NavRoutes, val icon: Int) {
        object Home : BottomNavItem(com.example.appetito.ui.navigation.Home, R.drawable.ic_home)
        object Cart : BottomNavItem(com.example.appetito.ui.navigation.Cart, R.drawable.ic_cart2)
        object Notification :
            BottomNavItem(
                com.example.appetito.ui.navigation.Notification,
                R.drawable.ic_notification
            )

        object Orders : BottomNavItem(
            com.example.appetito.ui.navigation.OrderList,
            R.drawable.ic_orders
        )
    }

    @OptIn(ExperimentalSharedTransitionApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().apply {
            setKeepOnScreenCondition {
                showSplashScreen
            }
            setOnExitAnimationListener { screen ->
                val zoomX = ObjectAnimator.ofFloat(
                    screen.iconView,
                    View.SCALE_X,
                    0.5f,
                    0f
                )
                val zoomY = ObjectAnimator.ofFloat(
                    screen.iconView,
                    View.SCALE_Y,
                    0.5f,
                    0f
                )
                zoomX.duration = 500
                zoomY.duration = 500
                zoomX.interpolator = OvershootInterpolator()
                zoomY.interpolator = OvershootInterpolator()
                zoomX.doOnEnd {
                    screen.remove()
                }
                zoomY.doOnEnd {
                    screen.remove()
                }
                zoomY.start()
                zoomX.start()
            }
        }
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppetitoTheme {

                val shouldShowBottomNav = remember {
                    mutableStateOf(false)
                }
                val navItems = listOf(
                    BottomNavItem.Home,
                    BottomNavItem.Cart,
                    BottomNavItem.Notification,
                    BottomNavItem.Orders
                )
                val navController = rememberNavController()
                val cartViewModel: CartViewModel = hiltViewModel()
                val cartItemSize = cartViewModel.cartItemCount.collectAsStateWithLifecycle()
                val notificationViewModel: NotificationsViewModel = hiltViewModel()
                val unreadCount = notificationViewModel.unreadCount.collectAsStateWithLifecycle()

                LaunchedEffect(key1 = true) {
                    viewModel.event.collectLatest {
                        when (it) {
                            is HomeViewModel.HomeEvent.NavigateToOrderDetail -> {
                                navController.navigate(OrderDetails(it.orderID))
                            }
                        }
                    }
                }
                Scaffold(modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        val currentRoute =
                            navController.currentBackStackEntryAsState().value?.destination
                        AnimatedVisibility(visible = shouldShowBottomNav.value) {
                            NavigationBar(
                                containerColor = Color.White
                            ) {
                                navItems.forEach { item ->
                                    val selected =
                                        currentRoute?.hierarchy?.any { it.route == item.route::class.qualifiedName } == true

                                    NavigationBarItem(
                                        selected = selected,
                                        onClick = {
                                            navController.navigate(item.route)
                                        },
                                        icon = {
                                            Box(modifier = Modifier.size(48.dp)) {
                                                Icon(
                                                    painter = painterResource(id = item.icon),
                                                    contentDescription = null,
                                                    tint = if (selected) MaterialTheme.colorScheme.primary else Color.Gray,
                                                    modifier = Modifier.align(Center)
                                                )

                                                if (item.route == Cart && cartItemSize.value > 0) {
                                                    ItemCount(cartItemSize.value)
                                                }
                                                if(item.route == Notification && unreadCount.value > 0) {
                                                    ItemCount(unreadCount.value)
                                                }
                                            }
                                        })
                                }
                            }
                        }
                    }) { innerPadding ->

                    SharedTransitionLayout {
                        NavHost(
                            navController = navController,
                            startDestination = if (session.getToken() != null) Home else AuthScreen,
                            modifier = Modifier.padding(innerPadding),
                            enterTransition = {
                                slideIntoContainer(
                                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                                    animationSpec = tween(300)
                                ) + fadeIn(animationSpec = tween(300))
                            },
                            exitTransition = {
                                slideOutOfContainer(
                                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                                    animationSpec = tween(300)
                                ) + fadeOut(animationSpec = tween(300))
                            },
                            popEnterTransition = {
                                slideIntoContainer(
                                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                                    animationSpec = tween(300)
                                ) + fadeIn(animationSpec = tween(300))
                            },
                            popExitTransition = {
                                slideOutOfContainer(
                                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                                    animationSpec = tween(300)
                                ) + fadeOut(animationSpec = tween(300))
                            }
                        ) {
                            composable<SignUp> {
                                shouldShowBottomNav.value = false
                                SignUpScreen(navController)
                            }
                            composable<AuthScreen> {
                                shouldShowBottomNav.value = false
                                AuthScreen(navController)
                            }
                            composable<Login> {
                                shouldShowBottomNav.value = false
                                SignInScreen(navController)
                            }
                            composable<Home> {
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
                                    restaurantID = route.restaurantId,
                                    this
                                )
                            }
                            composable<FoodDetails>(
                                typeMap = mapOf(typeOf<FoodItem>() to foodItemNavType)
                            ) {
                                shouldShowBottomNav.value = false
                                val route = it.toRoute<FoodDetails>()
                                FoodDetailsScreen(
                                    navController,
                                    foodItem = route.foodItems,
                                    this,
                                    onItemAddedToCart = { cartViewModel.getCart() }
                                )
                            }

                            composable<Cart>() {
                                shouldShowBottomNav.value = true
                                CartScreen(navController, cartViewModel)
                            }
                            composable<Notification> {
                                SideEffect {
                                    shouldShowBottomNav.value = true
                                }
                                NotificationsList(navController, notificationViewModel)
                            }
                            composable<AddressList> {
                                shouldShowBottomNav.value = false
                                AddressListScreen(navController)
                            }
                            composable<AddAddress> {
                                shouldShowBottomNav.value = false
                                AddAddressScreen(navController)
                            }
                            composable<OrderSuccess> {
                                shouldShowBottomNav.value = false
                                val orderID = it.toRoute<OrderSuccess>().orderId
                                OrderSuccess(orderID, navController)
                            }
                            composable<OrderList> {
                                shouldShowBottomNav.value = true
                                OrderListScreen(navController)
                            }

                            composable<OrderDetails> {
                                SideEffect {
                                    shouldShowBottomNav.value = false
                                }
                                val orderID = it.toRoute<OrderDetails>().orderId
                                OrderDetailsScreen(navController, orderID)
                            }

                        }
                    }

                }
            }
        }

        if (::foodApi.isInitialized) {
            Log.d("MainActivity", "FoodApi initialized")
        }
        CoroutineScope(Dispatchers.IO).launch {
            delay(3000)
            showSplashScreen = false
            processIntent(intent, viewModel)
        }
    }
}

@Composable
fun BoxScope.ItemCount(count: Int) {
    Box(
        modifier = Modifier
            .size(16.dp)
            .clip(CircleShape)
            .background(Mustard)
            .align(Alignment.TopEnd)
    ) {
        Text(
            text = "${count}",
            modifier = Modifier
                .align(Alignment.Center),
            color = Color.White,
            style = TextStyle(fontSize = 10.sp)
        )
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