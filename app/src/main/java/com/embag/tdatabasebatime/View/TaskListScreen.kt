package com.embag.tdatabasebatime.View

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.embag.tdatabasebatime.Model.Task
import com.embag.tdatabasebatime.ViewModel.TaskViewModel
import java.time.format.DateTimeFormatter


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    viewModel: TaskViewModel,
    onTaskClick: (Task) -> Unit,
    onAddTask: () -> Unit
) {
    val tasks by viewModel.tasks.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddTask,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Task")
            }
        },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("مدیریت تسک‌ها") }
            )
        }
    ) { paddingValues ->
        if (tasks.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("هیچ تسکی وجود ندارد")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(tasks) { task ->
                    TaskItem(
                        task = task,
                        viewModel = viewModel,
                        onClick = { onTaskClick(task) }
                    )
                }
            }
        }
    }
}

@Composable
fun TaskItem(
    task: Task,
    viewModel: TaskViewModel,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )

                // نمایش اولویت
                AssistChip(
                    onClick = {},
                    label = { Text(viewModel.getPriorityText(task.priority)) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = when (task.priority) {
                            1 -> MaterialTheme.colorScheme.errorContainer
                            2 -> MaterialTheme.colorScheme.primaryContainer
                            3 -> MaterialTheme.colorScheme.secondaryContainer
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        }
                    )
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // دسته‌بندی
            Text(
                text = "دسته‌بندی: ${task.category}",
                style = MaterialTheme.typography.bodyMedium
            )

            // توضیحات
            task.description?.let {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // تاریخ مهلت
                Text(
                    text = "مهلت: ${task.dueDate.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"))}",
                    style = MaterialTheme.typography.labelSmall
                )

                // وضعیت
                Badge(
                    containerColor = when (task.status) {
                        com.embag.tdatabasebatime.Model.TaskStatus.DONE -> MaterialTheme.colorScheme.primaryContainer
                        com.embag.tdatabasebatime.Model.TaskStatus.CANCELLED -> MaterialTheme.colorScheme.errorContainer
                        else -> MaterialTheme.colorScheme.secondaryContainer
                    }
                ) {
                    Text(viewModel.getStatusText(task.status))
                }
            }
        }
    }
}