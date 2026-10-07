package com.example.my_app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.my_app.GorevlerimApp
import com.example.my_app.model.FilterState
import com.example.my_app.ui.auth.LoginScreen
import com.example.my_app.ui.auth.RegisterScreen
import com.example.my_app.ui.categories.CategoryGridScreen
import com.example.my_app.ui.profile.ProfileScreen
import com.example.my_app.ui.settings.SettingsScreen
import com.example.my_app.ui.splash.SplashScreen
import com.example.my_app.ui.tasks.AddEditTaskScreen
import com.example.my_app.ui.tasks.TaskDetailScreen
import com.example.my_app.ui.tasks.TaskListScreen

@Composable
fun AppNavGraph() {
    val container = (LocalContext.current.applicationContext as GorevlerimApp).container
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    val bottomBarRoutes = listOf(Screen.CategoryGrid.route, Screen.Profile.route, Screen.Settings.route)

    Scaffold(
        bottomBar = {
            if (currentRoute in bottomBarRoutes) {
                AppBottomBar(navController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Splash.route) {
                SplashScreen(
                    onNavigateToLogin = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    },
                    onNavigateToTaskList = {
                        navController.navigate(Screen.CategoryGrid.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    }
                )
            }
            composable(Screen.Login.route) {
                LoginScreen(
                    onNavigateToRegister = {
                        navController.navigate(Screen.Register.route)
                    },
                    onLoginSuccess = {
                        navController.navigate(Screen.CategoryGrid.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
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
            composable(Screen.CategoryGrid.route) {
                CategoryGridScreen(
                    onCategoryClick = { categoryId, _ ->
                        container.pendingTaskFilter.value = FilterState(categoryId = categoryId)
                        navController.navigate(Screen.TaskList.route) {
                            launchSingleTop = true
                        }
                    },
                    onAddClick = {
                        navController.navigate(Screen.AddEditTask.createRoute())
                    }
                )
            }
            composable(Screen.TaskList.route) { backStackEntry ->
                val taskDeleted by backStackEntry.savedStateHandle
                    .getStateFlow("taskDeleted", false)
                    .collectAsState()
                val pendingFilter by container.pendingTaskFilter.collectAsState()

                TaskListScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onTaskClick = { taskId ->
                        navController.navigate(Screen.TaskDetail.createRoute(taskId))
                    },
                    onAddClick = {
                        navController.navigate(Screen.AddEditTask.createRoute())
                    },
                    showDeletedMessage = taskDeleted,
                    onDeletedMessageShown = {
                        backStackEntry.savedStateHandle["taskDeleted"] = false
                    },
                    initialFilter = pendingFilter,
                    onInitialFilterConsumed = {
                        container.pendingTaskFilter.value = null
                    }
                )
            }
            composable(
                route = Screen.TaskDetail.route,
                arguments = listOf(navArgument("taskId") { type = NavType.LongType })
            ) { backStackEntry ->
                TaskDetailScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onTaskDeleted = {
                        navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set("taskDeleted", true)
                        navController.popBackStack()
                    },
                    onEditClick = { taskId ->
                        navController.navigate(Screen.AddEditTask.createRoute(taskId))
                    },
                    argsBundle = backStackEntry.arguments
                )
            }
            composable(
                route = Screen.AddEditTask.route,
                arguments = listOf(navArgument("taskId") {
                    type = NavType.LongType
                    defaultValue = -1L
                })
            ) { backStackEntry ->
                AddEditTaskScreen(
                    onNavigateBack = { navController.popBackStack() },
                    argsBundle = backStackEntry.arguments
                )
            }
            composable(Screen.Profile.route) {
                ProfileScreen(
                    onLogout = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0)
                        }
                    },
                    onStatClick = { status ->
                        container.pendingTaskFilter.value = FilterState(status = status)
                        navController.navigate(Screen.TaskList.route) {
                            launchSingleTop = true
                        }
                    }
                )
            }
            composable(Screen.Settings.route) { SettingsScreen() }
        }
    }
}
