package com.embag.tdatabasebatime.Model.DAO

import androidx.room.*
import com.embag.tdatabasebatime.Model.Entity.Task
import com.embag.tdatabasebatime.Model.Entity.TaskWithSchedules
import kotlinx.coroutines.flow.Flow





@Dao
interface TaskDao {


    @Query("SELECT * FROM tasks WHERE id = :taskId")
    suspend fun getTaskById(taskId: Long): Task?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task): Long

    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)

    @Query("DELETE FROM tasks WHERE id = :taskId")
    suspend fun deleteTaskById(taskId: Long)

    @Query("SELECT * FROM tasks WHERE category = :category ORDER BY priority ASC")
    fun getTasksByCategory(category: String): Flow<List<Task>>

    @Query("SELECT DISTINCT category FROM tasks")
    fun getAllCategories(): Flow<List<String>>







    @Query("SELECT * FROM tasks")
    fun getAllTasks(): Flow<List<Task>>

    @Transaction
    @Query("SELECT * FROM tasks")
    fun getTasksWithSchedules(): Flow<List<TaskWithSchedules>>

    @Transaction
    @Query("SELECT * FROM tasks WHERE id = :taskId")
    fun getTaskWithSchedules(taskId: Long): Flow<TaskWithSchedules?>

    @Query("""
        SELECT * FROM tasks 
        WHERE id IN (SELECT taskId FROM task_schedule WHERE scheduleId = :scheduleId)
    """)
    suspend fun getTasksForSchedule(scheduleId: Long): List<Task>
}