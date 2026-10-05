package com.example.my_app.ui.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Register : Screen("register")
    object TaskList : Screen("task_list")
    object TaskDetail : Screen("task_detail")
    object AddEditTask : Screen("add_edit_task")
    object Profile : Screen("profile")
    object Settings : Screen("settings")
}
