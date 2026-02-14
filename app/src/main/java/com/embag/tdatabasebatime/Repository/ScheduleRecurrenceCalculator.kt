package com.embag.tdatabasebatime.Repository


import android.os.Build
import androidx.annotation.RequiresApi
import com.embag.tdatabasebatime.Model.Entity.RepeatType
import com.embag.tdatabasebatime.Model.Entity.Schedule
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.ChronoUnit

object ScheduleRecurrenceCalculator {

    @RequiresApi(Build.VERSION_CODES.O)
    fun isScheduleOccurringOnDate(schedule: Schedule, date: LocalDate): Boolean {
        // 1. بررسی زمان‌بندی بدون تکرار
        if (schedule.repeatType == RepeatType.NONE) {
            return schedule.scheduleDate == date
        }

        // 2. بررسی تاریخ شروع
        val startDate = schedule.scheduleDate ?: return false
        if (date.isBefore(startDate)) return false

        // 3. بررسی تاریخ پایان
        schedule.repeatEndDate?.let { endDate ->
            if (date.isAfter(endDate)) return false
        }

        // 4. بررسی تعداد تکرار (بدون فراخوانی تابع بازگشتی)
        schedule.repeatCount?.let { maxCount ->
            // محاسبه مستقیم تعداد تکرارها بدون فراخوانی بازگشتی
            val count = calculateOccurrenceCountDirectly(schedule, date)
            if (count > maxCount) return false
        }

        // 5. بررسی بر اساس نوع تکرار
        return when (schedule.repeatType) {
            RepeatType.DAILY -> {
                val daysBetween = ChronoUnit.DAYS.between(startDate, date)
                daysBetween >= 0 && daysBetween % schedule.repeatInterval == 0L
            }

            RepeatType.WEEKLY -> {
                // بررسی فاصله هفته‌ها
                val weeksBetween = ChronoUnit.WEEKS.between(startDate, date)
                if (weeksBetween % schedule.repeatInterval != 0L) return false

                // بررسی روز هفته
                val repeatDaysStr = schedule.repeatDaysOfWeek
                if (!repeatDaysStr.isNullOrEmpty()) {
                    // روزهای خاص انتخاب شده‌اند
                    val selectedDays = repeatDaysStr.split(",").map {
                        try {
                            DayOfWeek.of(it.toInt())
                        } catch (e: Exception) {
                            null
                        }
                    }.filterNotNull()

                    if (selectedDays.isEmpty()) {
                        // روز خاصی انتخاب نشده - روز هفته باید با شروع یکسان باشد
                        startDate.dayOfWeek == date.dayOfWeek
                    } else {
                        selectedDays.contains(date.dayOfWeek)
                    }
                } else {
                    // روز خاصی انتخاب نشده - روز هفته باید با شروع یکسان باشد
                    startDate.dayOfWeek == date.dayOfWeek
                }
            }

            RepeatType.MONTHLY -> {
                // بررسی فاصله ماه‌ها
                val monthsBetween = ChronoUnit.MONTHS.between(
                    startDate.withDayOfMonth(1),
                    date.withDayOfMonth(1)
                )
                if (monthsBetween % schedule.repeatInterval != 0L) return false

                // بررسی روز ماه
                val targetDay = if (schedule.repeatDayOfMonth != null) {
                    minOf(schedule.repeatDayOfMonth!!, date.lengthOfMonth())
                } else {
                    minOf(startDate.dayOfMonth, date.lengthOfMonth())
                }
                date.dayOfMonth == targetDay
            }

            RepeatType.YEARLY -> {
                // بررسی فاصله سال‌ها
                val yearsBetween = ChronoUnit.YEARS.between(startDate, date)
                if (yearsBetween % schedule.repeatInterval != 0L) return false

                // بررسی ماه و روز
                val targetMonth = schedule.repeatMonthOfYear ?: startDate.monthValue
                val targetDay = if (schedule.repeatDayOfMonth != null) {
                    minOf(schedule.repeatDayOfMonth!!, date.lengthOfMonth())
                } else {
                    minOf(startDate.dayOfMonth, date.lengthOfMonth())
                }

                date.monthValue == targetMonth && date.dayOfMonth == targetDay
            }

            RepeatType.CUSTOM_DAYS -> {
                val daysOfWeek = schedule.repeatDaysOfWeek?.split(",")?.map {
                    try {
                        DayOfWeek.of(it.toInt())
                    } catch (e: Exception) {
                        null
                    }
                }?.filterNotNull() ?: return false

                daysOfWeek.contains(date.dayOfWeek)
            }

            else -> false
        }
    }

