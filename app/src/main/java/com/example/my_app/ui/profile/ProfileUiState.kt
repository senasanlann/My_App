package com.example.my_app.ui.profile

data class ProfileUiState(
    val name: String = "",
    val email: String = "",
    val totalCount: Int = 0,
    val todoCount: Int = 0,
    val inProgressCount: Int = 0,
    val doneCount: Int = 0,
    val memberSinceLabel: String = "",
    val avatarPath: String? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val isLoggedOut: Boolean = false
)
