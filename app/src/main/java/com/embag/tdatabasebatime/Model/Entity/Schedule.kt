package com.embag.tdatabasebatime.Model.Entity

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@Entity(tableName = "schedules")
data class Schedule(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "categoryId", index = true)
    val categoryId: Long? = null,

    @ColumnInfo(name = "type", typeAffinity = ColumnInfo.TEXT)
    val type: ScheduleType,

    // تاریخ زمان‌بندی (برای همه انواع)
    @ColumnInfo(name = "scheduleDate")
    val scheduleDate: LocalDate? = null,

    // ساعت شروع و پایان (فقط برای SCHEDULED)
    @ColumnInfo(name = "startTime")
    val startTime: LocalTime? = null,

    @ColumnInfo(name = "endTime")
    val endTime: LocalTime? = null,

    // برای نوع ESTIMATED
    @ColumnInfo(name = "estimatedMinutes")
    val estimatedMinutes: Long? = null,

    // برای نوع COUNT
    @ColumnInfo(name = "count")
    val count: Int? = null,

    @ColumnInfo(name = "currentCount", defaultValue = "0")
    val currentCount: Int = 0,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "description")
    val description: String? = null,

    @ColumnInfo(name = "isActive", defaultValue = "1")
    val isActive: Boolean = true,

    @ColumnInfo(name = "createdAt")
    val createdAt: LocalDateTime = LocalDateTime.now()
)