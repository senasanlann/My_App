package com.example.my_app.model

enum class SortOption(val displayName: String) {
    DATE_NEWEST(displayName = "Tarihe Göre (Yeni → Eski)"),
    DATE_OLDEST(displayName = "Tarihe Göre (Eski → Yeni)"),
    PRIORITY(displayName = "Önceliğe Göre"),
    TITLE_AZ(displayName = "Başlığa Göre (A-Z)"),
    STATUS(displayName = "Duruma Göre")
}