package com.embag.tdatabasebatime.Model.Entity

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

/// برای نمایش Task به همراه تمام Scheduleهای مرتبط
data class TaskWithSchedules(
    @Embedded val task: Task,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = TaskScheduleCrossRef::class,
            parentColumn = "taskId",
            entityColumn = "scheduleId"
        )
    )
    val schedules: List<Schedule>
)