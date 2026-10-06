package com.example.my_app.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.my_app.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.my_app.util.Validators
import androidx.lifecycle.viewModelScope
import com.example.my_app.util.HashUtils
import kotlinx.coroutines.launch
import com.example.my_app.data.session.SessionManager

class LoginViewModel(
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChange(newEmail: String) {
        _uiState.value = _uiState.value.copy(email = newEmail, emailError = null)
    }

    fun onPasswordChange(newPassword: String) {
        _uiState.value = _uiState.value.copy(password = newPassword, passwordError = null)
    }

    fun validateForm(): Boolean {
        val currentState = _uiState.value

        val emailError = Validators.validateEmail(currentState.email)
        val passwordError = if (currentState.password.isBlank()) "Şifre boş olamaz" else null

        _uiState.value = currentState.copy(
            emailError = emailError,
            passwordError = passwordError
        )

        return emailError == null && passwordError == null
    }

    fun onLoginClick() {
        val isValid = validateForm()
        if (!isValid) {
            return
        }

        _uiState.value = _uiState.value.copy(isLoading = true)

        viewModelScope.launch {
            val passwordHash = HashUtils.sha256(_uiState.value.password)
            val result = authRepository.login(
                email = _uiState.value.email,
                passwordHash = passwordHash
            )

            result
                .onSuccess { user ->
                    sessionManager.saveSession(user.id, user.name, user.email)
                    _uiState.value = _uiState.value.copy(isLoading = false, isSuccess = true)
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        generalError = error.message
                    )
                }
        }
    }
}

class LoginViewModelFactory(
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return LoginViewModel(authRepository, sessionManager) as T
    }
}
