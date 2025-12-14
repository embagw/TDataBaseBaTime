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
import androidx.compose.material.icons.filled.Schedule
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
import com.embag.tdatabasebatime.Model.Entity.Schedule
import com.embag.tdatabasebatime.ViewModel.TaskViewModel


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LinkTaskToScheduleScreen(
    viewModel: TaskViewModel,
    onBack: () -> Unit
) {
    val currentTask by viewModel.currentTask.collectAsState()
    val availableSchedules by viewModel.availableSchedules.collectAsState()
    val schedulesForCurrentTask by viewModel.schedulesForCurrentTask.collectAsState()

    // زمان‌بندی‌های انتخاب شده
    var selectedScheduleIds by remember {
        mutableStateOf(schedulesForCurrentTask.map { it.id }.toMutableSet())
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("اتصال تسک به زمان‌بندی") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            // ذخیره اتصالات
                            val taskId = currentTask?.id ?: return@IconButton

                            // زمان‌بندی‌های جدید
                            val newSchedules = selectedScheduleIds.toList()

                            // به‌روزرسانی اتصالات
                            viewModel.linkSchedulesToTask(taskId, newSchedules)

                            onBack()
                        }
                    ) {
                        Icon(Icons.Default.Save, contentDescription = "Save")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (currentTask == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("تسک انتخاب نشده است")
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // اطلاعات تسک
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
                        text = currentTask?.title ?: "",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "دسته‌بندی: ${viewModel.getCategoryName(currentTask?.categoryId)}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "تعداد زمان‌بندی‌های متصل: ${schedulesForCurrentTask.size}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            // لیست زمان‌بندی‌ها
            LazyColumn (
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (availableSchedules.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    Icons.Default.Schedule,
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text("هیچ زمان‌بندی‌ای وجود ندارد")
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "ابتدا یک زمان‌بندی ایجاد کنید",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                } else {
                    items(availableSchedules) { schedule ->
                        ScheduleSelectionItem(
                            schedule = schedule,
                            isSelected = selectedScheduleIds.contains(schedule.id),
                            viewModel = viewModel,
                            onToggle = {
                                if (selectedScheduleIds.contains(schedule.id)) {
                                    selectedScheduleIds.remove(schedule.id)
                                } else {
                                    selectedScheduleIds.add(schedule.id)
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
fun ScheduleSelectionItem(
    schedule: Schedule,
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
                    text = schedule.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "${viewModel.getScheduleTypeText(schedule.type)}: ${viewModel.getScheduleDetails(schedule)}",
                    style = MaterialTheme.typography.bodySmall
                )

                schedule.description?.let {
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