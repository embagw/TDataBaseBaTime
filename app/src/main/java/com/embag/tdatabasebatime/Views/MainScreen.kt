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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.embag.tdatabasebatime.Model.Entity.ScheduleWithCalculatedPriority
import com.embag.tdatabasebatime.Model.Entity.Task
import com.embag.tdatabasebatime.ViewModel.TaskViewModel
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainScreen(
    viewModel: TaskViewModel,
    onTaskClick: (Task) -> Unit,
    onScheduleClick: (ScheduleWithCalculatedPriority) -> Unit,
    onAddTask: () -> Unit,
    onAddSchedule: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("تسک‌ها", "زمان‌بندی‌ها")

    Scaffold (){padd->
        Column(modifier = Modifier.fillMaxSize().padding(padd)) {
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }

            when (selectedTab) {
                0 -> TaskListScreen(
                    viewModel = viewModel,
                    onTaskClick = onTaskClick,
                    onAddTask = onAddTask
                )

                1 -> ScheduleListScreen(
                    viewModel = viewModel,
                    onScheduleClick = onScheduleClick,
                    onAddSchedule = onAddSchedule
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleListScreen(
    viewModel: TaskViewModel,
    onScheduleClick: (ScheduleWithCalculatedPriority) -> Unit,
    onAddSchedule: () -> Unit
) {
    val schedules by viewModel.schedulesWithPriority.collectAsState()

    Scaffold (
        floatingActionButton = {
            FloatingActionButton (
                onClick = onAddSchedule,
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Schedule")
            }
        },
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
            LazyColumn (
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(16.dp)
            ) {
                itemsIndexed(schedules) { index, schedule ->
                    ScheduleCard (
                        scheduleWithPriority = schedule,
                        viewModel = viewModel,
                        onClick = { onScheduleClick(schedule) }
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ScheduleCard(
    scheduleWithPriority: ScheduleWithCalculatedPriority,
    viewModel: TaskViewModel,
    onClick: () -> Unit
) {
    val schedule = scheduleWithPriority.schedule
    Card (
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
                // زمان شروع و پایان
                Column {
                    Text(
                        text = "شروع: ${schedule.startTime.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"))}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "پایان: ${schedule.endTime.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"))}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                // اولویت
                AssistChip(
                    onClick = {},
                    label = { Text(viewModel.getSchedulePriorityText(scheduleWithPriority)) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = when (scheduleWithPriority.calculatedPriority ?: Int.MAX_VALUE) {
                            1 -> MaterialTheme.colorScheme.errorContainer
                            2 -> MaterialTheme.colorScheme.primaryContainer
                            3 -> MaterialTheme.colorScheme.secondaryContainer
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        }
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // مدت زمان
            Text(
                text = "مدت زمان: ${schedule.durationMinutes} دقیقه",
                style = MaterialTheme.typography.bodySmall
            )

            // الگوی تکراری
            schedule.recurrencePattern?.let {
                Text(
                    text = "تکرار: $it",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // وضعیت فعال
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (schedule.isActive) Icons.Default.CheckCircle else Icons.Default.Close,
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