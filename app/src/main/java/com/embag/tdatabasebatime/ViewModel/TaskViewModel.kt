package com.embag.tdatabasebatime.ViewModel

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.embag.tdatabasebatime.Model.BackUP.BackupManager
import com.embag.tdatabasebatime.Model.Entity.Category
import com.embag.tdatabasebatime.Model.Entity.CategoryWithTaskCount
import com.embag.tdatabasebatime.Model.Entity.RepeatType
import com.embag.tdatabasebatime.Model.Entity.Schedule
import com.embag.tdatabasebatime.Model.Entity.ScheduleType
import com.embag.tdatabasebatime.Model.Entity.ScheduleWithTasks
import com.embag.tdatabasebatime.Model.Entity.Task
import com.embag.tdatabasebatime.Model.Entity.TaskStatus
import com.embag.tdatabasebatime.Model.Entity.TaskWithRelations
import com.embag.tdatabasebatime.Repository.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class TaskViewModel(private val repository: TaskRepository,private val context: Context) : ViewModel() {

    private val backupManager = BackupManager(context)

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()

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
        loadSchedulesWithTasks()
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

    @RequiresApi(Build.VERSION_CODES.O)
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

    @RequiresApi(Build.VERSION_CODES.O)
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
    fun getSchedulePriorityWithDefault(scheduleWithTasks: ScheduleWithTasks): Int {
        return scheduleWithTasks.calculatedPriority
    }

    fun getSchedulePriorityTextWithDefault(scheduleWithTasks: ScheduleWithTasks): String {
        val priority = scheduleWithTasks.calculatedPriority
        return when {
            priority <= 1 -> "ضروری"
            priority == 2 -> "مهم"
            priority == 3 -> "فوری"
            else -> "عادی" // پیش‌فرض برای 4 و بالاتر
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun createSchedule(
        categoryId: Long?,
        type: ScheduleType,
        title: String,
        description: String? = null,
        scheduleDate: LocalDate? = null,
        startTime: LocalTime? = null,
        endTime: LocalTime? = null,
        estimatedMinutes: Long? = null,
        count: Int? = null,
        // پارامترهای جدید تکرار
        repeatType: RepeatType = RepeatType.NONE,
        repeatInterval: Int = 1,
        repeatDaysOfWeek: Set<DayOfWeek>? = null, // تغییر به Set
        repeatDayOfMonth: Int? = null,
        repeatMonthOfYear: Int? = null,
        repeatEndDate: LocalDate? = null,
        repeatCount: Int? = null
    ){
        viewModelScope.launch {
            val schedule = Schedule(
                categoryId = categoryId,
                type = type,
                title = title,
                description = description,
                scheduleDate = scheduleDate,
                startTime = startTime,
                endTime = endTime,
                estimatedMinutes = estimatedMinutes,
                count = count,
                repeatType = repeatType,
                repeatInterval = repeatInterval,
                repeatDaysOfWeek = repeatDaysOfWeek?.joinToString(",") { it.value.toString() },
                repeatDayOfMonth = repeatDayOfMonth,
                repeatMonthOfYear = repeatMonthOfYear,
                repeatEndDate = repeatEndDate,
                repeatCount = repeatCount
            )
            repository.insertSchedule(schedule)
        }
    }

    // به روزرسانی متد getScheduleDetails
    @RequiresApi(Build.VERSION_CODES.O)
    fun getScheduleDetails(schedule: Schedule): String {
        val baseDetails = when (schedule.type) {
            ScheduleType.SCHEDULED -> {
                val dateStr = schedule.scheduleDate?.format(DateTimeFormatter.ofPattern("yyyy/MM/dd")) ?: "تعیین نشده"
                val startStr = schedule.startTime?.format(DateTimeFormatter.ofPattern("HH:mm")) ?: "تعیین نشده"
                val endStr = schedule.endTime?.format(DateTimeFormatter.ofPattern("HH:mm")) ?: "تعیین نشده"
                "$dateStr - $startStr تا $endStr"
            }
            ScheduleType.ESTIMATED -> {
                "${schedule.estimatedMinutes} دقیقه"
            }
            ScheduleType.COUNT -> {
                "${schedule.currentCount}/${schedule.count ?: 0} بار"
            }
            ScheduleType.EVENT -> {
                schedule.scheduleDate?.format(DateTimeFormatter.ofPattern("yyyy/MM/dd")) ?: "تعیین نشده"
            }
        }

        // اضافه کردن اطلاعات تکرار
        val repeatInfo = if (schedule.repeatType != RepeatType.NONE) {
            val repeatText = when (schedule.repeatType) {
                RepeatType.DAILY -> "هر ${schedule.repeatInterval} روز"
                RepeatType.WEEKLY -> "هر ${schedule.repeatInterval} هفته"
                RepeatType.MONTHLY -> "هر ${schedule.repeatInterval} ماه"
                RepeatType.YEARLY -> "هر ${schedule.repeatInterval} سال"
                RepeatType.CUSTOM_DAYS -> "روزهای خاص هفته"
                else -> ""
            }
            " (تکرار: $repeatText)"
        } else ""

        return baseDetails + repeatInfo
    }


    // متدهای backup
    fun createBackup(): Boolean {
        return backupManager.createBackup()
    }

    fun getBackupFiles(): List<File> {
        return backupManager.getBackupFiles()
    }

    fun restoreFromBackup(backupFile: File): Boolean {
        // قبل از restore، باید دیتابیس فعلی را ببندیم
        val result = backupManager.restoreFromBackup(backupFile)

        // بعد از restore، دیتابیس جدید باید بارگیری شود
        if (result) {
            loadAllData() // بارگیری مجدد همه داده‌ها
        }

        return result
    }

    fun deleteBackupFile(file: File): Boolean {
        return backupManager.deleteBackupFile(file)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun createSampleEstimatedSchedule() {
        viewModelScope.launch {
            val schedule = Schedule(
                categoryId = null,
                type = ScheduleType.ESTIMATED,
                title = "نمونه زمان‌بندی تخمینی",
                description = "برای تست الگوریتم",
                scheduleDate = LocalDate.now(),  // تاریخ امروز
                estimatedMinutes = 60,  // 1 ساعت
                isActive = true
            )
            repository.insertSchedule(schedule)
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun createTestEstimatedSchedule() {
        viewModelScope.launch {
            val schedule = Schedule(
                categoryId = null,
                type = ScheduleType.ESTIMATED,
                title = "تست تخمینی - ${LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))}",
                description = "ایجاد شده برای تست",
                scheduleDate = LocalDate.now(), // تاریخ امروز
                estimatedMinutes = 45,
                isActive = true
            )
            repository.insertSchedule(schedule)
        }
    }

    // متد برای گرفتن همه زمان‌بندی‌ها (برای دیباگ)
    suspend fun getAllSchedulesForDebug(): List<Schedule> {
        return repository.getAllSchedulesD()
    }
}


