package com.embag.tdatabasebatime.Repository

import com.embag.tdatabasebatime.Model.DAO.TaskDao
import com.embag.tdatabasebatime.Model.Task
import kotlinx.coroutines.flow.Flow


class TaskRepository(private val taskDao: TaskDao) {
    fun getAllTasks(): Flow<List<Task>> = taskDao.getAllTasks()

    fun getTasksByCategory(category: String): Flow<List<Task>> =
        taskDao.getTasksByCategory(category)

    fun getAllCategories(): Flow<List<String>> = taskDao.getAllCategories()

    suspend fun getTaskById(id: Long): Task? = taskDao.getTaskById(id)

    suspend fun insertTask(task: Task): Long = taskDao.insertTask(task)

    suspend fun updateTask(task: Task) = taskDao.updateTask(task)

    suspend fun deleteTask(task: Task) = taskDao.deleteTask(task)

    suspend fun deleteTaskById(id: Long) = taskDao.deleteTaskById(id)
}