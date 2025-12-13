package com.embag.tdatabasebatime.View

/*
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.embag.tdatabasebatime.Model.TaskStatus
import com.embag.tdatabasebatime.ViewModel.TaskViewModel
import java.time.LocalDateTime

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTaskScreen(
    viewModel: TaskViewModel,
    onBack: () -> Unit
) {
    val currentTask by viewModel.currentTask.collectAsState()
    val isEditMode = currentTask != null

    // State variables
    var category by remember { mutableStateOf(currentTask?.category ?: "Tasks") }
    var title by remember { mutableStateOf(currentTask?.title ?: "") }
    var description by remember { mutableStateOf(currentTask?.description ?: "") }
    var priority by remember { mutableStateOf(currentTask?.priority?.toString() ?: "4") }
    var status by remember { mutableStateOf(currentTask?.status ?: TaskStatus.NEEDS_DOING) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditMode) "ویرایش تسک" else "ایجاد تسک جدید") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
            // فرم ایجاد/ویرایش تسک
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("عنوان *") },
                modifier = Modifier.fillMaxWidth(),
                isError = title.isEmpty()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = category,
                onValueChange = { category = it },
                label = { Text("دسته‌بندی") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("توضیحات") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                maxLines = 5
            )

            Spacer(modifier = Modifier.height(8.dp))

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

            Spacer(modifier = Modifier.height(8.dp))

            // وضعیت (در حالت ویرایش)
            if (isEditMode) {
                ExposedDropdownMenuBox(
                    expanded = false,
                    onExpandedChange = {}
                ) {
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        value = when (status) {
                            TaskStatus.NEEDS_DOING -> "نیاز به انجام"
                            TaskStatus.DONE -> "انجام شده"
                            TaskStatus.CANCELLED -> "لغو شده"
                        },
                        onValueChange = {},
                        label = { Text("وضعیت") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = false) }
                    )
                    ExposedDropdownMenu(
                        expanded = false,
                        onDismissRequest = {}
                    ) {
                        DropdownMenuItem(
                            text = { Text("نیاز به انجام") },
                            onClick = { status = TaskStatus.NEEDS_DOING }
                        )
                        DropdownMenuItem(
                            text = { Text("انجام شده") },
                            onClick = { status = TaskStatus.DONE }
                        )
                        DropdownMenuItem(
                            text = { Text("لغو شده") },
                            onClick = { status = TaskStatus.CANCELLED }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // دکمه ذخیره
            Button(
                onClick = {
                    if (title.isNotEmpty() && priority.toIntOrNull() in 1..4) {
                        if (isEditMode) {
                            viewModel.updateTask(
                                id = currentTask!!.id,
                                category = category,
                                title = title,
                                description = description.ifBlank { null },
                                priority = priority.toInt(),
                                dueDate = currentTask!!.dueDate,
                                status = status
                            )
                        } else {
                            viewModel.createTask(
                                category = category,
                                title = title,
                                description = description.ifBlank { null },
                                priority = priority.toInt(),
                                dueDate = LocalDateTime.now().plusDays(7) // مثال: مهلت 7 روز آینده
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






















*/


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DateRange
//import androidx.compose.material.icons.filled.CalendarToday
//import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.embag.tdatabasebatime.Model.TaskStatus
//import com.embag.tdatabasebatime.View.components.DatePickerDialog
//import com.embag.tdatabasebatime.ui.components.TimePickerDialog
import com.embag.tdatabasebatime.ViewModel.TaskViewModel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTaskScreen(
    viewModel: TaskViewModel,
    onBack: () -> Unit
) {
    val currentTask by viewModel.currentTask.collectAsState()
    val isEditMode = currentTask != null

    // State variables
    var category by remember { mutableStateOf(currentTask?.category ?: "وظایف") }
    var title by remember { mutableStateOf(currentTask?.title ?: "") }
    var description by remember { mutableStateOf(currentTask?.description ?: "") }
    var priority by remember { mutableStateOf(currentTask?.priority?.toString() ?: "4") }
    var status by remember { mutableStateOf(currentTask?.status ?: TaskStatus.NEEDS_DOING) }

    // State برای مدیریت DatePicker و TimePicker
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
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("عنوان *") },
                modifier = Modifier.fillMaxWidth(),
                isError = title.isEmpty()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = category,
                onValueChange = { category = it },
                label = { Text("دسته‌بندی") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("توضیحات") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                maxLines = 5
            )

            Spacer(modifier = Modifier.height(8.dp))

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

            Spacer(modifier = Modifier.height(16.dp))

            // بخش انتخاب تاریخ و زمان
            Text(
                text = "تاریخ و زمان مهلت:",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

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
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "مهلت نهایی: ${LocalDateTime.of(selectedDate, selectedTime).format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"))}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            // وضعیت (در حالت ویرایش)
            if (isEditMode) {
                var expanded by remember { mutableStateOf(false) }
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
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
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
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                            }
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            statusOptions.forEach { (text, taskStatus) ->
                                DropdownMenuItem(
                                    text = { Text(text) },
                                    onClick = {
                                        status = taskStatus
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // دکمه ذخیره
            Button(
                onClick = {
                    if (title.isNotEmpty() && priority.toIntOrNull() in 1..4) {
                        // ترکیب تاریخ و زمان انتخاب شده
                        val finalDueDate = LocalDateTime.of(selectedDate, selectedTime)

                        if (isEditMode) {
                            viewModel.updateTask(
                                id = currentTask!!.id,
                                category = category,
                                title = title,
                                description = description.ifBlank { null },
                                priority = priority.toInt(),
                                dueDate = finalDueDate, // استفاده از تاریخ و زمان انتخاب شده
                                status = status
                            )
                        } else {
                            viewModel.createTask(
                                category = category,
                                title = title,
                                description = description.ifBlank { null },
                                priority = priority.toInt(),
                                dueDate = finalDueDate, // استفاده از تاریخ و زمان انتخاب شده
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