    /**
     * محاسبه مستقیم تعداد تکرارها بدون فراخوانی بازگشتی
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun calculateOccurrenceCountDirectly(schedule: Schedule, untilDate: LocalDate): Int {
        if (schedule.repeatType == RepeatType.NONE) return 0

        val startDate = schedule.scheduleDate ?: return 0
        if (untilDate.isBefore(startDate)) return 0

        var count = 0
        var currentDate = startDate
        val maxIterations = 10000 // جلوگیری از حلقه بی‌نهایت
        var iteration = 0

        while (currentDate <= untilDate && iteration < maxIterations) {
            iteration++

            // بررسی اینکه آیا currentDate یک تکرار معتبر است
            val isOccurring = when (schedule.repeatType) {
                RepeatType.DAILY -> {
                    val daysBetween = ChronoUnit.DAYS.between(startDate, currentDate)
                    daysBetween >= 0 && daysBetween % schedule.repeatInterval == 0L
                }
                RepeatType.WEEKLY -> {
                    val weeksBetween = ChronoUnit.WEEKS.between(startDate, currentDate)
                    if (weeksBetween % schedule.repeatInterval != 0L) false else {
                        val repeatDaysStr = schedule.repeatDaysOfWeek
                        if (!repeatDaysStr.isNullOrEmpty()) {
                            val selectedDays = repeatDaysStr.split(",").mapNotNull {
                                try { DayOfWeek.of(it.toInt()) } catch (e: Exception) { null }
                            }
                            if (selectedDays.isEmpty()) {
                                startDate.dayOfWeek == currentDate.dayOfWeek
                            } else {
                                selectedDays.contains(currentDate.dayOfWeek)
                            }
                        } else {
                            startDate.dayOfWeek == currentDate.dayOfWeek
                        }
                    }
                }
                RepeatType.MONTHLY -> {
                    val monthsBetween = ChronoUnit.MONTHS.between(
                        startDate.withDayOfMonth(1),
                        currentDate.withDayOfMonth(1)
                    )
                    if (monthsBetween % schedule.repeatInterval != 0L) false else {
                        val targetDay = if (schedule.repeatDayOfMonth != null) {
                            minOf(schedule.repeatDayOfMonth!!, currentDate.lengthOfMonth())
                        } else {
                            minOf(startDate.dayOfMonth, currentDate.lengthOfMonth())
                        }
                        currentDate.dayOfMonth == targetDay
                    }
                }
                RepeatType.YEARLY -> {
                    val yearsBetween = ChronoUnit.YEARS.between(startDate, currentDate)
                    if (yearsBetween % schedule.repeatInterval != 0L) false else {
                        val targetMonth = schedule.repeatMonthOfYear ?: startDate.monthValue
                        val targetDay = if (schedule.repeatDayOfMonth != null) {
                            minOf(schedule.repeatDayOfMonth!!, currentDate.lengthOfMonth())
                        } else {
                            minOf(startDate.dayOfMonth, currentDate.lengthOfMonth())
                        }
                        currentDate.monthValue == targetMonth && currentDate.dayOfMonth == targetDay
                    }
                }
                RepeatType.CUSTOM_DAYS -> {
                    val daysOfWeek = schedule.repeatDaysOfWeek?.split(",")?.mapNotNull {
                        try { DayOfWeek.of(it.toInt()) } catch (e: Exception) { null }
                    }
                    daysOfWeek?.contains(currentDate.dayOfWeek) ?: false
                }
                else -> false
            }

            if (isOccurring) {
                count++
            }

            currentDate = currentDate.plusDays(1) // حرکت روزانه
        }

        return count
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun generateScheduleInstances(
        schedule: Schedule,
        fromDate: LocalDate,
        toDate: LocalDate
    ): List<ScheduleInstance> {
        val instances = mutableListOf<ScheduleInstance>()

        val startDate = schedule.scheduleDate ?: return emptyList()

        if (schedule.repeatType == RepeatType.NONE) {
            if (startDate in fromDate..toDate) {
                instances.add(ScheduleInstance(schedule, startDate))
            }
            return instances
        }

        // محدودیت‌های تکرار
        val maxCount = schedule.repeatCount ?: Int.MAX_VALUE
        val endDate = schedule.repeatEndDate ?: toDate

        var currentDate = startDate
        var count = 0

        // محدودیت برای جلوگیری از حلقه بی‌نهایت
        val maxIterations = 10000
        var iteration = 0

        while (currentDate <= endDate && currentDate <= toDate && count < maxCount && iteration < maxIterations) {
            iteration++

            if (currentDate >= fromDate && isScheduleOccurringOnDate(schedule, currentDate)) {
                instances.add(ScheduleInstance(schedule, currentDate))
                count++
            }

            // Move to next potential date
            currentDate = when (schedule.repeatType) {
                RepeatType.DAILY -> currentDate.plusDays(schedule.repeatInterval.toLong())
                RepeatType.WEEKLY -> currentDate.plusWeeks(schedule.repeatInterval.toLong())
                RepeatType.MONTHLY -> {
                    schedule.repeatDayOfMonth?.let { dayOfMonth ->
                        // Move to next month, keeping the day of month
                        currentDate.plusMonths(schedule.repeatInterval.toLong())
                            .withDayOfMonth(minOf(dayOfMonth, currentDate.plusMonths(schedule.repeatInterval.toLong()).lengthOfMonth()))
                    } ?: currentDate.plusMonths(schedule.repeatInterval.toLong())
                }
                RepeatType.YEARLY -> currentDate.plusYears(schedule.repeatInterval.toLong())
                RepeatType.CUSTOM_DAYS -> {
                    // For custom days, find next day in the list
                    findNextCustomDay(schedule, currentDate.plusDays(1))
                }
                else -> break
            }
        }

        return instances
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun findNextCustomDay(schedule: Schedule, fromDate: LocalDate): LocalDate {
        val daysOfWeek = schedule.repeatDaysOfWeek?.split(",")?.map {
            try {
                DayOfWeek.of(it.toInt())
            } catch (e: Exception) {
                null
            }
        }?.filterNotNull() ?: return fromDate

        var currentDate = fromDate
        for (i in 0..365) { // Limit to 1 year
            if (daysOfWeek.contains(currentDate.dayOfWeek)) {
                return currentDate
            }
            currentDate = currentDate.plusDays(1)
        }
        return fromDate
    }
}

data class ScheduleInstance(
    val schedule: Schedule,
    val occurrenceDate: LocalDate
)
