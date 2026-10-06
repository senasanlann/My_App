package com.example.my_app

import android.content.Context
import com.example.my_app.data.local.AppDatabase
import com.example.my_app.data.repository.AuthRepository
import com.example.my_app.data.repository.UserRepository
import com.example.my_app.data.session.SessionManager

class AppContainer(context: Context) {
    val database: AppDatabase = AppDatabase.getInstance(context)
    val userRepository: UserRepository = UserRepository(database.userDao())
    val authRepository: AuthRepository = AuthRepository(
        database = database,
        userDao = database.userDao(),
        categoryDao = database.categoryDao()
    )
    val sessionManager: SessionManager = SessionManager(context)
}
