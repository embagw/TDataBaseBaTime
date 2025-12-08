package com.embag.tdatabasebatime.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.embag.tdatabasebatime.Model.Task
import com.embag.tdatabasebatime.Model.TaskStatus
import com.embag.tdatabasebatime.Repository.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class TaskViewModel(private val repository: TaskRepository) : ViewModel() {

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()

    private val _currentTask = MutableStateFlow<Task?>(null)
    val currentTask: StateFlow<Task?> = _currentTask.asStateFlow()

    init {
        loadTasks()
    }

    private fun loadTasks() {
        viewModelScope.launch {
            repository.getAllTasks().collect { taskList ->
                _tasks.value = taskList
            }
        }
    }

    fun setCurrentTask(task: Task?) {
        _currentTask.value = task
    }

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

    fun getPriorityText(priority: Int): String {
        return when (priority) {
            1 -> "اولویت بالا"
            2 -> "اولویت متوسط"
            3 -> "اولویت پایین"
            4 -> "بدون اولویت"
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
}