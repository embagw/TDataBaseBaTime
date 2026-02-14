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

    suspend fun calculateFreeSlots(
        targetDate: LocalDate,
        dayStart: LocalTime = LocalTime.of(0, 0),
        dayEnd: LocalTime = LocalTime.of(23, 59)
    ): List<Pair<LocalTime, LocalTime>> {

        val allSchedules = scheduleDao.getAllSchedulesForAlgorithm()

        val scheduledTasks = allSchedules
            .filter { schedule ->
                ScheduleRecurrenceCalculator.isScheduleOccurringOnDate(schedule, targetDate) &&
                        schedule.type == ScheduleType.SCHEDULED &&
                        schedule.isActive &&
                        schedule.startTime != null &&
                        schedule.endTime != null
            }
            .sortedBy { it.startTime }

        val freeSlots = mutableListOf<Pair<LocalTime, LocalTime>>()
        var currentTime = dayStart

        for (task in scheduledTasks) {
            task.startTime?.let { startTime ->
                if (currentTime.isBefore(startTime)) {
                    freeSlots.add(Pair(currentTime, startTime))
                }
                currentTime = task.endTime ?: currentTime
            }
        }

        if (currentTime.isBefore(dayEnd)) {
            freeSlots.add(Pair(currentTime, dayEnd))
        }

        return freeSlots
    }

    suspend fun getEstimatedSchedulesForDate(date: LocalDate): List<ScheduleWithPriority> {
        // Get all estimated schedules
        val allSchedules = scheduleDao.getAllSchedulesForAlgorithm()

        // Filter for estimated schedules occurring on this date
        val estimatedSchedules = allSchedules
            .filter { schedule ->
                ScheduleRecurrenceCalculator.isScheduleOccurringOnDate(schedule, date) &&
                        schedule.type == ScheduleType.ESTIMATED &&
                        schedule.isActive &&
                        schedule.estimatedMinutes != null
            }

        if (estimatedSchedules.isNotEmpty()) {
            println("📊 پیدا شد ${estimatedSchedules.size} زمان‌بندی تخمینی برای تاریخ $date")
            estimatedSchedules.forEach {
                println("   - ${it.title} (${it.estimatedMinutes} دقیقه)")
            }
        } else {
            println("⚠️ هیچ زمان‌بندی تخمینی برای تاریخ $date پیدا نشد")
        }

        val schedulesWithPriority = mutableListOf<ScheduleWithPriority>()

        for (schedule in estimatedSchedules) {
            val relatedTasks = taskScheduleDao.getTasksForSchedule(schedule.id)
            var minPriority = 4

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

        return schedulesWithPriority.sortedBy { it.priority }
    }

    suspend fun runSchedulingAlgorithm(
        targetDate: LocalDate,
        dayStart: LocalTime = LocalTime.of(0, 0),
        dayEnd: LocalTime = LocalTime.of(23, 59)
    ): AlgorithmResult {

        val freeSlots = calculateFreeSlots(targetDate, dayStart, dayEnd)

        val estimatedSchedulesWithPriority = getEstimatedSchedulesForDate(targetDate)

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

                    val scheduledEndTime = slotStart.plusMinutes(requiredMinutes)

                    val convertedSchedule = estimatedSchedule.copy(
                        type = ScheduleType.SCHEDULED,
                        scheduleDate = targetDate,
                        startTime = slotStart,
                        endTime = scheduledEndTime,
                        estimatedMinutes = null
                    )

                    scheduleDao.updateSchedule(convertedSchedule)
                    convertedSchedules.add(convertedSchedule)

                    remainingFreeSlots[i] = Pair(scheduledEndTime, slotEnd)
                    scheduled = true
                    break
                }
            }

            if (!scheduled) {
                failedSchedules.add(estimatedSchedule)
            }
        }

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

    suspend fun getAllSchedulesForDate(date: LocalDate): List<Schedule> {
        val allSchedules = scheduleDao.getAllSchedulesForAlgorithm()
        return allSchedules.filter { schedule ->
            ScheduleRecurrenceCalculator.isScheduleOccurringOnDate(schedule, date)
        }
    }
}

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
    val totalFreeMinutes: Long,
    val dayStart: LocalTime,
    val dayEnd: LocalTime
)