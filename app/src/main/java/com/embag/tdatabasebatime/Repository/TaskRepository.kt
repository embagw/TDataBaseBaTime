package com.embag.tdatabasebatime.Repository

import com.embag.tdatabasebatime.Model.DAO.CategoryDao
import com.embag.tdatabasebatime.Model.DAO.ScheduleDao
import com.embag.tdatabasebatime.Model.DAO.TaskDao
import com.embag.tdatabasebatime.Model.DAO.TaskScheduleDao
import com.embag.tdatabasebatime.Model.Entity.Category
import com.embag.tdatabasebatime.Model.Entity.CategoryWithTaskCount
import com.embag.tdatabasebatime.Model.Entity.Schedule
import com.embag.tdatabasebatime.Model.Entity.ScheduleWithCalculatedPriority
import com.embag.tdatabasebatime.Model.Entity.ScheduleWithTasks
import com.embag.tdatabasebatime.Model.Entity.Task
import com.embag.tdatabasebatime.Model.Entity.TaskScheduleCrossRef
import com.embag.tdatabasebatime.Model.Entity.TaskWithRelations
import com.embag.tdatabasebatime.Model.Entity.TaskWithSchedules
import kotlinx.coroutines.flow.Flow




class TaskRepository(
    private val taskDao: TaskDao,
    private val scheduleDao: ScheduleDao,
    private val taskScheduleDao: TaskScheduleDao,
    private val categoryDao: CategoryDao
) {
    // متدهای Task
    fun getAllTasks(): Flow<List<Task>> = taskDao.getAllTasks()
    fun getTasksWithRelations(): Flow<List<TaskWithRelations>> = taskDao.getTasksWithRelations()

    suspend fun getTaskById(id: Long): Task? = taskDao.getTaskById(id)
    suspend fun getTaskWithRelations(id: Long): TaskWithRelations? = taskDao.getTaskWithRelations(id)

    suspend fun insertTask(task: Task): Long = taskDao.insertTask(task)
    suspend fun updateTask(task: Task) = taskDao.updateTask(task)
    suspend fun deleteTask(task: Task) = taskDao.deleteTask(task)
    suspend fun deleteTaskById(id: Long) = taskDao.deleteTaskById(id)

    // متدهای Category
    fun getAllCategories(): Flow<List<Category>> = categoryDao.getAllCategories()
    fun getCategoriesWithTaskCount(): Flow<List<CategoryWithTaskCount>> = categoryDao.getCategoriesWithTaskCount()

    suspend fun insertCategory(category: Category): Long = categoryDao.insertCategory(category)
    suspend fun updateCategory(category: Category) = categoryDao.updateCategory(category)
    suspend fun deleteCategory(category: Category) = categoryDao.deleteCategory(category)
    suspend fun getCategoryById(categoryId: Long): Category? = categoryDao.getCategoryById(categoryId)

    // متدهای Schedule
    fun getAllSchedules(): Flow<List<Schedule>> = scheduleDao.getAllSchedules()
    fun getSchedulesWithTasks(): Flow<List<ScheduleWithTasks>> = scheduleDao.getSchedulesWithTasks()

    suspend fun insertSchedule(schedule: Schedule): Long = scheduleDao.insertSchedule(schedule)
    suspend fun updateSchedule(schedule: Schedule) = scheduleDao.updateSchedule(schedule)
    suspend fun deleteSchedule(schedule: Schedule) = scheduleDao.deleteSchedule(schedule)
    suspend fun getScheduleById(scheduleId: Long): Schedule? = scheduleDao.getScheduleById(scheduleId)
    suspend fun getScheduleWithTasks(scheduleId: Long): ScheduleWithTasks? = scheduleDao.getScheduleWithTasks(scheduleId)

    // متدهای رابطه Task-Schedule
    suspend fun linkTaskToSchedule(taskId: Long, scheduleId: Long) {
        val crossRef = TaskScheduleCrossRef(taskId, scheduleId)
        taskScheduleDao.insertTaskSchedule(crossRef)
    }

    suspend fun unlinkTaskFromSchedule(taskId: Long, scheduleId: Long) {
        taskScheduleDao.deleteTaskSchedule(taskId, scheduleId)
    }

    suspend fun getSchedulesForTask(taskId: Long): List<Schedule> {
        return scheduleDao.getSchedulesForTask(taskId)
    }

    suspend fun getTasksForSchedule(scheduleId: Long): List<Task> {
        return scheduleDao.getTasksForSchedule(scheduleId)
    }

    // متد برای اتصال چندین تسک به یک زمان‌بندی
    suspend fun linkTasksToSchedule(taskIds: List<Long>, scheduleId: Long) {
        taskIds.forEach { taskId ->
            linkTaskToSchedule(taskId, scheduleId)
        }
    }

    // متد برای اتصال چندین زمان‌بندی به یک تسک
    suspend fun linkSchedulesToTask(taskId: Long, scheduleIds: List<Long>) {
        scheduleIds.forEach { scheduleId ->
            linkTaskToSchedule(taskId, scheduleId)
        }
    }
    suspend fun getAllSchedulesD(): List<Schedule> {
        return scheduleDao.getAllSchedulesForDebug()
    }

    fun getAllSchedulesWithCalculatedPriority(): Flow<List<ScheduleWithCalculatedPriority>> =
        scheduleDao.getAllSchedulesWithCalculatedPriority()




}