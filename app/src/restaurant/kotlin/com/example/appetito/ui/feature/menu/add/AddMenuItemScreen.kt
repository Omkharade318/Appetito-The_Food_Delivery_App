package com.example.appetito.ui.feature.menu.add

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.example.appetito.ui.FoodHubTextField
import com.example.appetito.ui.navigation.ImagePicker
import kotlinx.coroutines.flow.collectLatest

val PrimaryOrange = Color(0xFFFE724C)
val TextGray = Color(0xFF9796A1)
val BgLight = Color(0xFFF9F9FB)

@Composable
fun AddMenuItemScreen(
    navController: NavController,
    viewModel: AddMenuItemViewModel = hiltViewModel()
) {

    val name = viewModel.name.collectAsStateWithLifecycle()
    val description = viewModel.description.collectAsStateWithLifecycle()
    val price = viewModel.price.collectAsStateWithLifecycle()
    val uiState = viewModel.addMenuItemState.collectAsStateWithLifecycle()
    val selectedImage = viewModel.imageUrl.collectAsStateWithLifecycle()

    val imageUri =
        navController.currentBackStackEntry?.savedStateHandle?.getStateFlow<Uri?>("imageUri", null)
            ?.collectAsStateWithLifecycle()
    LaunchedEffect(key1 = imageUri?.value) {
        imageUri?.value?.let {
            viewModel.onImageUrlChange(it)
        }
    }
    LaunchedEffect(key1 = true) {
        viewModel.addMenuItemEvent.collectLatest {
            when (it) {
                is AddMenuItemViewModel.AddMenuItemEvent.GoBack -> {
                    Toast.makeText(
                        navController.context, "Item added Successfully", Toast.LENGTH_SHORT
                    ).show()
                    navController.previousBackStackEntry?.savedStateHandle?.set("added", true)
                    navController.popBackStack()
                }

                is AddMenuItemViewModel.AddMenuItemEvent.AddNewImage -> {
                    navController.navigate(ImagePicker)
                }

                is AddMenuItemViewModel.AddMenuItemEvent.ShowErrorMessage -> {
                    Toast.makeText(navController.context, it.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgLight)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Add Menu Item",
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp,
            color = Color(0xFF323643),
            modifier = Modifier.padding(start = 24.dp, top = 24.dp, bottom = 16.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .shadow(elevation = 8.dp, shape = RoundedCornerShape(16.dp), spotColor = Color(0x1A000000))
                .background(Color.White, RoundedCornerShape(16.dp))
                .padding(20.dp)
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF1F2F6))
                        .border(
                            width = 2.dp,
                            color = PrimaryOrange.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable { viewModel.onImageClicked() },
                    contentAlignment = Alignment.Center
                ) {
                    if (selectedImage.value == null) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add Image",
                                tint = PrimaryOrange,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Tap to upload image",
                                color = TextGray,
                                fontSize = 14.sp
                            )
                        }
                    } else {
                        AsyncImage(
                            model = selectedImage.value,
                            contentDescription = "Food Image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                FoodHubTextField(
                    value = name.value,
                    onValueChange = { viewModel.onNameChange(it) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = "Item Name") }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                FoodHubTextField(
                    value = description.value,
                    onValueChange = { viewModel.onDescriptionChange(it) },
                    modifier = Modifier.fillMaxWidth().height(100.dp),
                    label = { Text(text = "Description") },
                    singleLine = false
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                FoodHubTextField(
                    value = price.value,
                    onValueChange = { viewModel.onPriceChange(it) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = "Price ($)") }
                )

                Spacer(modifier = Modifier.height(32.dp))

                if (uiState.value is AddMenuItemViewModel.AddMenuItemState.Loading) {
                    Button(
                        onClick = { },
                        enabled = false,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(28.dp),
                        colors = ButtonDefaults.buttonColors(
                            disabledContainerColor = TextGray.copy(alpha = 0.5f),
                        )
                    ) {
                        Text(text = "Adding Item...", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                } else {
                    if (uiState.value is AddMenuItemViewModel.AddMenuItemState.Error) {
                        Text(
                            text = (uiState.value as AddMenuItemViewModel.AddMenuItemState.Error).message,
                            color = Color(0xFFF44336),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    Button(
                        onClick = { viewModel.addMenuItem() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .shadow(8.dp, RoundedCornerShape(28.dp), spotColor = PrimaryOrange),
                        shape = RoundedCornerShape(28.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryOrange
                        )
                    ) {
                        Text(text = "Save Menu Item", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}
