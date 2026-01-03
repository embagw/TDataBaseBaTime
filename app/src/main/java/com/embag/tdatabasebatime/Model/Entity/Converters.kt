package com.embag.tdatabasebatime.Model.Entity

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.TypeConverter
import com.embag.tdatabasebatime.Model.Entity.ScheduleType
import com.embag.tdatabasebatime.Model.Entity.TaskStatus
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class Converters {




    @TypeConverter
    fun fromTaskStatus(status: TaskStatus?): String? {
        return status?.name
    }

    @TypeConverter
    fun toTaskStatus(value: String?): TaskStatus? {
        return value?.let { TaskStatus.valueOf(it) }
    }

    @TypeConverter
    fun fromScheduleType(type: ScheduleType?): String? {
        return type?.name
    }

    @TypeConverter
    fun toScheduleType(value: String?): ScheduleType? {
        return value?.let { ScheduleType.valueOf(it) }
    }



    @TypeConverter
    fun fromRepeatType(repeatType: RepeatType): String = repeatType.name

    @TypeConverter
    fun toRepeatType(value: String): RepeatType = RepeatType.valueOf(value)



    @TypeConverter
    fun fromLocalDateTime(date: LocalDateTime?): String? = date?.toString()

    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter
    fun toLocalDateTime(value: String?): LocalDateTime? = value?.let { LocalDateTime.parse(it) }

    @TypeConverter
    fun fromLocalDate(date: LocalDate?): String? = date?.toString()

    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter
    fun toLocalDate(value: String?): LocalDate? = value?.let { LocalDate.parse(it) }

    @TypeConverter
    fun fromLocalTime(time: LocalTime?): String? = time?.toString()

    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter
    fun toLocalTime(value: String?): LocalTime? = value?.let { LocalTime.parse(it) }

    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter
    fun fromDayOfWeekList(days: List<DayOfWeek>?): String? {
        return days?.joinToString(",") { it.value.toString() }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter
    fun toDayOfWeekList(value: String?): List<DayOfWeek>? {
        return value?.split(",")?.map { DayOfWeek.of(it.toInt()) }
    }



}