package com.embag.tdatabasebatime.Model.DAO

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.embag.tdatabasebatime.Model.Entity.TaskScheduleCrossRef

@Dao
interface TaskScheduleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTaskSchedule(crossRef: TaskScheduleCrossRef)

    @Delete
    suspend fun deleteTaskSchedule(crossRef: TaskScheduleCrossRef)

    @Query("DELETE FROM task_schedule WHERE taskId = :taskId AND scheduleId = :scheduleId")
    suspend fun deleteTaskSchedule(taskId: Long, scheduleId: Long)

    @Query("SELECT * FROM task_schedule WHERE taskId = :taskId")
    suspend fun getSchedulesForTask(taskId: Long): List<TaskScheduleCrossRef>

    @Query("SELECT * FROM task_schedule WHERE scheduleId = :scheduleId")
    suspend fun getTasksForSchedule(scheduleId: Long): List<TaskScheduleCrossRef>
}