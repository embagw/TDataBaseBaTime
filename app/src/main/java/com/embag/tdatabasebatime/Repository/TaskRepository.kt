package com.embag.tdatabasebatime.Repository

import com.embag.tdatabasebatime.Model.DAO.ScheduleDao
import com.embag.tdatabasebatime.Model.DAO.TaskDao
import com.embag.tdatabasebatime.Model.DAO.TaskScheduleDao
import com.embag.tdatabasebatime.Model.Entity.Schedule
import com.embag.tdatabasebatime.Model.Entity.ScheduleWithCalculatedPriority
import com.embag.tdatabasebatime.Model.Entity.ScheduleWithTasks
import com.embag.tdatabasebatime.Model.Entity.Task
import com.embag.tdatabasebatime.Model.Entity.TaskScheduleCrossRef
import com.embag.tdatabasebatime.Model.Entity.TaskWithSchedules
import kotlinx.coroutines.flow.Flow

class TaskRepository(
    private val taskDao: TaskDao,
    private val scheduleDao: ScheduleDao,
    private val taskScheduleDao: TaskScheduleDao
) {
    // متدهای Task
    fun getAllTasks(): Flow<List<Task>> = taskDao.getAllTasks()

    fun getTasksWithSchedules(): Flow<List<TaskWithSchedules>> =
        taskDao.getTasksWithSchedules()

    fun getTaskWithSchedules(taskId: Long): Flow<TaskWithSchedules?> =
        taskDao.getTaskWithSchedules(taskId)

    suspend fun getTaskById(id: Long): Task? = taskDao.getTaskById(id)

    suspend fun insertTask(task: Task): Long = taskDao.insertTask(task)

    suspend fun updateTask(task: Task) = taskDao.updateTask(task)

    suspend fun deleteTask(task: Task) = taskDao.deleteTask(task)

    suspend fun deleteTaskById(id: Long) = taskDao.deleteTaskById(id)

    // متدهای Schedule
    fun getAllSchedules(): Flow<List<Schedule>> = scheduleDao.getAllSchedules()

    fun getSchedulesWithTasks(): Flow<List<ScheduleWithTasks>> =
        scheduleDao.getSchedulesWithTasks()

    fun getAllSchedulesWithCalculatedPriority(): Flow<List<ScheduleWithCalculatedPriority>> =
        scheduleDao.getAllSchedulesWithCalculatedPriority()

    suspend fun insertSchedule(schedule: Schedule): Long =
        scheduleDao.insertSchedule(schedule)

    suspend fun updateSchedule(schedule: Schedule) =
        scheduleDao.updateSchedule(schedule)

    suspend fun deleteSchedule(schedule: Schedule) =
        scheduleDao.deleteSchedule(schedule)

    suspend fun getScheduleById(scheduleId: Long): Schedule? =
        scheduleDao.getScheduleById(scheduleId)

    // متدهای رابطه Task-Schedule
    suspend fun linkTaskToSchedule(taskId: Long, scheduleId: Long) {
        val crossRef = TaskScheduleCrossRef(taskId, scheduleId)
        taskScheduleDao.insertTaskSchedule(crossRef)
    }

    suspend fun unlinkTaskFromSchedule(taskId: Long, scheduleId: Long) {
        taskScheduleDao.deleteTaskSchedule(taskId, scheduleId)
    }

    suspend fun getTasksForSchedule(scheduleId: Long): List<Task> {
        return taskDao.getTasksForSchedule(scheduleId)
    }

    suspend fun getSchedulesForTask(taskId: Long): List<Schedule> {
        return scheduleDao.getSchedulesForTask(taskId)
    }

    // متد برای ایجاد Task با Schedule
    suspend fun createTaskWithSchedule(
        task: Task,
        schedule: Schedule
    ): Pair<Long, Long> {
        val taskId = taskDao.insertTask(task)
        val scheduleId = scheduleDao.insertSchedule(schedule)
        linkTaskToSchedule(taskId, scheduleId)
        return Pair(taskId, scheduleId)
    }

    // متد برای به‌روزرسانی اولویت Scheduleها پس از تغییر Task
    suspend fun recalculateSchedulePriorities() {
        // این متد می‌تواند در پس‌زمینه اجرا شود
    }
}