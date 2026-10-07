package com.example.my_app.ui.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.my_app.data.repository.CategoryRepository
import com.example.my_app.data.repository.TaskRepository
import com.example.my_app.data.session.SessionManager
import com.example.my_app.data.settings.SettingsManager
import com.example.my_app.model.FilterState
import com.example.my_app.model.SortOption
import com.example.my_app.util.TaskFilter
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
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
class TaskListViewModel(
    private val sessionManager: SessionManager,
    private val taskRepository: TaskRepository,
    private val categoryRepository: CategoryRepository,
    private val settingsManager: SettingsManager
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _filterState = MutableStateFlow(FilterState())
    private val _sortOption = MutableStateFlow(SortOption.NEWEST)
    private val _retryTrigger = MutableStateFlow(0)

    val uiState: StateFlow<TaskListUiState> = combine(
        sessionManager.userIdFlow,
        _retryTrigger
    ) { userId, _ -> userId }
        .flatMapLatest { userId ->
            if (userId == null) {
                flowOf(TaskListUiState(tasks = emptyList(), isLoading = false))
            } else {
                combine(
                    taskRepository.getTasks(userId),
                    categoryRepository.getCategories(userId),
                    _searchQuery,
                    _filterState,
                    _sortOption
                ) { tasks, categories, query, filters, sort ->
                    val categoryNames = categories.associateBy({ it.id }, { it.name })
                    val sortedTasks = TaskFilter.apply(tasks, query, filters, sort)

                    val items = sortedTasks.map { task ->
                        TaskListItem(
                            id = task.id,
                            title = task.title,
                            description = task.description,
                            categoryName = categoryNames[task.categoryId] ?: "Genel",
                            status = task.status,
                            priority = task.priority,
                            createdAt = task.createdAt,
                            imagePath = task.imagePath
                        )
                    }

                    TaskListUiState(
                        tasks = items,
                        totalTaskCount = tasks.size,
                        categories = categories,
                        searchQuery = query,
                        filterState = filters,
                        sortOption = sort,
                        isLoading = false
                    )
                }
            }
        }
        .catch { e ->
            emit(TaskListUiState(isLoading = false, errorMessage = e.message ?: "Bir hata oluştu"))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = TaskListUiState()
        )

    init {
        viewModelScope.launch {
            _sortOption.value = settingsManager.defaultSortFlow.first()
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onFilterStateChange(filters: FilterState) {
        _filterState.value = filters
    }

    fun onSortOptionChange(option: SortOption) {
        _sortOption.value = option
    }

    fun clearAllFilters() {
        _searchQuery.value = ""
        _filterState.value = FilterState()
    }

    fun retry() {
        _retryTrigger.value++
    }

    fun updateCategoryImage(categoryId: Long, path: String) {
        viewModelScope.launch {
            categoryRepository.updateCategoryImage(categoryId, path)
        }
    }

    fun updateCategoryEmoji(categoryId: Long, emoji: String) {
        viewModelScope.launch {
            categoryRepository.updateCategoryEmoji(categoryId, emoji)
        }
    }

    fun deleteTask(taskId: Long) {
        viewModelScope.launch {
            taskRepository.deleteTaskById(taskId)
        }
    }

    fun deleteCategory(categoryId: Long) {
        viewModelScope.launch {
            val userId = sessionManager.userIdFlow.first() ?: return@launch
            categoryRepository.deleteCategoryById(userId, categoryId)
        }
    }
}

class TaskListViewModelFactory(
    private val sessionManager: SessionManager,
    private val taskRepository: TaskRepository,
    private val categoryRepository: CategoryRepository,
    private val settingsManager: SettingsManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return TaskListViewModel(sessionManager, taskRepository, categoryRepository, settingsManager) as T
    }
}
