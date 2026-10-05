package com.example.my_app.util

import android.util.Patterns

object Validators {
    fun validateName(name: String): String? {
        if (name.isBlank()) {
            return "Ad boş olamaz"
        }
        if (name.length < 2) {
            return "Ad en az 2 karakter olmalı"
        }
        return null
    }

    fun validateEmail(email: String): String? {
        if (email.isBlank()) {
            return "E-posta boş olamaz"
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return "Geçerli bir e-posta adresi girin"
        }
        return null
    }

    fun validatePassword(password: String): String? {
        if (password.isBlank()) {
            return "Şifre boş olamaz"
        }
        if (password.length < 6) {
            return "Şifre en az 6 karakter olmalı"
        }
        return null
    }

    fun validateConfirmPassword(password: String, confirmPassword: String): String? {
        if (confirmPassword.isBlank()) {
            return "Şifre tekrarı boş olamaz"
        }
        if (confirmPassword != password) {
            return "Şifreler eşleşmiyor"
        }
        return null
    }

}