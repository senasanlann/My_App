package com.example.my_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.my_app.ui.navigation.AppNavGraph
import com.example.my_app.ui.theme.My_AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val settingsManager = (application as GorevlerimApp).container.settingsManager
        setContent {
            val systemDark = isSystemInDarkTheme()
            val darkTheme by settingsManager.darkThemeFlow.collectAsState(initial = systemDark)
            My_AppTheme(darkTheme = darkTheme) {
                AppNavGraph()
            }
        }
    }
}
