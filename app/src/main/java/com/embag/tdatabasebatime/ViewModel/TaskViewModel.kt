package com.embag.tdatabasebatime.ViewModel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.embag.tdatabasebatime.Model.Entity.Schedule
import com.embag.tdatabasebatime.Model.Entity.ScheduleWithCalculatedPriority
import com.embag.tdatabasebatime.Model.Entity.Task
import com.embag.tdatabasebatime.Model.Entity.TaskStatus
import com.embag.tdatabasebatime.Model.Entity.TaskWithSchedules
import com.embag.tdatabasebatime.Repository.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class TaskViewModel(private val repository: TaskRepository) : ViewModel() {

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()

    private val _schedulesWithPriority = MutableStateFlow<List<ScheduleWithCalculatedPriority>>(emptyList())
    val schedulesWithPriority: StateFlow<List<ScheduleWithCalculatedPriority>> = _schedulesWithPriority.asStateFlow()

    private val _tasksWithSchedules = MutableStateFlow<List<TaskWithSchedules>>(emptyList())
    val tasksWithSchedules: StateFlow<List<TaskWithSchedules>> = _tasksWithSchedules.asStateFlow()

    private val _currentTask = MutableStateFlow<Task?>(null)
    val currentTask: StateFlow<Task?> = _currentTask.asStateFlow()

    private val _currentSchedule = MutableStateFlow<Schedule?>(null)
    val currentSchedule: StateFlow<Schedule?> = _currentSchedule.asStateFlow()

    init {
        loadTasks()
        loadSchedulesWithPriority()
        loadTasksWithSchedules()
    }

    private fun loadTasks() {
        viewModelScope.launch {
            repository.getAllTasks().collect { taskList ->
                _tasks.value = taskList
            }
        }
    }

    private fun loadSchedulesWithPriority() {
        viewModelScope.launch {
            repository.getAllSchedulesWithCalculatedPriority().collect { scheduleList ->
                _schedulesWithPriority.value = scheduleList
            }
        }
    }

    private fun loadTasksWithSchedules() {
        viewModelScope.launch {
            repository.getTasksWithSchedules().collect { tasksWithSchedules ->
                _tasksWithSchedules.value = tasksWithSchedules
            }
        }
    }

    fun setCurrentTask(task: Task?) {
        _currentTask.value = task
    }

    fun setCurrentSchedule(schedule: Schedule?) {
        _currentSchedule.value = schedule
    }

    // متدهای قدیمی Task که قبلا استفاده می‌شدند (برگشت اضافه می‌کنیم)
    fun createTask(
        category: String,
        title: String,
        description: String?,
        priority: Int,
        dueDate: LocalDateTime,
        status: TaskStatus = TaskStatus.NEEDS_DOING
    ) {
        viewModelScope.launch {
            val task = Task(
                category = category,
                title = title,
                description = description,
                priority = priority,
                dueDate = dueDate,
                status = status
            )
            repository.insertTask(task)
        }
    }

    fun updateTask(
        id: Long,
        category: String,
        title: String,
        description: String?,
        priority: Int,
        dueDate: LocalDateTime,
        status: TaskStatus
    ) {
        viewModelScope.launch {
            val task = Task(
                id = id,
                category = category,
                title = title,
                description = description,
                priority = priority,
                dueDate = dueDate,
                status = status
            )
            repository.updateTask(task)
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    // متدهای جدید برای Schedule
    fun createSchedule(
        startTime: LocalDateTime,
        endTime: LocalDateTime,
        durationMinutes: Long,
        recurrencePattern: String? = null
    ) {
        viewModelScope.launch {
            val schedule = Schedule(
                startTime = startTime,
                endTime = endTime,
                durationMinutes = durationMinutes,
                recurrencePattern = recurrencePattern
            )
            repository.insertSchedule(schedule)
        }
    }

    fun updateSchedule(schedule: Schedule) {
        viewModelScope.launch {
            repository.updateSchedule(schedule)
        }
    }

    fun deleteSchedule(schedule: Schedule) {
        viewModelScope.launch {
            repository.deleteSchedule(schedule)
        }
    }

    fun linkCurrentTaskToSchedule(scheduleId: Long) {
        viewModelScope.launch {
            val taskId = _currentTask.value?.id ?: return@launch
            repository.linkTaskToSchedule(taskId, scheduleId)
        }
    }

    // متدهای کمکی
    fun getPriorityText(priority: Int): String {
        return when (priority) {
            1 -> "ضروری"
            2 -> "مهم"
            3 -> "فوری"
            4 -> "عادی"
            else -> "نامشخص"
        }
    }

    fun getStatusText(status: TaskStatus): String {
        return when (status) {
            TaskStatus.NEEDS_DOING -> "نیاز به انجام"
            TaskStatus.DONE -> "انجام شده"
            TaskStatus.CANCELLED -> "لغو شده"
        }
    }

    // متد برای گرفتن اولویت Schedule
    fun getSchedulePriorityText(scheduleWithPriority: ScheduleWithCalculatedPriority): String {
        val priority = scheduleWithPriority.calculatedPriority ?: Int.MAX_VALUE
        return when {
            priority <= 1 -> "ضروری"
            priority == 2 -> "مهم"
            priority == 3 -> "فوری"
            priority >= 4 -> "عادی"
            else -> "بدون تسک"
        }
    }

    // متد برای فرمت کردن تاریخ (راه‌حل برای ارور تاریخ)
    @RequiresApi(Build.VERSION_CODES.O)
    fun formatDueDate(dueDate: LocalDateTime?): String {
        return if (dueDate != null) {
            dueDate.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"))
        } else {
            "تعیین نشده"
        }
    }
}