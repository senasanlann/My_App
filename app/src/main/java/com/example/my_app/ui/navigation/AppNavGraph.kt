package com.example.my_app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.my_app.ui.auth.LoginScreen
import com.example.my_app.ui.auth.RegisterScreen
import com.example.my_app.ui.profile.ProfileScreen
import com.example.my_app.ui.settings.SettingsScreen
import com.example.my_app.ui.splash.SplashScreen
import com.example.my_app.ui.tasks.AddEditTaskScreen
import com.example.my_app.ui.tasks.TaskDetailScreen
import com.example.my_app.ui.tasks.TaskListScreen

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Splash.route) { SplashScreen() }
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }
        composable(Screen.Register.route) {
            RegisterScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        composable(Screen.TaskList.route) { TaskListScreen() }
        composable(Screen.TaskDetail.route) { TaskDetailScreen() }
        composable(Screen.AddEditTask.route) { AddEditTaskScreen() }
        composable(Screen.Profile.route) { ProfileScreen() }
        composable(Screen.Settings.route) { SettingsScreen() }
    }
}