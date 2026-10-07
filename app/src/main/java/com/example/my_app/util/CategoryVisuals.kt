package com.example.my_app.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.BusinessCenter
import androidx.compose.material.icons.outlined.Eco
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Label
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import java.util.Locale

data class CategoryVisual(val icon: ImageVector, val color: Color)

object CategoryVisuals {

    fun of(name: String, fallbackIndex: Int = 0): CategoryVisual {
        val lower = name.lowercase(Locale("tr")).trim()
        return when {
            lower.contains("ev") || lower.contains("yaşam") ->
                CategoryVisual(Icons.Outlined.Home, Color(0xFF6D4C41))
            lower == "iş" || lower.contains("çalış") || lower.contains("kariyer") ->
                CategoryVisual(Icons.Outlined.BusinessCenter, Color(0xFF3F4288))
            lower.contains("kişisel") || lower.contains("özel") ->
                CategoryVisual(Icons.Outlined.AutoAwesome, Color(0xFF9E3A56))
            lower.contains("sağlık") || lower.contains("spor") ->
                CategoryVisual(Icons.Outlined.Eco, Color(0xFF2E7D32))
            lower.contains("alışveriş") || lower.contains("market") ->
                CategoryVisual(Icons.Outlined.ShoppingBag, Color(0xFFC48625))
            else -> {
                val fallbackIcons = listOf(
                    Icons.Outlined.Home,
                    Icons.Outlined.BusinessCenter,
                    Icons.Outlined.AutoAwesome,
                    Icons.Outlined.Eco,
                    Icons.Outlined.ShoppingBag,
                    Icons.Outlined.Label
                )
                CategoryVisual(fallbackIcons[fallbackIndex % fallbackIcons.size], Color(0xFF55493A))
            }
        }
    }
}

data class CategoryBackground(val light: Color, val dark: Color)

object CategoryBackgrounds {
    private val palette = listOf(
        CategoryBackground(Color(0xFFEEE7DC), Color(0xFF2E2822)), // Ev & Yaşam (Oatmeal)
        CategoryBackground(Color(0xFFDFE2F7), Color(0xFF292C42)), // İş (Lavanta / Periwinkle)
        CategoryBackground(Color(0xFFFCE6E5), Color(0xFF3A272D)), // Kişisel (Blush / Gül)
        CategoryBackground(Color(0xFFDFEFE3), Color(0xFF213527)), // Sağlık (Adaçayı / Sage)
        CategoryBackground(Color(0xFFF6EBCE), Color(0xFF37321F))  // Alışveriş (Kum / Wheat)
    )

    fun of(index: Int): CategoryBackground = palette[index % palette.size]
}
