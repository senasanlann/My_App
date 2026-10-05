package com.example.my_app.model
import androidx.compose.ui.graphics.Color
enum class TaskStatus(val displayName: String, val color: Color) {
    TODO(displayName = "Yapılacak", color = Color(0xFFE57373)),
    IN_PROGRESS(displayName = "Devam Ediyor", color = Color(0xFFFFB74D)),
    DONE(displayName = "Tamamlandı", color = Color(0xFF81C784))
}