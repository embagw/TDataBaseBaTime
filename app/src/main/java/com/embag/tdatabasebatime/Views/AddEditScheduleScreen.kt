package com.embag.tdatabasebatime.Views

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
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
import com.embag.tdatabasebatime.ViewModel.TaskViewModel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditScheduleScreen(
    viewModel: TaskViewModel,
    onBack: () -> Unit
) {
    val currentSchedule by viewModel.currentSchedule.collectAsState()
    val isEditMode = currentSchedule != null

    // State variables
    var startDate by remember { mutableStateOf(currentSchedule?.startTime?.toLocalDate() ?: LocalDate.now()) }
    var startTime by remember { mutableStateOf(currentSchedule?.startTime?.toLocalTime() ?: LocalTime.now()) }
    var endDate by remember { mutableStateOf(currentSchedule?.endTime?.toLocalDate() ?: LocalDate.now()) }
    var endTime by remember { mutableStateOf(currentSchedule?.endTime?.toLocalTime() ?: LocalTime.now().plusHours(1)) }
    var durationMinutes by remember { mutableStateOf(currentSchedule?.durationMinutes?.toString() ?: "60") }
    var recurrencePattern by remember { mutableStateOf(currentSchedule?.recurrencePattern ?: "") }
    var isActive by remember { mutableStateOf(currentSchedule?.isActive ?: true) }

    // State برای مدیریت DatePicker و TimePicker
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }

    // فرمت‌های نمایش
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd")
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    // نمایش DatePicker و TimePicker ها
    if (showStartDatePicker) {
        DatePickerDialog(
            onDateSelected = { date ->
                startDate = date
            },
            onDismiss = { showStartDatePicker = false },
            initialDate = startDate
        )
    }

    if (showStartTimePicker) {
        TimePickerDialog(
            onTimeSelected = { time ->
                startTime = time
            },
            onDismiss = { showStartTimePicker = false },
            initialTime = startTime
        )
    }

    if (showEndDatePicker) {
        DatePickerDialog(
            onDateSelected = { date ->
                endDate = date
            },
            onDismiss = { showEndDatePicker = false },
            initialDate = endDate
        )
    }

    if (showEndTimePicker) {
        TimePickerDialog(
            onTimeSelected = { time ->
                endTime = time
            },
            onDismiss = { showEndTimePicker = false },
            initialTime = endTime
        )
    }

    Scaffold (
        topBar = {
            TopAppBar(
                title = { Text(if (isEditMode) "ویرایش زمان‌بندی" else "ایجاد زمان‌بندی جدید") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // زمان شروع
            Text(
                text = "زمان شروع:",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Row (
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 4.dp)
                    ) {
                        Icon(Icons.Default.DateRange, contentDescription = "Date", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = startDate.format(dateFormatter), style = MaterialTheme.typography.bodyMedium)
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Build, contentDescription = "Time", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = startTime.format(timeFormatter), style = MaterialTheme.typography.bodyMedium)
                    }
                }
                Row {
                    OutlinedButton (
                        onClick = { showStartDatePicker = true },
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text("تاریخ شروع")
                    }
                    OutlinedButton(
                        onClick = { showStartTimePicker = true }
                    ) {
                        Text("زمان شروع")
                    }
                }
            }

            // زمان پایان
            Text(
                text = "زمان پایان:",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 4.dp)
                    ) {
                        Icon(Icons.Default.DateRange, contentDescription = "Date", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = endDate.format(dateFormatter), style = MaterialTheme.typography.bodyMedium)
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Build, contentDescription = "Time", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = endTime.format(timeFormatter), style = MaterialTheme.typography.bodyMedium)
                    }
                }
                Row {
                    OutlinedButton(
                        onClick = { showEndDatePicker = true },
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text("تاریخ پایان")
                    }
                    OutlinedButton(
                        onClick = { showEndTimePicker = true }
                    ) {
                        Text("زمان پایان")
                    }
                }
            }

            // مدت زمان
            OutlinedTextField(
                value = durationMinutes,
                onValueChange = {
                    if (it.all { char -> char.isDigit() } && it.isNotEmpty()) {
                        durationMinutes = it
                    } else if (it.isEmpty()) {
                        durationMinutes = ""
                    }
                },
                label = { Text("مدت زمان (دقیقه)") },
                modifier = Modifier.fillMaxWidth()
            )

            // الگوی تکرار
            OutlinedTextField(
                value = recurrencePattern,
                onValueChange = { recurrencePattern = it },
                label = { Text("الگوی تکرار (اختیاری)") },
                modifier = Modifier.fillMaxWidth()
            )

            // فعال/غیرفعال
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = isActive,
                    onCheckedChange = { isActive = it }
                )
                Text(text = "فعال")
            }

            // دکمه ذخیره
            Button(
                onClick = {
                    val startDateTime = LocalDateTime.of(startDate, startTime)
                    val endDateTime = LocalDateTime.of(endDate, endTime)
                    val duration = durationMinutes.toLongOrNull() ?: 60

                    if (isEditMode) {
                        val schedule = Schedule(
                            id = currentSchedule!!.id,
                            startTime = startDateTime,
                            endTime = endDateTime,
                            durationMinutes = duration,
                            recurrencePattern = recurrencePattern.ifBlank { null },
                            isActive = isActive
                        )
                        viewModel.updateSchedule(schedule)
                    } else {
                        viewModel.createSchedule(
                            startTime = startDateTime,
                            endTime = endDateTime,
                            durationMinutes = duration,
                            recurrencePattern = recurrencePattern.ifBlank { null }
                        )
                    }
                    onBack()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = durationMinutes.isNotEmpty() && durationMinutes.toLongOrNull() != null
            ) {
                Text(if (isEditMode) "ذخیره تغییرات" else "ایجاد زمان‌بندی")
            }
        }
    }
}