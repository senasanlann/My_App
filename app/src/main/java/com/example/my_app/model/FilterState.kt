package com.example.my_app.model

data class FilterState(
    val categoryId: Long? = null,
    val status: TaskStatus? = null,
    val priority: TaskPriority? = null
) {
    val activeCount: Int
        get() = listOf(categoryId, status, priority).count { it != null }
}
