package com.embag.tdatabasebatime.Model.Entity

import androidx.room.ColumnInfo
import androidx.room.Embedded

// برای کوئری‌های پیشرفته
data class SchedulePriorityView(
    @Embedded val schedule: Schedule,
    @ColumnInfo(name = "max_priority") val maxPriority: Int?
)