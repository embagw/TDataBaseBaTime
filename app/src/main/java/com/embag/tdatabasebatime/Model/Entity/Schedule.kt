package com.embag.tdatabasebatime.Model.Entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "schedules")
data class Schedule(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val durationMinutes: Long,
    val recurrencePattern: String? = null, // برای زمانبندی‌های تکراری
    val isActive: Boolean = true
)

