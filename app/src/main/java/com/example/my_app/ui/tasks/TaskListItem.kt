package com.example.my_app.ui.tasks

import com.example.my_app.model.TaskPriority
import com.example.my_app.model.TaskStatus

data class TaskListItem(
    val id: Long,
    val title: String,
    val description: String,
    val categoryName: String,
    val status: TaskStatus,
    val priority: TaskPriority,
    val createdAt: Long,
    val imagePath: String? = null
)
