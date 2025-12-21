package com.embag.tdatabasebatime.Model.Entity

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

// برای نمایش Schedule به همراه بالاترین اولویت Taskهای متصل
data class ScheduleWithTasks(
    @Embedded val schedule: Schedule,

    @Relation(
        parentColumn = "categoryId",
        entityColumn = "id"
    )
    val category: Category?,

    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = TaskScheduleCrossRef::class,
            parentColumn = "scheduleId",
            entityColumn = "taskId"
        )
    )
    val tasks: List<Task>
) {
    val calculatedPriority: Int
        get() = tasks.minByOrNull { it.priority }?.priority ?: 4 // پیش‌فرض 4
}