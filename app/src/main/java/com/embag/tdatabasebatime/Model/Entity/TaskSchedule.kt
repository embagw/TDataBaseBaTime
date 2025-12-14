package com.embag.tdatabasebatime.Model.Entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "task_schedule",
    primaryKeys = ["taskId", "scheduleId"],
    foreignKeys = [
        ForeignKey(
            entity = Task::class,
            parentColumns = ["id"],
            childColumns = ["taskId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Schedule::class,
            parentColumns = ["id"],
            childColumns = ["scheduleId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["taskId"]),
        Index(value = ["scheduleId"])
    ]
)
data class TaskScheduleCrossRef(
    val taskId: Long,
    val scheduleId: Long
)