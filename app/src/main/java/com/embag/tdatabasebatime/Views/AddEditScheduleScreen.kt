package com.embag.tdatabasebatime.Views

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.embag.tdatabasebatime.Model.Entity.ScheduleType
import com.embag.tdatabasebatime.Model.Entity.Task
import com.embag.tdatabasebatime.ViewModel.TaskViewModel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditScheduleScreen(
    viewModel: TaskViewModel,
    onBack: () -> Unit,
    onLinkTasks: () -> Unit
) {
    val currentSchedule by viewModel.currentSchedule.collectAsState()
    val isEditMode = currentSchedule != null
    val tasksForCurrentSchedule by viewModel.tasksForCurrentSchedule.collectAsState()

    // State variables
    var scheduleType by remember {
        mutableStateOf(currentSchedule?.type ?: ScheduleType.SCHEDULED)
    }
    var title by remember { mutableStateOf(currentSchedule?.title ?: "") }
    var description by remember { mutableStateOf(currentSchedule?.description ?: "") }

    // State برای انواع مختلف زمان‌بندی
    var scheduledDateTime by remember {
        mutableStateOf(currentSchedule?.scheduledDateTime ?: LocalDateTime.now().plusHours(1))
    }
    var estimatedMinutes by remember {
        mutableStateOf(currentSchedule?.estimatedMinutes?.toString() ?: "60")
    }
    var count by remember {
        mutableStateOf(currentSchedule?.count?.toString() ?: "5")
    }
    var eventDate by remember {
        mutableStateOf(currentSchedule?.eventDate ?: LocalDate.now().plusDays(1))
    }

    // State برای مدیریت DatePicker و TimePicker
    var showDateTimePicker by remember { mutableStateOf(false) }
    var showEventDatePicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditMode) "ویرایش زمان‌بندی" else "ایجاد زمان‌بندی جدید") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // بخش اصلی زمان‌بندی
            Card(
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // عنوان
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("عنوان *") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = title.isEmpty()
                    )

                    // توضیحات
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("توضیحات") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp),
                        maxLines = 3
                    )

                    // نوع زمان‌بندی
                    var expanded by remember { mutableStateOf(false) }
                    Box(modifier = Modifier.fillMaxWidth()) {
                        ExposedDropdownMenuBox (
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded }
                        ) {
                            OutlinedTextField(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),
                                readOnly = true,
                                value = viewModel.getScheduleTypeText(scheduleType),
                                onValueChange = {},
                                label = { Text("نوع زمان‌بندی *") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = when (scheduleType) {
                                            ScheduleType.SCHEDULED -> Icons.Default.Schedule
                                            ScheduleType.ESTIMATED -> Icons.Default.Timer
                                            ScheduleType.COUNT -> Icons.Default.Repeat
                                            ScheduleType.EVENT -> Icons.Default.Event
                                        },
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp)
                                    )
                                },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                                }
                            )
                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                DropdownMenuItem(
                                    text = {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                Icons.Default.Schedule,
                                                contentDescription = null,
                                                modifier = Modifier.padding(end = 8.dp)
                                            )
                                            Text(viewModel.getScheduleTypeText(ScheduleType.SCHEDULED))
                                        }
                                    },
                                    onClick = {
                                        scheduleType = ScheduleType.SCHEDULED
                                        expanded = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                Icons.Default.Timer,
                                                contentDescription = null,
                                                modifier = Modifier.padding(end = 8.dp)
                                            )
                                            Text(viewModel.getScheduleTypeText(ScheduleType.ESTIMATED))
                                        }
                                    },
                                    onClick = {
                                        scheduleType = ScheduleType.ESTIMATED
                                        expanded = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                Icons.Default.Repeat,
                                                contentDescription = null,
                                                modifier = Modifier.padding(end = 8.dp)
                                            )
                                            Text(viewModel.getScheduleTypeText(ScheduleType.COUNT))
                                        }
                                    },
                                    onClick = {
                                        scheduleType = ScheduleType.COUNT
                                        expanded = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                Icons.Default.Event,
                                                contentDescription = null,
                                                modifier = Modifier.padding(end = 8.dp)
                                            )
                                            Text(viewModel.getScheduleTypeText(ScheduleType.EVENT))
                                        }
                                    },
                                    onClick = {
                                        scheduleType = ScheduleType.EVENT
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    // نمایش بخش‌های مختلف بر اساس نوع زمان‌بندی
                    when (scheduleType) {
                        ScheduleType.SCHEDULED -> {
                            Text(
                                text = "زمان برنامه‌ریزی شده:",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = scheduledDateTime.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm")),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }

                                Button(
                                    onClick = { showDateTimePicker = true }
                                ) {
                                    Text("انتخاب زمان")
                                }
                            }
                        }

                        ScheduleType.ESTIMATED -> {
                            OutlinedTextField(
                                value = estimatedMinutes,
                                onValueChange = {
                                    if (it.all { char -> char.isDigit() } && it.isNotEmpty()) {
                                        estimatedMinutes = it
                                    } else if (it.isEmpty()) {
                                        estimatedMinutes = ""
                                    }
                                },
                                label = { Text("مدت زمان تخمینی (دقیقه) *") },
                                modifier = Modifier.fillMaxWidth(),
                                isError = estimatedMinutes.isEmpty() || estimatedMinutes.toLongOrNull() == null,
                                leadingIcon = {
                                    Icon(Icons.Default.Timer, contentDescription = null)
                                }
                            )
                        }

                        ScheduleType.COUNT -> {
                            OutlinedTextField(
                                value = count,
                                onValueChange = {
                                    if (it.all { char -> char.isDigit() } && it.isNotEmpty()) {
                                        count = it
                                    } else if (it.isEmpty()) {
                                        count = ""
                                    }
                                },
                                label = { Text("تعداد دفعات *") },
                                modifier = Modifier.fillMaxWidth(),
                                isError = count.isEmpty() || count.toIntOrNull() == null,
                                leadingIcon = {
                                    Icon(Icons.Default.Repeat, contentDescription = null)
                                }
                            )
                        }

                        ScheduleType.EVENT -> {
                            Text(
                                text = "تاریخ رویداد:",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = eventDate.format(DateTimeFormatter.ofPattern("yyyy/MM/dd")),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }

                                Button(
                                    onClick = { showEventDatePicker = true }
                                ) {
                                    Text("انتخاب تاریخ")
                                }
                            }
                        }
                    }
                }
            }

            // نمایش DatePicker برای زمان برنامه‌ریزی شده
            if (showDateTimePicker) {
                DateTimePickerDialog(
                    initialDateTime = scheduledDateTime,
                    onDateTimeSelected = { dateTime ->
                        scheduledDateTime = dateTime
                        showDateTimePicker = false
                    },
                    onDismiss = { showDateTimePicker = false }
                )
            }

            // نمایش DatePicker برای رویداد
            if (showEventDatePicker) {
                DatePickerDialog(
                    onDateSelected = { date ->
                        eventDate = date
                        showEventDatePicker = false
                    },
                    onDismiss = { showEventDatePicker = false },
                    initialDate = eventDate
                )
            }

            // بخش اتصال به تسک‌ها
            Card(
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "اتصال به تسک‌ها",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )

                        Button(
                            onClick = onLinkTasks,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        ) {
                            Text("مدیریت اتصال")
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (tasksForCurrentSchedule.isEmpty()) {
                        Text(
                            text = "هیچ تسکی متصل نیست",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            tasksForCurrentSchedule.forEach { task ->
                                TaskConnectionItem(task = task, viewModel = viewModel)
                            }
                        }
                    }
                }
            }

            // دکمه ذخیره
            Button(
                onClick = {
                    when (scheduleType) {
                        ScheduleType.SCHEDULED -> {
                            if (title.isNotEmpty()) {
                                if (isEditMode) {
                                    viewModel.updateSchedule(
                                        Schedule(
                                            id = currentSchedule!!.id,
                                            type = scheduleType,
                                            title = title,
                                            description = description.ifBlank { null },
                                            scheduledDateTime = scheduledDateTime,
                                            isActive = currentSchedule?.isActive ?: true
                                        )
                                    )
                                } else {
                                    viewModel.createSchedule(
                                        type = scheduleType,
                                        title = title,
                                        description = description.ifBlank { null },
                                        scheduledDateTime = scheduledDateTime
                                    )
                                }
                                onBack()
                            }
                        }
                        ScheduleType.ESTIMATED -> {
                            if (title.isNotEmpty() && estimatedMinutes.isNotEmpty() && estimatedMinutes.toLongOrNull() != null) {
                                if (isEditMode) {
                                    viewModel.updateSchedule(
                                        Schedule(
                                            id = currentSchedule!!.id,
                                            type = scheduleType,
                                            title = title,
                                            description = description.ifBlank { null },
                                            estimatedMinutes = estimatedMinutes.toLong(),
                                            isActive = currentSchedule?.isActive ?: true
                                        )
                                    )
                                } else {
                                    viewModel.createSchedule(
                                        type = scheduleType,
                                        title = title,
                                        description = description.ifBlank { null },
                                        estimatedMinutes = estimatedMinutes.toLong()
                                    )
                                }
                                onBack()
                            }
                        }
                        ScheduleType.COUNT -> {
                            if (title.isNotEmpty() && count.isNotEmpty() && count.toIntOrNull() != null) {
                                if (isEditMode) {
                                    viewModel.updateSchedule(
                                        Schedule(
                                            id = currentSchedule!!.id,
                                            type = scheduleType,
                                            title = title,
                                            description = description.ifBlank { null },
                                            count = count.toInt(),
                                            currentCount = currentSchedule?.currentCount ?: 0,
                                            isActive = currentSchedule?.isActive ?: true
                                        )
                                    )
                                } else {
                                    viewModel.createSchedule(
                                        type = scheduleType,
                                        title = title,
                                        description = description.ifBlank { null },
                                        count = count.toInt()
                                    )
                                }
                                onBack()
                            }
                        }
                        ScheduleType.EVENT -> {
                            if (title.isNotEmpty()) {
                                if (isEditMode) {
                                    viewModel.updateSchedule(
                                        Schedule(
                                            id = currentSchedule!!.id,
                                            type = scheduleType,
                                            title = title,
                                            description = description.ifBlank { null },
                                            eventDate = eventDate,
                                            isActive = currentSchedule?.isActive ?: true
                                        )
                                    )
                                } else {
                                    viewModel.createSchedule(
                                        type = scheduleType,
                                        title = title,
                                        description = description.ifBlank { null },
                                        eventDate = eventDate
                                    )
                                }
                                onBack()
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = when (scheduleType) {
                    ScheduleType.SCHEDULED -> title.isNotEmpty()
                    ScheduleType.ESTIMATED -> title.isNotEmpty() && estimatedMinutes.isNotEmpty() && estimatedMinutes.toLongOrNull() != null
                    ScheduleType.COUNT -> title.isNotEmpty() && count.isNotEmpty() && count.toIntOrNull() != null
                    ScheduleType.EVENT -> title.isNotEmpty()
                }
            ) {
                Text(if (isEditMode) "ذخیره تغییرات" else "ایجاد زمان‌بندی")
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TaskConnectionItem(
    task: Task,
    viewModel: TaskViewModel
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.TaskAlt,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "اولویت: ${viewModel.getPriorityText(task.priority)} - مهلت: ${viewModel.formatDueDate(task.dueDate)}",
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateTimePickerDialog(
    initialDateTime: LocalDateTime,
    onDateTimeSelected: (LocalDateTime) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedDate by remember { mutableStateOf(initialDateTime.toLocalDate()) }
    var selectedTime by remember { mutableStateOf(initialDateTime.toLocalTime()) }
    var showDatePicker by remember { mutableStateOf(true) }

    if (showDatePicker) {
        DatePickerDialog(
            onDateSelected = { date ->
                selectedDate = date
                showDatePicker = false
            },
            onDismiss = onDismiss,
            initialDate = selectedDate
        )
    } else {
        TimePickerDialog(
            onTimeSelected = { time ->
                selectedTime = time
                val finalDateTime = LocalDateTime.of(selectedDate, selectedTime)
                onDateTimeSelected(finalDateTime)
            },
            onDismiss = onDismiss,
            initialTime = selectedTime
        )
    }
}