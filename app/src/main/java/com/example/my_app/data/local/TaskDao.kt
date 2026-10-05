package com.example.my_app.data.local

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Query("SELECT * FROM tasks WHERE userId = :userId")
    fun getTasksForUser(userId: Long): Flow<List<TaskEntity>>
}
