package com.example.my_app

import android.content.Context
import com.example.my_app.data.local.AppDatabase
import com.example.my_app.data.repository.UserRepository

class AppContainer(context: Context) {
    val database: AppDatabase = AppDatabase.getInstance(context)
    val userRepository: UserRepository = UserRepository(database.userDao())
}