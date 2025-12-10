package com.example.felujitas.navigation


//képernyők utvonala
sealed class Screen(val route: String) {
    object Dashboard : Screen("dashboard")
    object Tasks : Screen("tasks")
    object Materials : Screen("materials")
    object Rooms : Screen("rooms")
}