package com.embag.tdatabasebatime.ViewModel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.embag.tdatabasebatime.Model.Entity.Category
import com.embag.tdatabasebatime.Model.Entity.CategoryWithTaskCount
import com.embag.tdatabasebatime.Model.Entity.Schedule
import com.embag.tdatabasebatime.Model.Entity.ScheduleType
import com.embag.tdatabasebatime.Model.Entity.ScheduleWithCalculatedPriority
import com.embag.tdatabasebatime.Model.Entity.ScheduleWithTasks
import com.embag.tdatabasebatime.Model.Entity.Task
import com.embag.tdatabasebatime.Model.Entity.TaskStatus
import com.embag.tdatabasebatime.Model.Entity.TaskWithRelations
import com.embag.tdatabasebatime.Repository.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class TaskViewModel(private val repository: TaskRepository) : ViewModel() {

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()

    // تغییر: از ScheduleWithTasks به جای ScheduleWithCalculatedPriority استفاده کنید
    private val _schedulesWithTasks = MutableStateFlow<List<ScheduleWithTasks>>(emptyList())
    val schedulesWithTasks: StateFlow<List<ScheduleWithTasks>> = _schedulesWithTasks.asStateFlow()

    private val _tasksWithRelations = MutableStateFlow<List<TaskWithRelations>>(emptyList())
    val tasksWithRelations: StateFlow<List<TaskWithRelations>> = _tasksWithRelations.asStateFlow()

    private val _currentTask = MutableStateFlow<Task?>(null)
    val currentTask: StateFlow<Task?> = _currentTask.asStateFlow()

    private val _currentSchedule = MutableStateFlow<Schedule?>(null)
    val currentSchedule: StateFlow<Schedule?> = _currentSchedule.asStateFlow()

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    private val _categoriesWithTaskCount = MutableStateFlow<List<CategoryWithTaskCount>>(emptyList())
    val categoriesWithTaskCount: StateFlow<List<CategoryWithTaskCount>> = _categoriesWithTaskCount.asStateFlow()

    private val _availableSchedules = MutableStateFlow<List<Schedule>>(emptyList())
    val availableSchedules: StateFlow<List<Schedule>> = _availableSchedules.asStateFlow()

    private val _tasksForCurrentSchedule = MutableStateFlow<List<Task>>(emptyList())
    val tasksForCurrentSchedule: StateFlow<List<Task>> = _tasksForCurrentSchedule.asStateFlow()

    private val _schedulesForCurrentTask = MutableStateFlow<List<Schedule>>(emptyList())
    val schedulesForCurrentTask: StateFlow<List<Schedule>> = _schedulesForCurrentTask.asStateFlow()

    init {
        loadAllData()
    }

    private fun loadAllData() {
        loadTasks()
        loadSchedulesWithTasks() // تغییر نام این تابع
        loadTasksWithRelations()
        loadCategories()
        loadCategoriesWithTaskCount()
        loadAvailableSchedules()
    }

    private fun loadTasks() {
        viewModelScope.launch {
            repository.getAllTasks().collect { taskList ->
                _tasks.value = taskList
            }
        }
    }

    // تغییر: این تابع را به loadSchedulesWithTasks تغییر دهید
    private fun loadSchedulesWithTasks() {
        viewModelScope.launch {
            repository.getSchedulesWithTasks().collect { scheduleList ->
                _schedulesWithTasks.value = scheduleList
            }
        }
    }

    private fun loadTasksWithRelations() {
        viewModelScope.launch {
            repository.getTasksWithRelations().collect { tasks ->
                _tasksWithRelations.value = tasks
            }
        }
    }

    private fun loadCategories() {
        viewModelScope.launch {
            repository.getAllCategories().collect { categoryList ->
                _categories.value = categoryList
            }
        }
    }

    private fun loadCategoriesWithTaskCount() {
        viewModelScope.launch {
            repository.getCategoriesWithTaskCount().collect { categories ->
                _categoriesWithTaskCount.value = categories
            }
        }
    }

    private fun loadAvailableSchedules() {
        viewModelScope.launch {
            repository.getAllSchedules().collect { schedules ->
                _availableSchedules.value = schedules
            }
        }
    }

    fun loadTasksForCurrentSchedule() {
        viewModelScope.launch {
            val scheduleId = _currentSchedule.value?.id ?: return@launch
            _tasksForCurrentSchedule.value = repository.getTasksForSchedule(scheduleId)
        }
    }

    fun loadSchedulesForCurrentTask() {
        viewModelScope.launch {
            val taskId = _currentTask.value?.id ?: return@launch
            _schedulesForCurrentTask.value = repository.getSchedulesForTask(taskId)
        }
    }

    fun setCurrentTask(task: Task?) {
        _currentTask.value = task
        if (task != null) {
            loadSchedulesForCurrentTask()
        }
    }

    fun setCurrentSchedule(schedule: Schedule?) {
        _currentSchedule.value = schedule
        if (schedule != null) {
            loadTasksForCurrentSchedule()
        }
    }

    // متدهای Task
    fun createTask(
        categoryId: Long?,
        title: String,
        description: String?,
        priority: Int,
        dueDate: LocalDateTime?,
        status: TaskStatus = TaskStatus.NEEDS_DOING,
        estimatedDurationMinutes: Long = 0
    ) {
        viewModelScope.launch {
            val task = Task(
                categoryId = categoryId,
                title = title,
                description = description,
                priority = priority,
                dueDate = dueDate,
                status = status,
                estimatedDurationMinutes = estimatedDurationMinutes
            )
            repository.insertTask(task)
        }
    }

    fun updateTask(
        id: Long,
        categoryId: Long?,
        title: String,
        description: String?,
        priority: Int,
        dueDate: LocalDateTime?,
        status: TaskStatus
    ) {
        viewModelScope.launch {
            val task = Task(
                id = id,
                categoryId = categoryId,
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

    // متدهای Category
    fun createCategory(name: String, color: String? = null) {
        viewModelScope.launch {
            val category = Category(
                name = name,
                color = color
            )
            repository.insertCategory(category)
        }
    }

    fun updateCategory(category: Category) {
        viewModelScope.launch {
            repository.updateCategory(category)
        }
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch {
            repository.deleteCategory(category)
        }
    }

    // متدهای Schedule
    fun createSchedule(
        type: ScheduleType,
        title: String,
        description: String? = null,
        scheduledDateTime: LocalDateTime? = null,
        estimatedMinutes: Long? = null,
        count: Int? = null,
        eventDate: LocalDate? = null
    ) {
        viewModelScope.launch {
            val schedule = Schedule(
                type = type,
                title = title,
                description = description,
                scheduledDateTime = scheduledDateTime,
                estimatedMinutes = estimatedMinutes,
                count = count,
                eventDate = eventDate
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

    // متدهای اتصال Task و Schedule
    fun linkTaskToSchedule(taskId: Long, scheduleId: Long) {
        viewModelScope.launch {
            repository.linkTaskToSchedule(taskId, scheduleId)
            loadSchedulesForCurrentTask()
            loadTasksForCurrentSchedule()
        }
    }

    fun unlinkTaskFromSchedule(taskId: Long, scheduleId: Long) {
        viewModelScope.launch {
            repository.unlinkTaskFromSchedule(taskId, scheduleId)
            loadSchedulesForCurrentTask()
            loadTasksForCurrentSchedule()
        }
    }

    fun linkTasksToSchedule(taskIds: List<Long>, scheduleId: Long) {
        viewModelScope.launch {
            repository.linkTasksToSchedule(taskIds, scheduleId)
            loadTasksForCurrentSchedule()
        }
    }

    fun linkSchedulesToTask(taskId: Long, scheduleIds: List<Long>) {
        viewModelScope.launch {
            repository.linkSchedulesToTask(taskId, scheduleIds)
            loadSchedulesForCurrentTask()
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

    fun getScheduleTypeText(type: ScheduleType): String {
        return when (type) {
            ScheduleType.SCHEDULED -> "برنامه‌ریزی شده"
            ScheduleType.ESTIMATED -> "تخمین زمانی"
            ScheduleType.COUNT -> "تعداد دفعات"
            ScheduleType.EVENT -> "رویداد"
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getScheduleDetails(schedule: Schedule): String {
        return when (schedule.type) {
            ScheduleType.SCHEDULED -> {
                schedule.scheduledDateTime?.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm")) ?: "تعیین نشده"
            }
            ScheduleType.ESTIMATED -> {
                "${schedule.estimatedMinutes} دقیقه"
            }
            ScheduleType.COUNT -> {
                "${schedule.currentCount}/${schedule.count ?: 0} بار"
            }
            ScheduleType.EVENT -> {
                schedule.eventDate?.format(DateTimeFormatter.ofPattern("yyyy/MM/dd")) ?: "تعیین نشده"
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun formatDueDate(dueDate: LocalDateTime?): String {
        return if (dueDate != null) {
            dueDate.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"))
        } else {
            "تعیین نشده"
        }
    }

    fun getCategoryName(categoryId: Long?): String {
        if (categoryId == null) return "بدون دسته‌بندی"
        return _categories.value.find { it.id == categoryId }?.name ?: "بدون دسته‌بندی"
    }

    fun getCategoryColor(categoryId: Long?): String {
        if (categoryId == null) return "#9E9E9E" // خاکستری برای بدون دسته‌بندی
        return _categories.value.find { it.id == categoryId }?.color ?: "#9E9E9E"
    }
    fun getSchedulePriorityText(scheduleWithTasks: ScheduleWithTasks): String {
        val priority = scheduleWithTasks.calculatedPriority
        return when {
            priority <= 1 -> "ضروری"
            priority == 2 -> "مهم"
            priority == 3 -> "فوری"
            priority >= 4 -> "عادی"
            else -> "بدون تسک"
        }
    }
}