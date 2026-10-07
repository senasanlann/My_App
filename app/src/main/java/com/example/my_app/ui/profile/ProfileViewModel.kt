package com.example.my_app.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.my_app.data.repository.TaskRepository
import com.example.my_app.data.repository.UserRepository
import com.example.my_app.data.session.SessionManager
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
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private data class ProfileExtras(
    val memberSinceLabel: String = "",
    val avatarPath: String? = null
)

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModel(
    private val sessionManager: SessionManager,
    private val taskRepository: TaskRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private var userId: Long = 0
    private val _extras = MutableStateFlow(ProfileExtras())
    private val _errorMessage = MutableStateFlow<String?>(null)
    private val _isLoggedOut = MutableStateFlow(false)

    private val statsFlow = combine(
        sessionManager.userNameFlow,
        sessionManager.userEmailFlow,
        sessionManager.userIdFlow.flatMapLatest { id ->
            if (id == null) flowOf(emptyList()) else taskRepository.getTasks(id)
        },
        _extras,
        _errorMessage
    ) { name, email, tasks, extras, error ->
        ProfileUiState(
            name = name ?: "",
            email = email ?: "",
            totalCount = tasks.size,
            todoCount = tasks.count { it.status == TaskStatus.TODO },
            inProgressCount = tasks.count { it.status == TaskStatus.IN_PROGRESS },
            doneCount = tasks.count { it.status == TaskStatus.DONE },
            memberSinceLabel = extras.memberSinceLabel,
            avatarPath = extras.avatarPath,
            isLoading = false,
            errorMessage = error
        )
    }.catch { e ->
        emit(ProfileUiState(isLoading = false, errorMessage = e.message ?: "Bir hata oluştu"))
    }

    val uiState: StateFlow<ProfileUiState> = combine(statsFlow, _isLoggedOut) { state, loggedOut ->
        state.copy(isLoggedOut = loggedOut)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ProfileUiState()
    )

    init {
        loadProfileExtras()
    }

    private fun loadProfileExtras() {
        viewModelScope.launch {
            try {
                val id = sessionManager.userIdFlow.first() ?: return@launch
                userId = id
                val user = userRepository.findById(id)
                if (user != null) {
                    val format = SimpleDateFormat("MMMM yyyy", Locale("tr"))
                    _extras.value = ProfileExtras(
                        memberSinceLabel = "Üyelik: ${format.format(Date(user.createdAt))}",
                        avatarPath = user.avatarPath
                    )
                }
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Bir hata oluştu"
            }
        }
    }

    fun updateAvatar(path: String) {
        viewModelScope.launch {
            userRepository.updateAvatar(userId, path)
            _extras.value = _extras.value.copy(avatarPath = path)
        }
    }

    fun retry() {
        loadProfileExtras()
    }

    fun logout() {
        viewModelScope.launch {
            sessionManager.clearSession()
            _isLoggedOut.value = true
        }
    }
}

class ProfileViewModelFactory(
    private val sessionManager: SessionManager,
    private val taskRepository: TaskRepository,
    private val userRepository: UserRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return ProfileViewModel(sessionManager, taskRepository, userRepository) as T
    }
}
