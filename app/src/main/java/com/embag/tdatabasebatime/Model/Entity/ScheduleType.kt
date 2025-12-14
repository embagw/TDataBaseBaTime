package com.embag.tdatabasebatime.Model.Entity

enum class ScheduleType {
    SCHEDULED,    // برنامه‌ریزی شده (ساعت و تاریخ مشخص)
    ESTIMATED,    // تخمین (بازه زمانی)
    COUNT,        // تعداد (تعداد دفعات)
    EVENT         // رویداد (تاریخ)
}