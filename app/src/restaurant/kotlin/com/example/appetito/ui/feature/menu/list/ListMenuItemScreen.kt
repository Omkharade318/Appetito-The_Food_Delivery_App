package com.example.appetito.ui.feature.menu.list

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import /* androidx.compose.foundation.layout.Column */ androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.appetito.ui.features.common.FoodItemView
import com.example.appetito.ui.features.notifications.ErrorScreen
import com.example.appetito.ui.features.notifications.LoadingScreen
import com.example.appetito.ui.navigation.AddMenu
import kotlinx.coroutines.flow.collectLatest

val PrimaryOrange = Color(0xFFFE724C)
val TextGray = Color(0xFF9796A1)
val BgLight = Color(0xFFF9F9FB)

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.ListMenuItemsScreen(
    navController: NavController,
    animatedVisibilityScope: AnimatedVisibilityScope,
    viewModel: ListMenuItemViewModel = hiltViewModel()
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgLight)
    ) {
        val uiState = viewModel.listMenuItemState.collectAsStateWithLifecycle()
        LaunchedEffect(key1 = true) {
            viewModel.menuItemEvent.collectLatest {
                when (it) {
                    is ListMenuItemViewModel.MenuItemEvent.AddNewMenuItem -> {
                        navController.currentBackStackEntry?.savedStateHandle?.remove<Boolean>("added")
                        navController.navigate(AddMenu)
                    }
                }
            }
        }
        val isItemAdded =
            navController.currentBackStackEntry?.savedStateHandle?.getStateFlow<Boolean>(
                "added",
                false
            )?.collectAsState()
        LaunchedEffect(key1 = isItemAdded?.value) {
            if (isItemAdded?.value == true) {
                viewModel.retry()
            }
        }

        Column(modifier = Modifier.fillMaxSize()) {
            Text(
                text = "Menu Items",
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                color = Color(0xFF323643),
                modifier = Modifier.padding(start = 24.dp, top = 24.dp, bottom = 16.dp)
            )

            when (val state = uiState.value) {
                is ListMenuItemViewModel.ListMenuItemState.Loading -> {
                    LoadingScreen()
                }

                is ListMenuItemViewModel.ListMenuItemState.Success -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 88.dp)
                    ) {
                        items(state.data, key = { it.id ?: "" }) { item ->
                            Box(modifier = Modifier.padding(8.dp)) {
                                FoodItemView(item, animatedVisibilityScope) {
                                    //navController.navigate(FoodDetails.route)
                                }
                            }
                        }
                    }
                }

                is ListMenuItemViewModel.ListMenuItemState.Error -> {
                    ErrorScreen(message = state.message) {
                        viewModel.retry()
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { viewModel.onAddItemClicked() },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
                .size(64.dp),
            containerColor = PrimaryOrange,
            contentColor = Color.White,
            shape = CircleShape
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Item",
                modifier = Modifier.size(32.dp)
            )
        }
    }
}