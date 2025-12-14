package com.embag.tdatabasebatime.Model.Entity

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter



@Entity(
    tableName = "tasks",
    foreignKeys = [
        ForeignKey(
            entity = Category::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.SET_NULL
        )
    ]
)
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(index = true)
    val categoryId: Long? = null,

    val title: String,
    val description: String? = null,
    val priority: Int, // 1=بالاترین, 2, 3, 4
    val dueDate: LocalDateTime? = null,
    val status: TaskStatus = TaskStatus.NEEDS_DOING,
    val estimatedDurationMinutes: Long = 0,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)


enum class TaskStatus {
    NEEDS_DOING, DONE, CANCELLED
}

