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
import java.time.temporal.ChronoUnit

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

                    // دسته‌بندی
                    DetailRow(
                        title = "دسته‌بندی",
                        value = viewModel.getCategoryName(schedule.categoryId)
                    )

                    // نوع زمان‌بندی
                    DetailRow(
                        title = "نوع زمان‌بندی",
                        value = viewModel.getScheduleTypeText(schedule.type)
                    )

                    // تاریخ زمان‌بندی (برای همه انواع)
                    schedule.scheduleDate?.let { date ->
                        DetailRow(
                            title = "تاریخ",
                            value = date.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"))
                        )
                    }

                    // جزئیات بر اساس نوع
                    when (schedule.type) {
                        ScheduleType.SCHEDULED -> {
                            schedule.startTime?.let { startTime ->
                                DetailRow(
                                    title = "ساعت شروع",
                                    value = startTime.format(DateTimeFormatter.ofPattern("HH:mm"))
                                )
                            }
                            schedule.endTime?.let { endTime ->
                                DetailRow(
                                    title = "ساعت پایان",
                                    value = endTime.format(DateTimeFormatter.ofPattern("HH:mm"))
                                )
                            }
                            // نمایش مدت زمان
                            if (schedule.startTime != null && schedule.endTime != null) {
                                val duration = ChronoUnit.MINUTES.between(
                                    schedule.startTime,
                                    schedule.endTime
                                )
                                DetailRow(
                                    title = "مدت زمان",
                                    value = "$duration دقیقه"
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
                            // برای رویداد فقط تاریخ را نشان می‌دهیم
                            // هیچ کار خاصی نیاز نیست، قبلاً تاریخ نشان داده شده
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

/*@Composable
fun DetailRow(title: String, value: String) {
    Column(
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}*/