package com.example.my_app.data.repository

import androidx.room.withTransaction
import com.example.my_app.data.local.AppDatabase
import com.example.my_app.data.local.CategoryDao
import com.example.my_app.data.local.CategoryEntity
import com.example.my_app.data.local.UserDao
import com.example.my_app.data.local.UserEntity

class AuthRepository(
    private val database: AppDatabase,
    private val userDao: UserDao,
    private val categoryDao: CategoryDao
) {
    suspend fun register(name: String, email: String, passwordHash: String): Result<Unit> {
        return database.withTransaction {
            val existingUser = userDao.getUserByEmail(email)
            if (existingUser != null) {
                return@withTransaction Result.failure(Exception("Bu e-posta zaten kayıtlı"))
            }

            val newUser = UserEntity(
                name = name,
                email = email,
                passwordHash = passwordHash
            )
            val newUserId = userDao.insertUser(newUser)

            val defaultCategories = listOf(
                CategoryEntity(userId = newUserId, name = "Ev & Yaşam", isDefault = true),
                CategoryEntity(userId = newUserId, name = "İş", isDefault = true),
                CategoryEntity(userId = newUserId, name = "Kişisel", isDefault = true),
                CategoryEntity(userId = newUserId, name = "Sağlık", isDefault = true),
                CategoryEntity(userId = newUserId, name = "Alışveriş", isDefault = true)
            )
            categoryDao.insertCategories(defaultCategories)

            Result.success(Unit)
        }
    }
    suspend fun login(email: String, passwordHash: String): Result<UserEntity> {
        val user = userDao.getUserByEmail(email)
            ?: return Result.failure(Exception("E-posta veya şifre hatalı"))

        if (user.passwordHash != passwordHash) {
            return Result.failure(Exception("E-posta veya şifre hatalı"))
        }

        return Result.success(user)
    }
}
