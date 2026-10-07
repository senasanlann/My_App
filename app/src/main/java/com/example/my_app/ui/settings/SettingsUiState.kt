package com.example.my_app.ui.settings

import com.example.my_app.model.SortOption

data class SettingsUiState(
    val darkTheme: Boolean = false,
    val defaultSort: SortOption = SortOption.NEWEST
)
