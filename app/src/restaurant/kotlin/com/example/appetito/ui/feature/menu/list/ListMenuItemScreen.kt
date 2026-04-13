package com.example.appetito.ui.feature.menu.list

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.appetito.ui.features.common.FoodItemView
import com.example.appetito.ui.features.notifications.ErrorScreen
import com.example.appetito.ui.features.notifications.LoadingScreen
import com.example.appetito.ui.navigation.AddMenu
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.ListMenuItemsScreen(
    navController: NavController,
    animatedVisibilityScope: AnimatedVisibilityScope,
    viewModel: ListMenuItemViewModel = hiltViewModel()
) {
    Box {
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

        when (val state = uiState.value) {
            is ListMenuItemViewModel.ListMenuItemState.Loading -> {
                LoadingScreen()
            }

            is ListMenuItemViewModel.ListMenuItemState.Success -> {
                LazyVerticalGrid(columns = GridCells.Fixed(2)) {
                    items(state.data, key = { it.id ?: "" }) { item ->
                        FoodItemView(item, animatedVisibilityScope) {
                            //navController.navigate(FoodDetails.route)
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

        Button(
            onClick = { viewModel.onAddItemClicked() },
            modifier = Modifier.align(Alignment.BottomEnd)
        ) {
            Text(text = "Add Item")
        }
    }

}