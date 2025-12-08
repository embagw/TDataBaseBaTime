package com.embag.tdatabasebatime.View

import android.os.Build
import androidx.annotation.RequiresApi
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
    var category by remember { mutableStateOf(currentTask?.category ?: "") }
    var title by remember { mutableStateOf(currentTask?.title ?: "") }
    var description by remember { mutableStateOf(currentTask?.description ?: "") }
    var priority by remember { mutableStateOf(currentTask?.priority?.toString() ?: "1") }
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