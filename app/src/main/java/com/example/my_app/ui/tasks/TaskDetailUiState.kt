package com.example.my_app.ui.tasks

import com.example.my_app.data.local.TaskEntity

data class TaskDetailUiState(
    val task: TaskEntity? = null,
    val categoryName: String = "",
    val isLoading: Boolean = true,
    val notFound: Boolean = false,
    val isDeleted: Boolean = false,
    val errorMessage: String? = null
)
