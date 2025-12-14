package com.embag.tdatabasebatime.Model.Entity

import androidx.room.ColumnInfo
import androidx.room.Embedded

data class ScheduleWithCalculatedPriority(
    @Embedded val schedule: Schedule,
    @ColumnInfo(name = "calculated_priority") val calculatedPriority: Int?
)