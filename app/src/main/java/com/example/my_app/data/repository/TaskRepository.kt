package com.example.my_app.data.repository

import com.example.my_app.data.local.TaskDao
import com.example.my_app.data.local.TaskEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class TaskRepository(private val taskDao: TaskDao) {

    fun getTasks(userId: Long): Flow<List<TaskEntity>> {
        return taskDao.getTasksForUser(userId)
    }

    fun getTask(taskId: Long): Flow<TaskEntity?> {
        return taskDao.getTaskById(taskId)
    }

    suspend fun addTask(task: TaskEntity) {
        taskDao.insertTask(task)
    }

    suspend fun updateTask(task: TaskEntity) {
        taskDao.updateTask(task)
    }

    suspend fun deleteTask(task: TaskEntity) {
        taskDao.deleteTask(task)
    }

    suspend fun deleteTaskById(taskId: Long) {
        val task = taskDao.getTaskById(taskId).first() ?: return
        taskDao.deleteTask(task)
    }

    suspend fun countTasksInCategory(categoryId: Long): Int {
        return taskDao.countTasksInCategory(categoryId)
    }
}
