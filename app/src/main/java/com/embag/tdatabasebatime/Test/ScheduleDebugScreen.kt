package com.embag.tdatabasebatime.Test

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import com.embag.tdatabasebatime.Model.Entity.ScheduleType
import com.embag.tdatabasebatime.ViewModel.TaskViewModel
import kotlinx.coroutines.launch
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ScheduleDebugScreen(
    viewModel: TaskViewModel,
    onBack: () -> Unit
) {
    var debugText by remember  { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect (Unit) {
        loadDebugInfo(viewModel) { info ->
            debugText = info
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("دیباگ زمان‌بندی‌ها") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "بازگشت")
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
            // دکمه‌های عملیاتی
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        isLoading = true
                        viewModel.createTestEstimatedSchedule()
                        Toast.makeText(
                            LocalContext.current,
                            "یک زمان‌بندی تخمینی نمونه ایجاد شد",
                            Toast.LENGTH_SHORT
                        ).show()

                        // بارگیری مجدد اطلاعات
                        loadDebugInfo(viewModel) { info ->
                            debugText = info
                            isLoading = false
                        }
                    }
                ) {
                    Text("ایجاد نمونه تخمینی")
                }

                Button(
                    onClick = {
                        isLoading = true
                        loadDebugInfo(viewModel) { info ->
                            debugText = info
                            isLoading = false
                        }
                    }
                ) {
                    Text("بارگیری مجدد")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                Card (
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Text(
                        text = debugText,
                        modifier = Modifier
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState()),
                        style = MaterialTheme.typography.bodyMedium,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}

// تابع کمکی برای بارگیری اطلاعات دیباگ
@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun loadDebugInfo(
    viewModel: TaskViewModel,
    onResult: (String) -> Unit
) {
    LaunchedEffect(Unit) {
        try {
            val allSchedules = viewModel.getAllSchedulesForDebug()

            val info = buildString {
                appendLine("📊 دیباگ زمان‌بندی‌ها - ${LocalDateTime.now()}")
                appendLine("=".repeat(60))
                appendLine()

                val estimatedSchedules = allSchedules.filter { it.type == ScheduleType.ESTIMATED }
                appendLine("🎯 زمان‌بندی‌های تخمینی (${estimatedSchedules.size}):")
                appendLine("-".repeat(40))

                if (estimatedSchedules.isEmpty()) {
                    appendLine("❌ هیچ زمان‌بندی تخمینی وجود ندارد")
                } else {
                    estimatedSchedules.forEachIndexed { index, schedule ->
                        appendLine("${index + 1}. ${schedule.title}")
                        appendLine("   ID: ${schedule.id}")
                        appendLine("   تاریخ: ${schedule.scheduleDate ?: "NULL ⚠️"}")
                        appendLine("   تخمین: ${schedule.estimatedMinutes} دقیقه")
                        appendLine("   فعال: ${schedule.isActive}")
                        appendLine("   ایجاد: ${schedule.createdAt}")
                        appendLine()
                    }
                }

                appendLine()

                appendLine("📋 همه زمان‌بندی‌ها (${allSchedules.size}):")
                appendLine("-".repeat(40))

                allSchedules.forEachIndexed { index, schedule ->
                    val dateInfo = if (schedule.scheduleDate == null) "NULL" else schedule.scheduleDate.toString()
                    appendLine("${index + 1}. ${schedule.title} (${schedule.type}) - تاریخ: $dateInfo")
                }
            }

            onResult(info)
        } catch (e: Exception) {
            onResult("❌ خطا: ${e.message}")
        }
    }
}