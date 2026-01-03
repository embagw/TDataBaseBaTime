package com.embag.tdatabasebatime.Views

import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.embag.tdatabasebatime.Model.Entity.ScheduleWithTasks
import com.embag.tdatabasebatime.ViewModel.TaskViewModel

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleListScreen(
    viewModel: TaskViewModel,
    onScheduleClick: (ScheduleWithTasks) -> Unit,
//    onAddSchedule: () -> Unit
) {
    val schedules by viewModel.schedulesWithTasks.collectAsState() // تغییر اینجا

    Scaffold(
        /*floatingActionButton = {
            FloatingActionButton(
                onClick = onAddSchedule,
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Schedule")
            }
        },*/
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("مدیریت زمان‌بندی‌ها") }
            )
        }
    ) { paddingValues ->
        if (schedules.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("هیچ زمان‌بندی وجود ندارد")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(schedules) { schedule ->
                    ScheduleCard(
                        scheduleWithTasks = schedule,
                        viewModel = viewModel,
                        onClick = { onScheduleClick(schedule) }
                    )
                }
            }
        }
    }
}

@Composable
@RequiresApi(Build.VERSION_CODES.O)
fun ScheduleCard(
    scheduleWithTasks: ScheduleWithTasks,
    viewModel: TaskViewModel,
    onClick: () -> Unit
) {
    val schedule = scheduleWithTasks.schedule
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
                // اطلاعات زمان‌بندی
                Column {
                    Text(
                        text = schedule.title,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = viewModel.getScheduleTypeText(schedule.type),
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = viewModel.getScheduleDetails(schedule),
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                // اولویت
                AssistChip(
                    onClick = {},
                    label = {
                        Text(
                            viewModel.getSchedulePriorityTextWithDefault(scheduleWithTasks)
                        )
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = when (scheduleWithTasks.calculatedPriority) {
                            1 -> MaterialTheme.colorScheme.errorContainer
                            2 -> MaterialTheme.colorScheme.primaryContainer
                            3 -> MaterialTheme.colorScheme.secondaryContainer
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        }
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // تعداد تسک‌های متصل
            Text(
                text = "تعداد تسک‌های متصل: ${scheduleWithTasks.tasks.size}",
                style = MaterialTheme.typography.bodySmall
            )

            // وضعیت فعال
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (schedule.isActive) Icons.Default.CheckCircle else Icons.Default.Cancel,
                    contentDescription = null,
                    tint = if (schedule.isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (schedule.isActive) "فعال" else "غیرفعال",
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}