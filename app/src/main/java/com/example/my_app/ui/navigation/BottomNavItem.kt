package com.example.my_app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(val route: String, val label: String, val icon: ImageVector) {
    object Tasks : BottomNavItem(Screen.CategoryGrid.route, "Görevler", Icons.Outlined.GridView)
    object Profile : BottomNavItem(Screen.Profile.route, "Profil", Icons.Outlined.Person)
    object Settings : BottomNavItem(Screen.Settings.route, "Ayarlar", Icons.Outlined.Settings)
}