package com.embag.tdatabasebatime.Views

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.test.services.storage.file.PropertyFile.Column
import com.embag.tdatabasebatime.Model.Entity.Schedule
import com.embag.tdatabasebatime.Model.Entity.ScheduleType
import com.embag.tdatabasebatime.Model.Entity.TaskStatus
import com.embag.tdatabasebatime.ViewModel.TaskViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTaskScreen(
    viewModel: TaskViewModel,
    onBack: () -> Unit,
    onManageCategories: () -> Unit,
    onLinkSchedules: () -> Unit
) {
    val currentTask by viewModel.currentTask.collectAsState()
    val isEditMode = currentTask != null
    val categories by viewModel.categories.collectAsState()
    val schedulesForCurrentTask by viewModel.schedulesForCurrentTask.collectAsState()

    // State variables
    var selectedCategoryId by remember {
        mutableStateOf(currentTask?.categoryId ?: categories.firstOrNull { it.name == "اصلی" }?.id)
    }
    var title by remember { mutableStateOf(currentTask?.title ?: "") }
    var description by remember { mutableStateOf(currentTask?.description ?: "") }
    var priority by remember { mutableStateOf(currentTask?.priority?.toString() ?: "4") }
    var status by remember { mutableStateOf(currentTask?.status ?: TaskStatus.NEEDS_DOING) }

    // State برای مدیریت تاریخ و زمان (nullable)
    var hasDueDate by remember { mutableStateOf(currentTask?.dueDate != null) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    // State برای تاریخ و زمان
    val initialDueDate = currentTask?.dueDate ?: LocalDateTime.now().plusDays(7)
    var selectedDate by remember { mutableStateOf(initialDueDate.toLocalDate()) }
    var selectedTime by remember { mutableStateOf(initialDueDate.toLocalTime()) }

    // فرمت‌های نمایش
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd")
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    // نمایش DatePicker Dialog
    if (showDatePicker) {
        DatePickerDialog(
            onDateSelected = { date ->
                selectedDate = date
            },
            onDismiss = { showDatePicker = false },
            initialDate = selectedDate
        )
    }

    // نمایش TimePicker Dialog
    if (showTimePicker) {
        TimePickerDialog(
            onTimeSelected = { time ->
                selectedTime = time
            },
            onDismiss = { showTimePicker = false },
            initialTime = selectedTime
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditMode) "ویرایش تسک" else "ایجاد تسک جدید") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
            // بخش اصلی تسک
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

                    // دسته‌بندی
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        var expanded by remember { mutableStateOf(false) }

                        Box(modifier = Modifier.weight(1f)) {
                            ExposedDropdownMenuBox(
                                expanded = expanded,
                                onExpandedChange = { expanded = !expanded }
                            ) {
                                OutlinedTextField(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor(),
                                    readOnly = true,
                                    value = categories.find { it.id == selectedCategoryId }?.name ?: "بدون دسته‌بندی",
                                    onValueChange = {},
                                    label = { Text("دسته‌بندی") },
                                    leadingIcon = {
                                        if (selectedCategoryId != null) {
                                            Box(
                                                modifier = Modifier
                                                    .size(20.dp)
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(Color(android.graphics.Color.parseColor(
                                                        viewModel.getCategoryColor(selectedCategoryId)
                                                    )))
                                            )
                                        }
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
                                        text = { Text("بدون دسته‌بندی") },
                                        onClick = {
                                            selectedCategoryId = null
                                            expanded = false
                                        }
                                    )
                                    categories.forEach { category ->
                                        DropdownMenuItem(
                                            text = {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(20.dp)
                                                            .clip(RoundedCornerShape(4.dp))
                                                            .background(Color(android.graphics.Color.parseColor(category.color ?: "#9E9E9E")))
                                                            .padding(end = 8.dp)
                                                    )
                                                    Text(category.name)
                                                }
                                            },
                                            onClick = {
                                                selectedCategoryId = category.id
                                                expanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        IconButton(onClick = onManageCategories) {
                            Icon(Icons.Default.Settings, contentDescription = "Manage Categories")
                        }
                    }

                    // توضیحات
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("توضیحات") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        maxLines = 5
                    )

                    // اولویت
                    OutlinedTextField(
                        value = priority,
                        onValueChange = {
                            if (it.all { char -> char.isDigit() } && it.isNotEmpty()) {
                                val value = it.toInt()
                                if (value in 1..4) {
                                    priority = it
                                }
                            } else if (it.isEmpty()) {
                                priority = ""
                            }
                        },
                        label = { Text("اولویت (1-4) *") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = priority.isEmpty() || priority.toIntOrNull() !in 1..4
                    )

                    // بخش انتخاب تاریخ و زمان
                    Card(
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "تاریخ مهلت (اختیاری):",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )

                                Switch(
                                    checked = hasDueDate,
                                    onCheckedChange = { hasDueDate = it }
                                )
                            }

                            if (hasDueDate) {
                                // انتخاب تاریخ و زمان
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    // نمایش تاریخ و زمان انتخاب شده
                                    Column {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(bottom = 4.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.DateRange,
                                                contentDescription = "Date",
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = selectedDate.format(dateFormatter),
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                        }

                                        Row(
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                Icons.Default.Build,
                                                contentDescription = "Time",
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = selectedTime.format(timeFormatter),
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                        }
                                    }

                                    // دکمه‌های انتخاب تاریخ و زمان
                                    Row {
                                        OutlinedButton(
                                            onClick = { showDatePicker = true },
                                            modifier = Modifier.padding(end = 8.dp)
                                        ) {
                                            Text("تاریخ")
                                        }

                                        OutlinedButton(
                                            onClick = { showTimePicker = true }
                                        ) {
                                            Text("زمان")
                                        }
                                    }
                                }

                                // نمایش تاریخ و زمان ترکیبی
                                Text(
                                    text = "مهلت نهایی: ${LocalDateTime.of(selectedDate, selectedTime).format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"))}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            } else {
                                Text(
                                    text = "تاریخ مهلت تنظیم نشده است",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontStyle = FontStyle.Italic
                                )
                            }
                        }
                    }

                    // وضعیت (در حالت ویرایش)
                    if (isEditMode) {
                        var statusExpanded by remember { mutableStateOf(false) }
                        val statusOptions = listOf(
                            "نیاز به انجام" to TaskStatus.NEEDS_DOING,
                            "انجام شده" to TaskStatus.DONE,
                            "لغو شده" to TaskStatus.CANCELLED
                        )
                        val currentStatusText = when (status) {
                            TaskStatus.NEEDS_DOING -> "نیاز به انجام"
                            TaskStatus.DONE -> "انجام شده"
                            TaskStatus.CANCELLED -> "لغو شده"
                        }

                        Box(modifier = Modifier.fillMaxWidth()) {
                            ExposedDropdownMenuBox(
                                expanded = statusExpanded,
                                onExpandedChange = { statusExpanded = !statusExpanded }
                            ) {
                                OutlinedTextField(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor(),
                                    readOnly = true,
                                    value = currentStatusText,
                                    onValueChange = {},
                                    label = { Text("وضعیت") },
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusExpanded)
                                    }
                                )
                                ExposedDropdownMenu(
                                    expanded = statusExpanded,
                                    onDismissRequest = { statusExpanded = false }
                                ) {
                                    statusOptions.forEach { (text, taskStatus) ->
                                        DropdownMenuItem(
                                            text = { Text(text) },
                                            onClick = {
                                                status = taskStatus
                                                statusExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // بخش اتصال به زمان‌بندی‌ها
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
                            text = "اتصال به زمان‌بندی‌ها",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )

                        Button(
                            onClick = onLinkSchedules,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        ) {
                            Text("مدیریت اتصال")
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (schedulesForCurrentTask.isEmpty()) {
                        Text(
                            text = "هیچ زمان‌بندی‌ای متصل نیست",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            schedulesForCurrentTask.forEach { schedule ->
                                ScheduleConnectionItem(schedule = schedule, viewModel = viewModel)
                            }
                        }
                    }
                }
            }


            // دکمه ذخیره
            Button(
                onClick = {
                    if (title.isNotEmpty() && priority.toIntOrNull() in 1..4) {
                        // تاریخ مهلت فقط اگر کاربر انتخاب کرده باشد
                        val finalDueDate = if (hasDueDate) {
                            LocalDateTime.of(selectedDate, selectedTime)
                        } else {
                            null
                        }

                        if (isEditMode) {
                            viewModel.updateTask(
                                id = currentTask!!.id,
                                categoryId = selectedCategoryId,
                                title = title,
                                description = description.ifBlank { null },
                                priority = priority.toInt(),
                                dueDate = finalDueDate,
                                status = status
                            )
                        } else {
                            viewModel.createTask(
                                categoryId = selectedCategoryId,
                                title = title,
                                description = description.ifBlank { null },
                                priority = priority.toInt(),
                                dueDate = finalDueDate,
                                status = TaskStatus.NEEDS_DOING
                            )
                        }
                        onBack()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = title.isNotEmpty() && priority.toIntOrNull() in 1..4
            ) {
                Text(if (isEditMode) "ذخیره تغییرات" else "ایجاد تسک")
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ScheduleConnectionItem(
    schedule: Schedule,
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
                imageVector = when (schedule.type) {
                    ScheduleType.SCHEDULED -> Icons.Default.Schedule
                    ScheduleType.ESTIMATED -> Icons.Default.Timer
                    ScheduleType.COUNT -> Icons.Default.Repeat
                    ScheduleType.EVENT -> Icons.Default.Event
                },
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = schedule.title,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${viewModel.getScheduleTypeText(schedule.type)}: ${viewModel.getScheduleDetails(schedule)}",
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}