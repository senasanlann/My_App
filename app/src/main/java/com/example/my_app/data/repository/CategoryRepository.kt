package com.example.my_app.data.repository

import androidx.room.withTransaction
import com.example.my_app.data.local.AppDatabase
import com.example.my_app.data.local.CategoryDao
import com.example.my_app.data.local.CategoryEntity
import com.example.my_app.data.local.TaskDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class CategoryRepository(
    private val database: AppDatabase,
    private val categoryDao: CategoryDao,
    private val taskDao: TaskDao
) {

    fun getCategories(userId: Long): Flow<List<CategoryEntity>> {
        return categoryDao.getCategoriesForUser(userId)
    }

    suspend fun addCategory(userId: Long, name: String): Long {
        return categoryDao.insertCategory(CategoryEntity(userId = userId, name = name))
    }

    suspend fun deleteCategory(category: CategoryEntity, fallbackCategoryId: Long) {
        database.withTransaction {
            taskDao.moveTasksToCategory(category.id, fallbackCategoryId)
            categoryDao.deleteCategory(category)
        }
    }

    suspend fun deleteCategoryById(userId: Long, categoryId: Long): Boolean {
        val categories = categoryDao.getCategoriesForUser(userId).first()
        val category = categories.firstOrNull { it.id == categoryId } ?: return false
        val fallback = categories.firstOrNull { it.id != categoryId && (it.name == "Kişisel" || it.name == "Ev & Yaşam" || it.isDefault) }
            ?: categories.firstOrNull { it.id != categoryId }
            ?: return false
        database.withTransaction {
            taskDao.moveTasksToCategory(category.id, fallback.id)
            categoryDao.deleteCategory(category)
        }
        return true
    }

    suspend fun updateCategoryImage(categoryId: Long, path: String) {
        categoryDao.updateVisual(categoryId, imagePath = path, emoji = null)
    }

    suspend fun updateCategoryEmoji(categoryId: Long, emoji: String) {
        categoryDao.updateVisual(categoryId, imagePath = null, emoji = emoji)
    }
}
