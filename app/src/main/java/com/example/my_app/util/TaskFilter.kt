package com.example.my_app.util

import com.example.my_app.data.local.TaskEntity
import com.example.my_app.model.FilterState
import com.example.my_app.model.SortOption
import java.text.Collator
import java.util.Locale

object TaskFilter {

    fun apply(
        tasks: List<TaskEntity>,
        query: String,
        filters: FilterState,
        sortOption: SortOption
    ): List<TaskEntity> {
        val filtered = tasks.filter { task ->
            val matchesQuery = query.isBlank() ||
                task.title.contains(query, ignoreCase = true) ||
                task.description.contains(query, ignoreCase = true)
            val matchesCategory = filters.categoryId == null || task.categoryId == filters.categoryId
            val matchesStatus = filters.status == null || task.status == filters.status
            val matchesPriority = filters.priority == null || task.priority == filters.priority
            matchesQuery && matchesCategory && matchesStatus && matchesPriority
        }

        val collator = Collator.getInstance(Locale("tr")).apply { strength = Collator.SECONDARY }
        val titleComparator = Comparator<TaskEntity> { a, b -> collator.compare(a.title, b.title) }

        return when (sortOption) {
            SortOption.NEWEST -> filtered.sortedByDescending { it.createdAt }
            SortOption.OLDEST -> filtered.sortedBy { it.createdAt }
            SortOption.TITLE_ASC -> filtered.sortedWith(titleComparator.thenByDescending { it.createdAt })
            SortOption.TITLE_DESC -> filtered.sortedWith(titleComparator.reversed().thenByDescending { it.createdAt })
            SortOption.PRIORITY -> filtered.sortedWith(
                compareByDescending<TaskEntity> { it.priority.level }.thenByDescending { it.createdAt }
            )
        }
    }
}
