package com.example.my_app.ui.tasks

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun TaskListScreen() {
    Scaffold { innerPadding ->
        Text(
            text = "Task List Ekranı (henüz boş)",
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        )
    }
}