package com.example.my_app.ui.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun LoginScreen(onNavigateToRegister: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(text = "Login Ekranı")
        Button(onClick = onNavigateToRegister) {
            Text(text = "Kayıt ol")
        }
    }
}