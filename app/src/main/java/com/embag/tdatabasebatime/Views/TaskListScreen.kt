package com.embag.tdatabasebatime.Views

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.embag.tdatabasebatime.Model.Entity.Task
import com.embag.tdatabasebatime.Model.Entity.TaskStatus
import com.embag.tdatabasebatime.ViewModel.TaskViewModel

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
                title = { Text("مدیریت وظیفه ها") }
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
                Text("هیچ وظیفه ای وجود ندارد")
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

            // دسته‌بندی (استفاده از getCategoryName)
            Text(
                text = "دسته‌بندی: ${viewModel.getCategoryName(task.categoryId)}",
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
                    text = "مهلت: ${viewModel.formatDueDate(task.dueDate)}",
                    style = MaterialTheme.typography.labelSmall
                )

                // وضعیت
                Badge(
                    containerColor = when (task.status) {
                        TaskStatus.DONE -> MaterialTheme.colorScheme.primaryContainer
                        TaskStatus.CANCELLED -> MaterialTheme.colorScheme.errorContainer
                        else -> MaterialTheme.colorScheme.secondaryContainer
                    }
                ) {
                    Text(viewModel.getStatusText(task.status))
                }
            }
        }
    }
}