package com.embag.tdatabasebatime.Model.Entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalDateTime

@Entity(tableName = "schedules")
data class Schedule(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val type: ScheduleType,

    // برای نوع SCHEDULED
    val scheduledDateTime: LocalDateTime? = null,

    // برای نوع ESTIMATED
    val estimatedMinutes: Long? = null,

    // برای نوع COUNT
    val count: Int? = null,
    val currentCount: Int = 0,

    // برای نوع EVENT
    val eventDate: LocalDate? = null,

    val title: String,
    val description: String? = null,
    val isActive: Boolean = true,
    val createdAt: LocalDateTime = LocalDateTime.now()
)