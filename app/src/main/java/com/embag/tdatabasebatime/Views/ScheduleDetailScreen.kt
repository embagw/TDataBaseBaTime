package com.embag.tdatabasebatime.Views

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.embag.tdatabasebatime.Model.Entity.ScheduleType
import com.embag.tdatabasebatime.ViewModel.TaskViewModel
import java.time.format.DateTimeFormatter
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleDetailScreen(
    viewModel: TaskViewModel,
    onEdit: () -> Unit,
    onBack: () -> Unit
) {
    val currentSchedule by viewModel.currentSchedule.collectAsState()

    if (currentSchedule == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("زمان‌بندی یافت نشد")
        }
        return
    }

    val schedule = currentSchedule!!

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("جزئیات زمان‌بندی") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        viewModel.deleteSchedule(schedule)
                        onBack()
                    }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                    }
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
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
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    // عنوان
                    Text(
                        text = schedule.title,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // نوع زمان‌بندی
                    DetailRow(
                        title = "نوع زمان‌بندی",
                        value = viewModel.getScheduleTypeText(schedule.type)
                    )

                    // جزئیات بر اساس نوع
                    when (schedule.type) {
                        ScheduleType.SCHEDULED -> {
                            schedule.scheduledDateTime?.let { dateTime ->
                                DetailRow(
                                    title = "زمان برنامه‌ریزی شده",
                                    value = dateTime.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"))
                                )
                            }
                        }
                        ScheduleType.ESTIMATED -> {
                            schedule.estimatedMinutes?.let { minutes ->
                                DetailRow(
                                    title = "مدت زمان تخمینی",
                                    value = "$minutes دقیقه"
                                )
                            }
                        }
                        ScheduleType.COUNT -> {
                            schedule.count?.let { count ->
                                DetailRow(
                                    title = "تعداد دفعات",
                                    value = "${schedule.currentCount}/$count بار"
                                )
                            }
                        }
                        ScheduleType.EVENT -> {
                            schedule.eventDate?.let { date ->
                                DetailRow(
                                    title = "تاریخ رویداد",
                                    value = date.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"))
                                )
                            }
                        }
                    }

                    // توضیحات
                    schedule.description?.let {
                        DetailRow(
                            title = "توضیحات",
                            value = it
                        )
                    }

                    // وضعیت فعال
                    DetailRow(
                        title = "وضعیت",
                        value = if (schedule.isActive) "فعال" else "غیرفعال"
                    )

                    // تاریخ ایجاد
                    DetailRow(
                        title = "تاریخ ایجاد",
                        value = schedule.createdAt.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"))
                    )
                }
            }
        }
    }
}