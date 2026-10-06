package com.abhay.brewkery

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.abhay.brewkery.data.model.MenuItem
import com.abhay.brewkery.data.repository.UiState
import com.abhay.brewkery.screen.CartScreen
import com.abhay.brewkery.screen.ItemDetailScreen
import com.abhay.brewkery.screen.MenuScreen
import com.abhay.brewkery.screen.OrderScreen
import com.abhay.brewkery.ui.CartViewModel
import com.abhay.brewkery.ui.ItemDetailViewModel
import com.abhay.brewkery.ui.MenuViewModel
import com.abhay.brewkery.ui.theme.BrewkeryTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BrewkeryTheme {
                BrewkeryApp()
            }
        }
    }
}

@Composable
fun BrewkeryApp() {
    val navController = rememberNavController()

    // Shared ViewModels
    val cartVm: CartViewModel = viewModel()
    val menuVm: MenuViewModel = viewModel()
    val detailVm: ItemDetailViewModel = viewModel()

    val menuState by menuVm.menuState.collectAsStateWithLifecycle()
    val storeMeta = (menuState as? UiState.Success)?.data?.meta

    NavHost(
        navController = navController,
        startDestination = "menu",
        modifier = Modifier.fillMaxSize()
    ) {
        // Screen 1: Home / Menu
        composable("menu") {
            MenuScreen(
                menuVm = menuVm,
                cartVm = cartVm,
                onItemClick = { item ->
                    navController.navigate("item_detail/${item.id}")
                },
                onCartClick = {
                    navController.navigate("cart")
                },
                onOrderClick = {
                    navController.navigate("order_status")
                }
            )
        }

        // Screen 2: Item Detail
        composable(
            route = "item_detail/{itemId}",
            arguments = listOf(navArgument("itemId") { type = NavType.IntType })
        ) { backStackEntry ->
            val itemId = backStackEntry.arguments?.getInt("itemId") ?: 1
            val itemsList = (menuState as? UiState.Success)?.data?.items
            val fallbackItem: MenuItem? = itemsList?.find { it.id == itemId }

            ItemDetailScreen(
                itemId = itemId,
                fallbackItem = fallbackItem,
                detailVm = detailVm,
                cartVm = cartVm,
                onBackClick = { navController.popBackStack() },
                onCartClick = { navController.navigate("cart") }
            )
        }

        // Screen 3: Cart
        composable("cart") {
            CartScreen(
                cartVm = cartVm,
                storeMeta = storeMeta,
                onBackClick = { navController.popBackStack() },
                onPlaceOrderSuccess = {
                    navController.navigate("order_status") {
                        popUpTo("menu") { inclusive = false }
                    }
                },
                onBrowseMenuClick = {
                    navController.popBackStack()
                }
            )
        }

        // Screen 4: Order Status
        composable("order_status") {
            OrderScreen(
                cartVm = cartVm,
                onBackToMenuClick = {
                    navController.navigate("menu") {
                        popUpTo("menu") { inclusive = true }
                    }
                }
            )
        }
    }
}
