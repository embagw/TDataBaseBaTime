package com.embag.tdatabasebatime.Views


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddTask
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.embag.tdatabasebatime.Model.Entity.ScheduleWithTasks
import com.embag.tdatabasebatime.Model.Entity.Task
import com.embag.tdatabasebatime.ViewModel.TaskViewModel


@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainScreen(
    navController: NavController,
    viewModel: TaskViewModel,
    onTaskClick: (Task) -> Unit,
    onScheduleClick: (ScheduleWithTasks) -> Unit,
    onAddTask: () -> Unit,
    onAddSchedule: () -> Unit,
    onManageCategories: () -> Unit = {},
    onBackupRestore: () -> Unit = {}
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("وظیفه ها", "زمان‌بندی‌ها")

    // متغیر برای کنترل باز/بسته بودن FAB منو
    var expanded by remember { mutableStateOf(false) }




    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("مدیریت وظیفه ها") },
                actions = {

                    IconButton(
                        onClick = { navController.navigate("calendarView") }
                    ) {
                        Icon(Icons.Default.CalendarToday, contentDescription = "تقویم")
                    }
                    val isDebugMode = true

                    if (isDebugMode) {
                        IconButton(
                            onClick = { navController.navigate("scheduleDebug") }
                        ) {
                            Icon(Icons.Default.BugReport, contentDescription = "دیباگ")
                        }
                    }
                        // دکمه الگوریتم
                        IconButton(
                            onClick = {
                                navController.navigate("algorithmScreen")
                            }
                        ) {
                            Icon(
                                Icons.Default.AutoAwesome,
                                contentDescription = "الگوریتم برنامه‌ریزی"
                            )
                        }

                    IconButton(onClick = onBackupRestore) {
                        Icon(Icons.Default.Backup, contentDescription = "Backup")
                    }
                }
            )
        },
                floatingActionButton = {
            // Extended FAB با منو
            Column(
                horizontalAlignment = Alignment.End
            ) {
                // دکمه‌های منو
                AnimatedVisibility(
                    visible = expanded,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
                    exit = fadeOut() + slideOutVertically(targetOffsetY = { it })
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        // دکمه افزودن زمان‌بندی
                        ExtendedFloatingActionButton(
                            onClick = {
                                expanded = false
                                onAddSchedule()
                            },
                            icon = { Icon(Icons.Default.Schedule, contentDescription = null) },
                            text = { Text("افزودن زمان‌بندی") },
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )

                        // دکمه افزودن تسک
                        ExtendedFloatingActionButton(
                            onClick = {
                                expanded = false
                                onAddTask()
                            },
                            icon = { Icon(Icons.Default.AddTask, contentDescription = null) },
                            text = { Text("افزودن تسک") },
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                // دکمه اصلی
                FloatingActionButton(
                    onClick = { expanded = !expanded },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(
                        if (expanded) Icons.Default.Close else Icons.Default.Add,
                        contentDescription = if (expanded) "بستن منو" else "باز کردن منو"
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
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
//
                )
                1 -> ScheduleListScreen(
                    viewModel = viewModel,
                    onScheduleClick = onScheduleClick,
//
                )
            }
        }
    }
}
