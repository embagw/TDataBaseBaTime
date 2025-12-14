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
import com.embag.tdatabasebatime.Model.Entity.ScheduleWithCalculatedPriority
import com.embag.tdatabasebatime.Model.Entity.ScheduleWithTasks
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

    @Query("SELECT * FROM schedules")
    fun getAllSchedules(): Flow<List<Schedule>>

    @Transaction
    @Query("SELECT * FROM schedules")
    fun getSchedulesWithTasks(): Flow<List<ScheduleWithTasks>>

    @Transaction
    @Query("SELECT * FROM schedules WHERE id = :scheduleId")
    fun getScheduleWithTasks(scheduleId: Long): Flow<ScheduleWithTasks?>

    // کوئری برای گرفتن Scheduleها به همراه اولویت محاسبه شده
    @Query("""
        SELECT s.*, 
               MIN(t.priority) as calculated_priority
        FROM schedules s
        LEFT JOIN task_schedule ts ON s.id = ts.scheduleId
        LEFT JOIN tasks t ON ts.taskId = t.id
        WHERE s.isActive = 1
        GROUP BY s.id
        ORDER BY calculated_priority ASC, s.startTime ASC
    """)
    fun getAllSchedulesWithCalculatedPriority(): Flow<List<ScheduleWithCalculatedPriority>>

    @Query("""
        SELECT * FROM schedules 
        WHERE id IN (SELECT scheduleId FROM task_schedule WHERE taskId = :taskId)
        ORDER BY startTime ASC
    """)
    suspend fun getSchedulesForTask(taskId: Long): List<Schedule>
}