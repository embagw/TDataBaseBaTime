package com.embag.tdatabasebatime.Model.DAO

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.embag.tdatabasebatime.Model.Entity.Schedule
import com.embag.tdatabasebatime.Model.Entity.SchedulePriorityView
import com.embag.tdatabasebatime.Model.Entity.ScheduleType
import com.embag.tdatabasebatime.Model.Entity.ScheduleWithCalculatedPriority
import com.embag.tdatabasebatime.Model.Entity.ScheduleWithTasks
import com.embag.tdatabasebatime.Model.Entity.Task
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

@Dao
interface ScheduleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedule(schedule: Schedule): Long

    @Update
    suspend fun updateSchedule(schedule: Schedule)

    @Delete
    suspend fun deleteSchedule(schedule: Schedule)

    @Query("SELECT * FROM schedules WHERE id = :scheduleId")
    suspend fun getScheduleById(scheduleId: Long): Schedule?

    @Query("SELECT * FROM schedules ORDER BY createdAt DESC")
    fun getAllSchedules(): Flow<List<Schedule>>

    @Transaction
    @Query("SELECT * FROM schedules")
    fun getSchedulesWithTasks(): Flow<List<ScheduleWithTasks>>

    @Transaction
    @Query("SELECT * FROM schedules WHERE id = :scheduleId")
    suspend fun getScheduleWithTasks(scheduleId: Long): ScheduleWithTasks?

    // کوئری برای گرفتن زمان‌بندی‌های یک تسک
    @Query("""
        SELECT s.* FROM schedules s
        INNER JOIN task_schedule ts ON s.id = ts.scheduleId
        WHERE ts.taskId = :taskId
        ORDER BY s.type ASC, s.createdAt DESC
    """)
    suspend fun getSchedulesForTask(taskId: Long): List<Schedule>

    // کوئری برای گرفتن تسک‌های یک زمان‌بندی
    @Query("""
        SELECT t.* FROM tasks t
        INNER JOIN task_schedule ts ON t.id = ts.taskId
        WHERE ts.scheduleId = :scheduleId
    """)
    suspend fun getTasksForSchedule(scheduleId: Long): List<Task>

    // کوئری برای زمان‌بندی‌های فعال بر اساس نوع
    @Query("SELECT * FROM schedules WHERE type = :type AND isActive = 1")
    fun getActiveSchedulesByType(type: ScheduleType): Flow<List<Schedule>>


    @Query("""
        SELECT s.*, 
               MIN(t.priority) as calculated_priority
        FROM schedules s
        LEFT JOIN task_schedule ts ON s.id = ts.scheduleId
        LEFT JOIN tasks t ON ts.taskId = t.id
        GROUP BY s.id
        ORDER BY calculated_priority ASC, s.createdAt ASC
    """)
    fun getAllSchedulesWithCalculatedPriority(): Flow<List<ScheduleWithCalculatedPriority>>
}