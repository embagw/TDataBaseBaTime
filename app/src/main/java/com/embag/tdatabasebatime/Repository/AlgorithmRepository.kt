package com.embag.tdatabasebatime.Repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.embag.tdatabasebatime.Model.DAO.ScheduleDao
import com.embag.tdatabasebatime.Model.DAO.TaskDao
import com.embag.tdatabasebatime.Model.DAO.TaskScheduleDao
import com.embag.tdatabasebatime.Model.Entity.Schedule
import com.embag.tdatabasebatime.Model.Entity.ScheduleType
import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.ChronoUnit

@RequiresApi(Build.VERSION_CODES.O)
class AlgorithmRepository(
    private val scheduleDao: ScheduleDao,
    private val taskDao: TaskDao,
    private val taskScheduleDao: TaskScheduleDao
) {

    // دریافت زمان‌های خالی روز
    suspend fun calculateFreeSlots(
        targetDate: LocalDate,
        dayStart: LocalTime = LocalTime.of(0, 0),  // 00:00
        dayEnd: LocalTime = LocalTime.of(23, 59)   // 23:59
    ): List<Pair<LocalTime, LocalTime>> {

        // 1. دریافت زمان‌بندی‌های SCHEDULED برای این تاریخ
        val scheduledTasks = scheduleDao.getSchedulesForDate(targetDate)
            .filter {
                it.type == ScheduleType.SCHEDULED &&
                        it.isActive &&
                        it.startTime != null &&
                        it.endTime != null
            }
            .sortedBy { it.startTime }

        val freeSlots = mutableListOf<Pair<LocalTime, LocalTime>>()
        var currentTime = dayStart

        // 2. محاسبه زمان‌های خالی
        for (task in scheduledTasks) {
            task.startTime?.let { startTime ->
                if (currentTime.isBefore(startTime)) {
                    freeSlots.add(Pair(currentTime, startTime))
                }
                currentTime = task.endTime ?: currentTime
            }
        }

        // زمان خالی انتهای روز
        if (currentTime.isBefore(dayEnd)) {
            freeSlots.add(Pair(currentTime, dayEnd))
        }

        return freeSlots
    }

    // دریافت زمان‌بندی‌های ESTIMATED برای یک تاریخ خاص
    suspend fun getEstimatedSchedulesForDate(date: LocalDate): List<ScheduleWithPriority> {
        // 🆕 دریافت زمان‌بندی‌های تخمینی که تاریخشان با تاریخ انتخاب شده مطابقت دارد
        val allSchedules = scheduleDao.getSchedulesForDate(date)

        val estimatedSchedules = allSchedules
            .filter {
                it.type == ScheduleType.ESTIMATED &&
                        it.isActive &&
                        it.estimatedMinutes != null &&
                        it.scheduleDate == date  // 🆕 مهم: فقط تاریخ‌های مطابقت‌دار
            }

        // دیباگ: نمایش زمان‌بندی‌های یافت شده
        if (estimatedSchedules.isNotEmpty()) {
            println("📊 پیدا شد ${estimatedSchedules.size} زمان‌بندی تخمینی برای تاریخ $date")
            estimatedSchedules.forEach {
                println("   - ${it.title} (${it.estimatedMinutes} دقیقه)")
            }
        } else {
            println("⚠️ هیچ زمان‌بندی تخمینی برای تاریخ $date پیدا نشد")
        }

        // محاسبه اولویت برای هر زمان‌بندی
        val schedulesWithPriority = mutableListOf<ScheduleWithPriority>()

        for (schedule in estimatedSchedules) {
            val relatedTasks = taskScheduleDao.getTasksForSchedule(schedule.id)
            var minPriority = 4 // پیش‌فرض

            for (taskCrossRef in relatedTasks) {
                val task = taskDao.getTaskById(taskCrossRef.taskId)
                task?.let {
                    if (it.priority < minPriority) {
                        minPriority = it.priority
                    }
                }
            }

            schedulesWithPriority.add(
                ScheduleWithPriority(
                    schedule = schedule,
                    priority = minPriority
                )
            )
        }

        // مرتب‌سازی بر اساس اولویت (اولویت کمتر = مهم‌تر)
        return schedulesWithPriority.sortedBy { it.priority }
    }

    // اجرای الگوریتم اصلی
    suspend fun runSchedulingAlgorithm(
        targetDate: LocalDate,
        dayStart: LocalTime = LocalTime.of(8, 0),
        dayEnd: LocalTime = LocalTime.of(22, 0)
    ): AlgorithmResult {

        // 1. دریافت زمان‌های خالی
        val freeSlots = calculateFreeSlots(targetDate, dayStart, dayEnd)

        // 2. دریافت زمان‌بندی‌های ESTIMATED با اولویت
        val estimatedSchedulesWithPriority = getEstimatedSchedulesForDate(targetDate)

        // 3. اجرای الگوریتم
        val convertedSchedules = mutableListOf<Schedule>()
        val failedSchedules = mutableListOf<Schedule>()
        val remainingFreeSlots = freeSlots.toMutableList()

        for (scheduleWithPriority in estimatedSchedulesWithPriority) {
            val estimatedSchedule = scheduleWithPriority.schedule
            val requiredMinutes = estimatedSchedule.estimatedMinutes ?: continue
            var scheduled = false

            for (i in remainingFreeSlots.indices) {
                val (slotStart, slotEnd) = remainingFreeSlots[i]
                val slotDuration = ChronoUnit.MINUTES.between(slotStart, slotEnd)

                if (slotDuration >= requiredMinutes) {
                    // زمان کافی موجود است - تبدیل به SCHEDULED
                    val scheduledEndTime = slotStart.plusMinutes(requiredMinutes)

                    val convertedSchedule = estimatedSchedule.copy(
                        type = ScheduleType.SCHEDULED,
                        scheduleDate = targetDate,
                        startTime = slotStart,
                        endTime = scheduledEndTime,
                        estimatedMinutes = null // پاک کردن فیلد تخمینی
                    )

                    // ذخیره در دیتابیس
                    scheduleDao.updateSchedule(convertedSchedule)
                    convertedSchedules.add(convertedSchedule)

                    // به‌روزرسانی زمان خالی
                    remainingFreeSlots[i] = Pair(scheduledEndTime, slotEnd)
                    scheduled = true
                    break
                }
            }

            if (!scheduled) {
                failedSchedules.add(estimatedSchedule)
            }
        }

        // محاسبه کل زمان خالی باقی‌مانده
        val totalFreeMinutes = remainingFreeSlots.sumOf { slot ->
            ChronoUnit.MINUTES.between(slot.first, slot.second).toLong()
        }

        return AlgorithmResult(
            targetDate = targetDate,
            convertedSchedules = convertedSchedules,
            failedSchedules = failedSchedules,
            remainingFreeSlots = remainingFreeSlots,
            totalConverted = convertedSchedules.size,
            totalFailed = failedSchedules.size,
            totalFreeMinutes = totalFreeMinutes,
            dayStart = dayStart,
            dayEnd = dayEnd
        )
    }
}

// مدل‌های کمکی
data class ScheduleWithPriority(
    val schedule: Schedule,
    val priority: Int
)

data class AlgorithmResult(
    val targetDate: LocalDate,
    val convertedSchedules: List<Schedule>,
    val failedSchedules: List<Schedule>,
    val remainingFreeSlots: List<Pair<LocalTime, LocalTime>>,
    val totalConverted: Int,
    val totalFailed: Int,
    val totalFreeMinutes: Long, // تغییر به Long
    val dayStart: LocalTime,
    val dayEnd: LocalTime
)