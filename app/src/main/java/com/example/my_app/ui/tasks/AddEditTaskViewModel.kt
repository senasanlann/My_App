package com.example.my_app.ui.tasks

import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.savedstate.SavedStateRegistryOwner
import com.example.my_app.data.local.CategoryEntity
import com.example.my_app.data.local.TaskEntity
import com.example.my_app.data.repository.CategoryRepository
import com.example.my_app.data.repository.TaskRepository
import com.example.my_app.data.session.SessionManager
import com.example.my_app.model.TaskPriority
import com.example.my_app.model.TaskStatus
import com.example.my_app.util.Validators
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AddEditTaskViewModel(
    savedStateHandle: SavedStateHandle,
    private val sessionManager: SessionManager,
    private val taskRepository: TaskRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val taskId: Long = savedStateHandle.get<Long>("taskId") ?: -1L
    private var userId: Long = 0
    private var existingTask: TaskEntity? = null

    private val _uiState = MutableStateFlow(AddEditTaskUiState(isEditMode = taskId != -1L))
    val uiState: StateFlow<AddEditTaskUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                userId = sessionManager.userIdFlow.first() ?: return@launch
                val categories = categoryRepository.getCategories(userId).first()
                val personalCategory = categories.firstOrNull { it.name == "Kişisel" }

                if (taskId != -1L) {
                    val task = taskRepository.getTask(taskId).first()
                    existingTask = task
                    if (task != null) {
                        _uiState.value = _uiState.value.copy(
                            title = task.title,
                            description = task.description,
                            categories = categories,
                            selectedCategoryId = task.categoryId,
                            status = task.status,
                            priority = task.priority,
                            dueDate = task.dueDate,
                            imagePath = task.imagePath,
                            isLoading = false,
                            isEditMode = true
                        )
                        return@launch
                    }
                }

                _uiState.value = _uiState.value.copy(
                    categories = categories,
                    selectedCategoryId = personalCategory?.id,
                    status = TaskStatus.TODO,
                    priority = TaskPriority.MEDIUM,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Bir hata oluştu"
                )
            }
        }
    }

    fun onTitleChange(value: String) {
        _uiState.value = _uiState.value.copy(title = value, titleError = null)
    }

    fun onDescriptionChange(value: String) {
        _uiState.value = _uiState.value.copy(description = value, descriptionError = null)
    }

    fun onCategorySelected(categoryId: Long) {
        _uiState.value = _uiState.value.copy(selectedCategoryId = categoryId)
    }

    fun onStatusSelected(status: TaskStatus) {
        _uiState.value = _uiState.value.copy(status = status)
    }

    fun onPrioritySelected(priority: TaskPriority) {
        _uiState.value = _uiState.value.copy(priority = priority)
    }

    fun onImageSelected(path: String?) {
        _uiState.value = _uiState.value.copy(imagePath = path)
    }

    fun onDueDateSelected(date: Long?) {
        _uiState.value = _uiState.value.copy(dueDate = date)
    }

    fun addNewCategory(name: String) {
        viewModelScope.launch {
            val newId = categoryRepository.addCategory(userId, name)
            val categories = categoryRepository.getCategories(userId).first()
            _uiState.value = _uiState.value.copy(
                categories = categories,
                selectedCategoryId = newId
            )
        }
    }

    suspend fun getCategoryTaskCount(categoryId: Long): Int {
        return taskRepository.countTasksInCategory(categoryId)
    }

    fun deleteCategory(category: CategoryEntity) {
        viewModelScope.launch {
            val categories = _uiState.value.categories
            val personalCategory = categories.firstOrNull { it.name == "Kişisel" } ?: return@launch
            categoryRepository.deleteCategory(category, personalCategory.id)
            val updatedCategories = categoryRepository.getCategories(userId).first()
            val newSelectedId = if (_uiState.value.selectedCategoryId == category.id) {
                personalCategory.id
            } else {
                _uiState.value.selectedCategoryId
            }
            _uiState.value = _uiState.value.copy(
                categories = updatedCategories,
                selectedCategoryId = newSelectedId
            )
        }
    }

    fun save() {
        val state = _uiState.value
        val titleError = Validators.validateTaskTitle(state.title)
        val descriptionError = Validators.validateTaskDescription(state.description)

        if (titleError != null || descriptionError != null) {
            _uiState.value = state.copy(titleError = titleError, descriptionError = descriptionError)
            return
        }

        val categoryId = state.selectedCategoryId ?: return

        viewModelScope.launch {
            try {
                if (state.isEditMode && existingTask != null) {
                    taskRepository.updateTask(
                        existingTask!!.copy(
                            categoryId = categoryId,
                            title = state.title,
                            description = state.description,
                            status = state.status,
                            priority = state.priority,
                            dueDate = state.dueDate,
                            imagePath = state.imagePath,
                            updatedAt = System.currentTimeMillis()
                        )
                    )
                } else {
                    taskRepository.addTask(
                        TaskEntity(
                            userId = userId,
                            categoryId = categoryId,
                            title = state.title,
                            description = state.description,
                            status = state.status,
                            priority = state.priority,
                            dueDate = state.dueDate,
                            imagePath = state.imagePath
                        )
                    )
                }
                _uiState.value = _uiState.value.copy(isSaved = true)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(errorMessage = e.message ?: "Kaydedilemedi, tekrar dene")
            }
        }
    }
}

class AddEditTaskViewModelFactory(
    private val sessionManager: SessionManager,
    private val taskRepository: TaskRepository,
    private val categoryRepository: CategoryRepository,
    owner: SavedStateRegistryOwner,
    defaultArgs: Bundle? = null
) : AbstractSavedStateViewModelFactory(owner, defaultArgs) {
    override fun <T : ViewModel> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        @Suppress("UNCHECKED_CAST")
        return AddEditTaskViewModel(handle, sessionManager, taskRepository, categoryRepository) as T
    }
}
