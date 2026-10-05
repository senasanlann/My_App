package com.example.my_app.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.my_app.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.my_app.util.Validators

class RegisterViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onNameChange(newName: String) {
        _uiState.value = _uiState.value.copy(name = newName, nameError = null)
    }

    fun onEmailChange(newEmail: String) {
        _uiState.value = _uiState.value.copy(email = newEmail, emailError = null)
    }

    fun onPasswordChange(newPassword: String) {
        _uiState.value = _uiState.value.copy(password = newPassword, passwordError = null)
    }

    fun onConfirmPasswordChange(newConfirmPassword: String) {
        _uiState.value = _uiState.value.copy(confirmPassword = newConfirmPassword, confirmPasswordError = null)
    }
    fun validateForm(): Boolean {
        val currentState = _uiState.value

        val nameError = Validators.validateName(currentState.name)
        val emailError = Validators.validateEmail(currentState.email)
        val passwordError = Validators.validatePassword(currentState.password)
        val confirmPasswordError = Validators.validateConfirmPassword(
            currentState.password,
            currentState.confirmPassword
        )

        _uiState.value = currentState.copy(
            nameError = nameError,
            emailError = emailError,
            passwordError = passwordError,
            confirmPasswordError = confirmPasswordError
        )

        return nameError == null && emailError == null &&
                passwordError == null && confirmPasswordError == null
    }
    fun onRegisterClick() {
        val isValid = validateForm()
        if (!isValid) {
            return
        }
        // TODO: Gerçek kayıt işlemi (Room'a yazma) ileride eklenecek
    }
}

class RegisterViewModelFactory(
    private val userRepository: UserRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return RegisterViewModel(userRepository) as T
    }
}