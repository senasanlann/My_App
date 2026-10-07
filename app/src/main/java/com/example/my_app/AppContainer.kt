package com.example.my_app

import android.content.Context
import com.example.my_app.data.local.AppDatabase
import com.example.my_app.data.repository.AuthRepository
import com.example.my_app.data.repository.CategoryRepository
import com.example.my_app.data.repository.TaskRepository
import com.example.my_app.data.repository.UserRepository
import com.example.my_app.data.session.SessionManager
import com.example.my_app.data.settings.SettingsManager
import com.example.my_app.model.FilterState
import kotlinx.coroutines.flow.MutableStateFlow

class AppContainer(context: Context) {
    val database: AppDatabase = AppDatabase.getInstance(context)
    val userRepository: UserRepository = UserRepository(database.userDao())
    val settingsManager: SettingsManager = SettingsManager(context)

    /**
     * Kategori ızgarasından bir kategoriye, ya da Profil'deki bir istatistik
     * kartına tıklanınca Görevler listesine aktarılacak başlangıç filtresi.
     * Görevler ekranı artık normal bir "push" ile açıldığı için (geri okuyla
     * kapanan bir alt ekran), bu basit paylaşılan state yeterli ve güvenilir.
     */
    val pendingTaskFilter = MutableStateFlow<FilterState?>(null)

    val authRepository: AuthRepository = AuthRepository(
        database = database,
        userDao = database.userDao(),
        categoryDao = database.categoryDao()
    )
    val sessionManager: SessionManager = SessionManager(context)
    val taskRepository: TaskRepository = TaskRepository(database.taskDao())
    val categoryRepository: CategoryRepository = CategoryRepository(
        database = database,
        categoryDao = database.categoryDao(),
        taskDao = database.taskDao()
    )
}
