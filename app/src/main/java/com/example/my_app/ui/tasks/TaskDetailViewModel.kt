package com.example.my_app.ui.tasks

import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.savedstate.SavedStateRegistryOwner
import com.example.my_app.data.repository.CategoryRepository
import com.example.my_app.data.repository.TaskRepository
import com.example.my_app.model.TaskStatus
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class TaskDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val taskRepository: TaskRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val taskId: Long = checkNotNull(savedStateHandle.get<Long>("taskId"))

    private val _isDeleted = MutableStateFlow(false)
    private val _retryTrigger = MutableStateFlow(0)

    private val detailFlow = _retryTrigger.flatMapLatest {
        taskRepository.getTask(taskId).flatMapLatest { task ->
            if (task == null) {
                flowOf(TaskDetailUiState(isLoading = false, notFound = true))
            } else {
                categoryRepository.getCategories(task.userId).map { categories ->
                    val categoryName = categories.firstOrNull { it.id == task.categoryId }?.name ?: "Genel"
                    TaskDetailUiState(
                        task = task,
                        categoryName = categoryName,
                        isLoading = false,
                        notFound = false
                    )
                }
            }
        }
    }.catch { e ->
        emit(TaskDetailUiState(isLoading = false, errorMessage = e.message ?: "Bir hata oluştu"))
    }

    val uiState: StateFlow<TaskDetailUiState> = combine(detailFlow, _isDeleted) { detail, deleted ->
        detail.copy(isDeleted = deleted)
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = TaskDetailUiState()
        )

    fun deleteTask() {
        viewModelScope.launch {
            val task = taskRepository.getTask(taskId).first()
            if (task != null) {
                taskRepository.deleteTask(task)
            }
            _isDeleted.value = true
        }
    }

    fun retry() {
        _retryTrigger.value++
    }

    fun markComplete() {
        viewModelScope.launch {
            val task = taskRepository.getTask(taskId).first() ?: return@launch
            taskRepository.updateTask(
                task.copy(status = TaskStatus.DONE, updatedAt = System.currentTimeMillis())
            )
        }
    }

    fun markTodo() {
        viewModelScope.launch {
            val task = taskRepository.getTask(taskId).first() ?: return@launch
            taskRepository.updateTask(
                task.copy(status = TaskStatus.TODO, updatedAt = System.currentTimeMillis())
            )
        }
    }
}

class TaskDetailViewModelFactory(
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
        return TaskDetailViewModel(handle, taskRepository, categoryRepository) as T
    }
}
