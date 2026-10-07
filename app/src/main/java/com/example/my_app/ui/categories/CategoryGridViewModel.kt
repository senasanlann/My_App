package com.example.my_app.ui.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.my_app.data.repository.CategoryRepository
import com.example.my_app.data.repository.TaskRepository
import com.example.my_app.data.session.SessionManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class CategoryGridViewModel(
    private val sessionManager: SessionManager,
    private val categoryRepository: CategoryRepository,
    taskRepository: TaskRepository
) : ViewModel() {

    val uiState: StateFlow<CategoryGridUiState> = sessionManager.userIdFlow
        .flatMapLatest { userId ->
            if (userId == null) {
                flowOf(CategoryGridUiState(isLoading = false))
            } else {
                combine(
                    categoryRepository.getCategories(userId),
                    taskRepository.getTasks(userId),
                    sessionManager.userNameFlow
                ) { categories, tasks, userName ->
                    val countsByCategory = tasks.groupingBy { it.categoryId }.eachCount()
                    val items = categories.map { category ->
                        CategoryGridItem(
                            id = category.id,
                            name = category.name,
                            isDefault = category.isDefault,
                            taskCount = countsByCategory[category.id] ?: 0,
                            imagePath = category.imagePath,
                            emoji = category.emoji
                        )
                    }
                    CategoryGridUiState(
                        categories = items,
                        userName = userName ?: "",
                        totalTaskCount = tasks.size,
                        isLoading = false
                    )
                }
            }
        }
        .catch { e ->
            emit(CategoryGridUiState(isLoading = false, errorMessage = e.message ?: "Bir hata oluştu"))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = CategoryGridUiState()
        )

    fun addCategory(name: String) {
        viewModelScope.launch {
            val userId = sessionManager.userIdFlow.first() ?: return@launch
            categoryRepository.addCategory(userId, name)
        }
    }

    fun deleteCategory(categoryId: Long) {
        viewModelScope.launch {
            val userId = sessionManager.userIdFlow.first() ?: return@launch
            categoryRepository.deleteCategoryById(userId, categoryId)
        }
    }
}

class CategoryGridViewModelFactory(
    private val sessionManager: SessionManager,
    private val categoryRepository: CategoryRepository,
    private val taskRepository: TaskRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return CategoryGridViewModel(sessionManager, categoryRepository, taskRepository) as T
    }
}
