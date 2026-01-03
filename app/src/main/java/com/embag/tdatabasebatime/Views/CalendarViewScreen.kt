package com.embag.tdatabasebatime.Views

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import com.embag.tdatabasebatime.Model.Entity.RepeatType
import com.embag.tdatabasebatime.Model.Entity.Schedule
import com.embag.tdatabasebatime.ViewModel.TaskViewModel
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@SuppressLint("RememberReturnType")
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarViewScreen(
    viewModel: TaskViewModel,
    onBack: () -> Unit
) {
    val currentDate = remember { mutableStateOf(LocalDate.now()) }
    val schedulesForMonth by produceState (emptyList<Schedule>()) {
        viewModel.viewModelScope.launch {
            // اینجا باید تابعی ایجاد کنیم که تمام زمان‌بندی‌های یک ماه را برگرداند
            // فعلاً از تابع موجود استفاده می‌کنیم
            val allSchedules = viewModel.getAllSchedulesForDebug()
            value = allSchedules
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("نمای تقویم") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "بازگشت")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Header with month navigation
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    currentDate.value = currentDate.value.minusMonths(1)
                }) {
                    Icon(Icons.Default.ChevronLeft, contentDescription = "ماه قبل")
                }

                Text(
                    text = currentDate.value.format(DateTimeFormatter.ofPattern("MMMM yyyy")),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                IconButton(onClick = {
                    currentDate.value = currentDate.value.plusMonths(1)
                }) {
                    Icon(Icons.Default.ChevronRight, contentDescription = "ماه بعد")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Day headers
            val daysOfWeek = listOf("ش", "ی", "د", "س", "چ", "پ", "ج")
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                daysOfWeek.forEach { day ->
                    Text(
                        text = day,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Calendar grid
            val daysInMonth = currentDate.value.lengthOfMonth()
            val firstDayOfMonth = currentDate.value.withDayOfMonth(1)
            val dayOfWeek = firstDayOfMonth.dayOfWeek.value % 7 // Adjust for Saturday start

            LazyVerticalGrid (
                columns = GridCells.Fixed(7),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Empty cells for days before the first day of month
                items(dayOfWeek) {
                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .padding(2.dp)
                    )
                }

                // Days of the month
                items(daysInMonth) { day ->
                    val date = currentDate.value.withDayOfMonth(day + 1)
                    val hasSchedules = schedulesForMonth.any { schedule ->
                        isScheduleOccurringOnDate(schedule, date)
                    }

                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .padding(2.dp)
                            .background(
                                color = if (date == LocalDate.now())
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                else if (hasSchedules)
                                    MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)
                                else
                                    Color.Transparent,
                                shape = CircleShape
                            )
                            .border(
                                width = if (date == LocalDate.now()) 2.dp else 0.dp,
                                color = MaterialTheme.colorScheme.primary,
                                shape = CircleShape
                            )
                            .clickable {
                                // Navigate to day view or show details
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = (day + 1).toString(),
                                fontWeight = if (date == LocalDate.now()) FontWeight.Bold else FontWeight.Normal
                            )

                            if (hasSchedules) {
                                Spacer(modifier = Modifier.height(2.dp))
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.secondary)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Legend
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                            .border(1.dp, MaterialTheme.colorScheme.primary, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("امروز", style = MaterialTheme.typography.bodySmall)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f))
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("زمان‌بندی", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun isScheduleOccurringOnDate(schedule: Schedule, date: LocalDate): Boolean {
    // Check if schedule occurs on this date (including repeats)
    if (schedule.repeatType == RepeatType.NONE) {
        return schedule.scheduleDate == date
    }

    // If schedule hasn't started yet
    schedule.scheduleDate?.let { startDate ->
        if (date.isBefore(startDate)) return false
    }

    // If repeat has end date
    schedule.repeatEndDate?.let { endDate ->
        if (date.isAfter(endDate)) return false
    }

    // Check based on repeat type
    return when (schedule.repeatType) {
        RepeatType.DAILY -> {
            val startDate = schedule.scheduleDate ?: return false
            val daysBetween = ChronoUnit.DAYS.between(startDate, date)
            daysBetween >= 0 && daysBetween % schedule.repeatInterval == 0L
        }
        RepeatType.WEEKLY -> {
            val startDate = schedule.scheduleDate ?: return false
            val weeksBetween = ChronoUnit.WEEKS.between(startDate, date)
            weeksBetween >= 0 && weeksBetween % schedule.repeatInterval == 0L
        }
        RepeatType.MONTHLY -> {
            val startDate = schedule.scheduleDate ?: return false
            schedule.repeatDayOfMonth?.let { dayOfMonth ->
                date.dayOfMonth == dayOfMonth
            } ?: run {
                // Same day of month as start date
                startDate.dayOfMonth == date.dayOfMonth
            }
        }
        RepeatType.YEARLY -> {
            val startDate = schedule.scheduleDate ?: return false
            schedule.repeatMonthOfYear?.let { month ->
                schedule.repeatDayOfMonth?.let { day ->
                    date.monthValue == month && date.dayOfMonth == day
                }
            } ?: run {
                // Same month and day as start date
                startDate.monthValue == date.monthValue && startDate.dayOfMonth == date.dayOfMonth
            }
        }
        RepeatType.CUSTOM_DAYS -> {
            val daysOfWeek = schedule.repeatDaysOfWeek?.split(",")?.map {
                DayOfWeek.of(it.toInt())
            } ?: return false
            daysOfWeek.contains(date.dayOfWeek)
        }
        else -> false
    }
}