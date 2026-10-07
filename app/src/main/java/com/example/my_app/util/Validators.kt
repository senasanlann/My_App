package com.example.my_app.util

import android.util.Patterns
import androidx.annotation.StringRes
import com.example.my_app.R

object Validators {
    @StringRes
    fun validateName(name: String): Int? {
        if (name.isBlank()) {
            return R.string.error_name_blank
        }
        if (name.length < 2) {
            return R.string.error_name_short
        }
        return null
    }

    @StringRes
    fun validateEmail(email: String): Int? {
        if (email.isBlank()) {
            return R.string.error_email_blank
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return R.string.error_email_invalid
        }
        return null
    }

    @StringRes
    fun validatePassword(password: String): Int? {
        if (password.isBlank()) {
            return R.string.error_password_blank
        }
        if (password.length < 8) {
            return R.string.error_password_short
        }
        if (password.none { it.isLetter() } || password.none { it.isDigit() }) {
            return R.string.error_password_weak
        }
        return null
    }

    @StringRes
    fun validateLoginPassword(password: String): Int? {
        if (password.isBlank()) {
            return R.string.error_password_blank
        }
        return null
    }

    @StringRes
    fun validateConfirmPassword(password: String, confirmPassword: String): Int? {
        if (confirmPassword.isBlank()) {
            return R.string.error_confirm_password_blank
        }
        if (confirmPassword != password) {
            return R.string.error_password_mismatch
        }
        return null
    }

    @StringRes
    fun validateTaskTitle(title: String): Int? {
        if (title.isBlank()) {
            return R.string.error_task_title_blank
        }
        if (title.length > 100) {
            return R.string.error_task_title_too_long
        }
        return null
    }

    @StringRes
    fun validateTaskDescription(description: String): Int? {
        if (description.length > 500) {
            return R.string.error_task_description_too_long
        }
        return null
    }

    @StringRes
    fun validateCategoryName(name: String): Int? {
        if (name.isBlank()) {
            return R.string.error_category_name_blank
        }
        if (name.length > 20) {
            return R.string.error_category_name_too_long
        }
        return null
    }
}
