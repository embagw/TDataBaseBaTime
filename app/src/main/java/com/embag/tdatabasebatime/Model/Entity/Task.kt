package com.embag.tdatabasebatime.Model.Entity

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val category: String = "Tasks",
    val title: String,
    val description: String? = null,
    val priority: Int, // 1=بالاترین, 2, 3, 4
    val dueDate: LocalDateTime? = null, // تاریخ سررسید اختیاری
    val status: TaskStatus = TaskStatus.NEEDS_DOING,
    val estimatedDurationMinutes: Long = 0
)

enum class TaskStatus {
    NEEDS_DOING, DONE, CANCELLED
}

// Converters برای Room
class Converters {
    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter
    fun fromLocalDateTime(value: LocalDateTime?): String? {
        return value?.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter
    fun toLocalDateTime(value: String?): LocalDateTime? {
        return value?.let { LocalDateTime.parse(it, DateTimeFormatter.ISO_LOCAL_DATE_TIME) }
    }

    @TypeConverter
    fun fromTaskStatus(status: TaskStatus): String {
        return status.name
    }

    @TypeConverter
    fun toTaskStatus(value: String): TaskStatus {
        return TaskStatus.valueOf(value)
    }
}