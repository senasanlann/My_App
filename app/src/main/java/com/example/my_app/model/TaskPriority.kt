package com.example.my_app.model

import androidx.compose.ui.graphics.Color

enum class TaskPriority(val displayName: String, val level: Int, val color: Color) {
    LOW(displayName = "Düşük", level = 1, color = Color(0xFF81C784)),
    MEDIUM(displayName = "Orta", level = 2, color = Color(0xFFFFB74D)),
    HIGH(displayName = "Yüksek", level = 3, color = Color(0xFFE57373))
}