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
data class Schedule @RequiresApi(Build.VERSION_CODES.O) constructor(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val categoryId: Long? = null,
    val type: ScheduleType = ScheduleType.SCHEDULED,

    val title: String = "",
    val description: String? = null,

    val scheduleDate: LocalDate? = null,
    val startTime: LocalTime? = null,
    val endTime: LocalTime? = null,

    val estimatedMinutes: Long? = null,
    val count: Int? = null,
    val currentCount: Int = 0,

    // فیلدهای جدید برای تکرار
    val repeatType: RepeatType = RepeatType.NONE,
    val repeatInterval: Int = 1, // هر چند روز/هفته/ماه

    // برای تکرار هفتگی - روزهای هفته
    val repeatDaysOfWeek: String? = null, // "1,2,5" برای شنبه، یکشنبه، چهارشنبه

    // برای تکرار ماهیانه - روز ماه
    val repeatDayOfMonth: Int? = null,

    // برای تکرار سالانه - روز و ماه
    val repeatMonthOfYear: Int? = null, // 1-12

    val repeatEndDate: LocalDate? = null,
    val repeatCount: Int? = null, // تعداد دفعات تکرار

    val isActive: Boolean = true,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

enum class RepeatType {
    NONE,
    DAILY,
    WEEKLY,
    MONTHLY,
    YEARLY,
    CUSTOM_DAYS  // برای روزهای خاص هفته
}

enum class ScheduleType {
    SCHEDULED,
    ESTIMATED,
    COUNT,
    EVENT
}
