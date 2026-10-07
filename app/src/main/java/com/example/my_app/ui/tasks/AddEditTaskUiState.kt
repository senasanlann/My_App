package com.example.my_app.ui.tasks

import androidx.annotation.StringRes
import com.example.my_app.data.local.CategoryEntity
import com.example.my_app.model.TaskPriority
import com.example.my_app.model.TaskStatus

data class AddEditTaskUiState(
    val title: String = "",
    val description: String = "",
    val categories: List<CategoryEntity> = emptyList(),
    val selectedCategoryId: Long? = null,
    val status: TaskStatus = TaskStatus.TODO,
    val priority: TaskPriority = TaskPriority.MEDIUM,
    val dueDate: Long? = null,
    @StringRes val titleError: Int? = null,
    @StringRes val descriptionError: Int? = null,
    val isLoading: Boolean = true,
    val isSaved: Boolean = false,
    val isEditMode: Boolean = false,
    val imagePath: String? = null,
    val errorMessage: String? = null
)
