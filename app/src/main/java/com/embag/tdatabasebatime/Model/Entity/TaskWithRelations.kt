package com.embag.tdatabasebatime.Model.Entity

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

// تسک به همراه دسته‌بندی و زمان‌بندی‌ها
data class TaskWithRelations(
    @Embedded val task: Task,

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
            parentColumn = "taskId",
            entityColumn = "scheduleId"
        )
    )
    val schedules: List<Schedule>
)

