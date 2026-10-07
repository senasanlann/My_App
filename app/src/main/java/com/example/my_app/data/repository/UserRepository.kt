package com.example.my_app.data.repository

import com.example.my_app.data.local.UserDao
import com.example.my_app.data.local.UserEntity

class UserRepository(private val userDao: UserDao) {
    suspend fun register(user: UserEntity): Long {
        return userDao.insertUser(user)
    }

    suspend fun findByEmail(email: String): UserEntity? {
        return userDao.getUserByEmail(email)
    }

    suspend fun findById(userId: Long): UserEntity? {
        return userDao.getUserById(userId)
    }

    suspend fun updateAvatar(userId: Long, path: String?) {
        userDao.updateAvatarPath(userId, path)
    }
}