package com.example.my_app.ui.categories

data class CategoryGridItem(
    val id: Long,
    val name: String,
    val isDefault: Boolean,
    val taskCount: Int,
    val imagePath: String? = null,
    val emoji: String? = null
)

data class CategoryGridUiState(
    val categories: List<CategoryGridItem> = emptyList(),
    val userName: String = "",
    val totalTaskCount: Int = 0,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)
