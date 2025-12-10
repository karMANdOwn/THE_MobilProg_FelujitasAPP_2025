package com.example.felujitas.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.felujitas.ui.screens.*
import com.example.felujitas.ui.viewmodel.*

//gomb adatstruktura
data class BottomNavItem(
    val screen: Screen,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val label: String
)

//gombok és navigáció beállitása
@Composable
fun RenovationApp(
    viewModelFactory: ViewModelFactory
) {
    val navController = rememberNavController()

    val bottomNavItems = listOf(
        BottomNavItem(Screen.Dashboard, Icons.Default.Home, "Főoldal"),
        BottomNavItem(Screen.Tasks, Icons.Default.CheckCircle, "Feladatok"),
        BottomNavItem(Screen.Materials, Icons.Default.ShoppingCart, "Anyagok"),
        BottomNavItem(Screen.Rooms, Icons.Default.MeetingRoom, "Szobák")
    )

    //alsó navigációs sáv és oldalkezelés
    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                bottomNavItems.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) },
                        selected = currentDestination?.hierarchy?.any {
                            it.route == item.screen.route
                        } == true,
                        onClick = {
                            navController.navigate(item.screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    )

    //navigációs útvonalak beállítáa
    { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Dashboard.route) {
                val viewModel: DashboardViewModel = viewModel(factory = viewModelFactory)
                DashboardScreen(
                    viewModel = viewModel,
                    onNavigateToRoom = { roomId ->
                        navController.navigate(Screen.Tasks.route)
                    }
                )
            }

            composable(Screen.Tasks.route) {
                val viewModel: TaskViewModel = viewModel(factory = viewModelFactory)
                TasksScreen(viewModel = viewModel)
            }

            composable(Screen.Materials.route) {
                val viewModel: MaterialViewModel = viewModel(factory = viewModelFactory)
                MaterialsScreen(viewModel = viewModel)
            }

            composable(Screen.Rooms.route) {
                val viewModel: RoomViewModel = viewModel(factory = viewModelFactory)
                RoomsScreen(viewModel = viewModel)
            }
        }
    }
}