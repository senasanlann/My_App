package com.example.my_app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// "Rude Olive" markasının yumuşak, sıcak hissini tüm uygulamaya (kart, metin
// alanı, diyalog, alt panel) otomatik olarak yayan köşe-yuvarlama sistemi.
val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(22.dp),
    extraLarge = RoundedCornerShape(28.dp)
)
