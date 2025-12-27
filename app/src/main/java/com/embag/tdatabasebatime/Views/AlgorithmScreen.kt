package com.embag.tdatabasebatime.Views

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.embag.tdatabasebatime.Model.Entity.Schedule
import com.embag.tdatabasebatime.Repository.AlgorithmResult
import com.embag.tdatabasebatime.Repository.ScheduleWithPriority
import com.embag.tdatabasebatime.ViewModel.AlgorithmViewModel
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlgorithmScreen(
    viewModel: AlgorithmViewModel,
    onBack: () -> Unit
) {
    val selectedDate by viewModel.selectedDate.collectAsState()
    val freeSlots by viewModel.freeSlots.collectAsState()
    val estimatedSchedules by viewModel.estimatedSchedules.collectAsState()
    val algorithmResult by viewModel.algorithmResult.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var showDatePicker by remember { mutableStateOf(false) }


    // 🆕 دیباگ دکمه برای بررسی داده‌ها
    var showDebugInfo by remember { mutableStateOf(false) }


    LaunchedEffect (Unit) {
        viewModel.setSelectedDate(LocalDate.now())
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("الگوریتم برنامه‌ریزی") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "بازگشت")
                    }
                },
                actions = {
                    // دکمه دیباگ
                    IconButton(onClick = { showDebugInfo = true }) {
                        Icon(Icons.Default.BugReport, contentDescription = "دیباگ")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // انتخاب تاریخ
            Card (elevation = CardDefaults.cardElevation(4.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "انتخاب تاریخ",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row (
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = selectedDate.format(DateTimeFormatter.ofPattern("yyyy/MM/dd")),
                            style = MaterialTheme.typography.bodyLarge
                        )

                        Button(onClick = { showDatePicker = true }) {
                            Text("تغییر تاریخ")
                        }
                    }
                }
            }


            if (showDebugInfo) {
                AlertDialog(
                    onDismissRequest = { showDebugInfo = false },
                    title = { Text("اطلاعات دیباگ") },
                    text = {
                        Column {
                            Text("تاریخ انتخابی: $selectedDate")
                            Text("تعداد زمان‌های خالی: ${freeSlots.size}")
                            Text("تعداد زمان‌بندی‌های تخمینی: ${estimatedSchedules.size}")

                            Spacer(modifier = Modifier.height(8.dp))

                            Text("زمان‌های خالی:", fontWeight = FontWeight.Bold)
                            freeSlots.forEach { slot ->
                                Text("  ${slot.first} - ${slot.second}")
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text("زمان‌بندی‌های تخمینی:", fontWeight = FontWeight.Bold)
                            estimatedSchedules.forEach { scheduleWithPriority ->
                                val schedule = scheduleWithPriority.schedule
                                Text("  ${schedule.title} (${schedule.estimatedMinutes} دقیقه)")
                            }
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = { showDebugInfo = false }) {
                            Text("باشه")
                        }
                    }
                )
            }


            // زمان‌های خالی
            Card(elevation = CardDefaults.cardElevation(4.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "زمان‌های خالی روز",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    if (freeSlots.isEmpty()) {
                        Text(
                            text = "هیچ زمان خالی در این روز وجود ندارد",
                            color = MaterialTheme.colorScheme.error
                        )
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            freeSlots.forEach { slot ->
                                FreeSlotItem(slot = slot)
                            }
                        }
                    }
                }
            }

            // زمان‌بندی‌های ESTIMATED
            Card(elevation = CardDefaults.cardElevation(4.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "زمان‌بندی‌های تخمینی",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    if (estimatedSchedules.isEmpty()) {
                        Text(
                            text = "هیچ زمان‌بندی تخمینی برای این تاریخ وجود ندارد"
                        )
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            estimatedSchedules.forEach { scheduleWithPriority ->
                                EstimatedScheduleItem(scheduleWithPriority = scheduleWithPriority)
                            }
                        }
                    }
                }
            }

            // دکمه اجرای الگوریتم
            Button(
                onClick = { viewModel.runAlgorithm() },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading && estimatedSchedules.isNotEmpty() && freeSlots.isNotEmpty()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("در حال پردازش...")
                } else {
                    Text("اجرای الگوریتم برنامه‌ریزی")
                }
            }

            // نمایش نتایج
            algorithmResult?.let { result ->
                ResultCard(result = result)
            }
        }
    }

    // Date Picker
    if (showDatePicker) {
        DatePickerDialog(
            onDateSelected = { date ->
                viewModel.setSelectedDate(date)
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false },
            initialDate = selectedDate
        )
    }
    if (showDebugInfo) {
        AlertDialog(
            onDismissRequest = { showDebugInfo = false },
            title = { Text("اطلاعات دیباگ") },
            text = {
                Column {
                    Text("تاریخ انتخابی: $selectedDate")
                    Text("تعداد زمان‌های خالی: ${freeSlots.size}")
                    Text("تعداد زمان‌بندی‌های تخمینی: ${estimatedSchedules.size}")

                    Spacer(modifier = Modifier.height(8.dp))

                    Text("زمان‌های خالی:", fontWeight = FontWeight.Bold)
                    freeSlots.forEach { slot ->
                        Text("  ${slot.first} - ${slot.second}")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text("زمان‌بندی‌های تخمینی:", fontWeight = FontWeight.Bold)
                    estimatedSchedules.forEach { scheduleWithPriority ->
                        val schedule = scheduleWithPriority.schedule
                        Text("  ${schedule.title} (${schedule.estimatedMinutes} دقیقه)")
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showDebugInfo = false }) {
                    Text("باشه")
                }
            }
        )
    }




}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FreeSlotItem(slot: Pair<LocalTime, LocalTime>) {
    val duration = ChronoUnit.MINUTES.between(slot.first, slot.second)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Schedule,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${slot.first.format(DateTimeFormatter.ofPattern("HH:mm"))} - ${slot.second.format(DateTimeFormatter.ofPattern("HH:mm"))}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "$duration دقیقه",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun EstimatedScheduleItem(scheduleWithPriority: ScheduleWithPriority) {
    val schedule = scheduleWithPriority.schedule

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Timer,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = schedule.title,
                    style = MaterialTheme.typography.bodyMedium
                )
                schedule.estimatedMinutes?.let { minutes ->
                    Text(
                        text = "مدت تخمینی: $minutes دقیقه (اولویت: ${scheduleWithPriority.priority})",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ResultCard(result: AlgorithmResult) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "نتایج الگوریتم",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(12.dp))

            // آمار
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = result.totalConverted.toString(),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "تبدیل شده",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = result.totalFailed.toString(),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                    Text(
                        text = "ناموفق",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // زمان‌بندی‌های تبدیل شده
            if (result.convertedSchedules.isNotEmpty()) {
                Text(
                    text = "تبدیل شده‌ها:",
                    style = MaterialTheme.typography.titleSmall
                )

                Spacer(modifier = Modifier.height(8.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    result.convertedSchedules.forEach { schedule ->
                        ConvertedScheduleItem(schedule = schedule)
                    }
                }
            }

            // زمان‌بندی‌های ناموفق
            if (result.failedSchedules.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "تبدیل نشده‌ها (زمان کافی نبود):",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.error
                )

                Spacer(modifier = Modifier.height(8.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    result.failedSchedules.forEach { schedule ->
                        FailedScheduleItem(schedule = schedule)
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ConvertedScheduleItem(schedule: Schedule) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Default.CheckCircle,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = schedule.title)
            schedule.startTime?.let { startTime ->
                schedule.endTime?.let { endTime ->
                    Text(
                        text = "${startTime.format(DateTimeFormatter.ofPattern("HH:mm"))} - ${endTime.format(DateTimeFormatter.ofPattern("HH:mm"))}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
fun FailedScheduleItem(schedule: Schedule) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Default.Warning,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = schedule.title,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.width(8.dp))
        schedule.estimatedMinutes?.let { minutes ->
            Text(
                text = "($minutes دقیقه)",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}