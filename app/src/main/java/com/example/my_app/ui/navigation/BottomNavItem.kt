package com.example.my_app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(val route: String, val label: String, val icon: ImageVector) {
    object Tasks : BottomNavItem(Screen.TaskList.route, "Görevler", Icons.Filled.List)
    object Profile : BottomNavItem(Screen.Profile.route, "Profil", Icons.Filled.Person)
    object Settings : BottomNavItem(Screen.Settings.route, "Ayarlar", Icons.Filled.Settings)
}