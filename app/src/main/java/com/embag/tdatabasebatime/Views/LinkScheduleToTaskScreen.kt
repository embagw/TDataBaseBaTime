package com.embag.tdatabasebatime.Views

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.embag.tdatabasebatime.Model.Entity.Task
import com.embag.tdatabasebatime.ViewModel.TaskViewModel


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LinkScheduleToTaskScreen(
    viewModel: TaskViewModel,
    onBack: () -> Unit
) {
    val currentSchedule by viewModel.currentSchedule.collectAsState()
    val availableTasks by viewModel.tasks.collectAsState()
    val tasksForCurrentSchedule by viewModel.tasksForCurrentSchedule.collectAsState()

    // تسک‌های انتخاب شده
    var selectedTaskIds by remember {
        mutableStateOf(tasksForCurrentSchedule.map { it.id }.toMutableSet())
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("اتصال زمان‌بندی به تسک") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            // ذخیره اتصالات
                            val scheduleId = currentSchedule?.id ?: return@IconButton

                            // تسک‌های جدید
                            val newTasks = selectedTaskIds.toList()

                            // به‌روزرسانی اتصالات
                            viewModel.linkTasksToSchedule(newTasks, scheduleId)

                            onBack()
                        }
                    ) {
                        Icon(Icons.Default.Save, contentDescription = "Save")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (currentSchedule == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("زمان‌بندی انتخاب نشده است")
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // اطلاعات زمان‌بندی
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = currentSchedule?.title ?: "",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "نوع: ${currentSchedule?.let { viewModel.getScheduleTypeText(it.type) }}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "جزئیات: ${currentSchedule?.let { viewModel.getScheduleDetails(it) }}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "تعداد تسک‌های متصل: ${tasksForCurrentSchedule.size}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            // لیست تسک‌ها
            LazyColumn (
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (availableTasks.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    Icons.Default.TaskAlt,
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text("هیچ تسکی وجود ندارد")
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "ابتدا یک تسک ایجاد کنید",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                } else {
                    items(availableTasks) { task ->
                        TaskSelectionItem(
                            task = task,
                            isSelected = selectedTaskIds.contains(task.id),
                            viewModel = viewModel,
                            onToggle = {
                                if (selectedTaskIds.contains(task.id)) {
                                    selectedTaskIds.remove(task.id)
                                } else {
                                    selectedTaskIds.add(task.id)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TaskSelectionItem(
    task: Task,
    isSelected: Boolean,
    viewModel: TaskViewModel,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onToggle() }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isSelected,
                onCheckedChange = { onToggle() }
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "اولویت: ${viewModel.getPriorityText(task.priority)}",
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    text = "مهلت: ${viewModel.formatDueDate(task.dueDate)}",
                    style = MaterialTheme.typography.bodySmall
                )

                task.description?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}