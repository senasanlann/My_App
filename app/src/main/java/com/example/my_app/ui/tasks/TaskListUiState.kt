package com.example.my_app.ui.tasks

import com.example.my_app.data.local.CategoryEntity
import com.example.my_app.model.FilterState
import com.example.my_app.model.SortOption

data class TaskListUiState(
    val tasks: List<TaskListItem> = emptyList(),
    val totalTaskCount: Int = 0,
    val categories: List<CategoryEntity> = emptyList(),
    val searchQuery: String = "",
    val filterState: FilterState = FilterState(),
    val sortOption: SortOption = SortOption.NEWEST,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)
