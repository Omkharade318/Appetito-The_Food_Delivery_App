package com.example.appetito.ui.feature.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.example.appetito.ui.features.notifications.ErrorScreen
import com.example.appetito.ui.features.notifications.LoadingScreen
import com.example.appetito.R
import com.example.appetito.ui.navigation.CreateAd
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete

val PrimaryOrange = Color(0xFFFE724C)
val TextGray = Color(0xFF9796A1)
val BgLight = Color(0xFFF9F9FB)

@Composable
fun HomeScreen(navController: NavController, viewModel: HomeViewModel = hiltViewModel()) {

    val uiState = viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgLight)
    ) {
        when (uiState.value) {
            is HomeViewModel.HomeScreenState.Loading -> {
                LoadingScreen()
            }

            is HomeViewModel.HomeScreenState.Success -> {
                val restaurant = (uiState.value as HomeViewModel.HomeScreenState.Success).data
                
                Text(
                    text = "Welcome Back!",
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp,
                    color = Color(0xFF323643),
                    modifier = Modifier.padding(start = 24.dp, top = 24.dp, bottom = 16.dp)
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .shadow(elevation = 12.dp, shape = RoundedCornerShape(18.dp), spotColor = Color(0x1A000000))
                        .background(Color.White, RoundedCornerShape(18.dp))
                        .clip(RoundedCornerShape(18.dp))
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Image(
                            painter = painterResource(id = R.drawable.pizza),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp),
                            contentScale = ContentScale.Crop
                        )
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "Pizza Palace",
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = Color(0xFF323643)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = restaurant.address, 
                                fontSize = 14.sp,
                                color = TextGray
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Box(
                                modifier = Modifier
                                    .background(Color(0xFFF1F2F6), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "Joined ${restaurant.createdAt.take(10)}", 
                                    fontSize = 12.sp, 
                                    color = Color(0xFF8A8E9B), 
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Button(
                    onClick = { navController.navigate(CreateAd) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange)
                ) {
                    Text("Create New Advertisement", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }

                val ads = (uiState.value as HomeViewModel.HomeScreenState.Success).ads
                if (ads.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "Your Active Ads",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color(0xFF323643),
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    ads.forEach { ad ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp, vertical = 8.dp)
                                .shadow(elevation = 4.dp, shape = RoundedCornerShape(12.dp), spotColor = Color(0x1A000000))
                                .background(Color.White, RoundedCornerShape(12.dp))
                                .clip(RoundedCornerShape(12.dp))
                        ) {
                            androidx.compose.foundation.layout.Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AsyncImage(
                                    model = ad.imageUrl,
                                    contentDescription = ad.title,
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = ad.title,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = Color(0xFF323643)
                                    )
                                    Text(
                                        text = "Active",
                                        fontSize = 12.sp,
                                        color = Color(0xFF4CAF50),
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                                androidx.compose.material3.IconButton(
                                    onClick = { viewModel.deleteAd(ad.id) }
                                ) {
                                    androidx.compose.material3.Icon(
                                        imageVector = androidx.compose.material.icons.Icons.Default.Delete,
                                        contentDescription = "Delete Ad",
                                        tint = Color(0xFFE53935)
                                    )
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            is HomeViewModel.HomeScreenState.Failed -> {
                ErrorScreen(message = "Failed to load data") {
                    viewModel.retry()
                }
            }
        }
    }
}