package com.embag.tdatabasebatime.Views

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.embag.tdatabasebatime.Model.TaskDatabase
import com.embag.tdatabasebatime.Repository.AlgorithmRepository
import com.embag.tdatabasebatime.Repository.TaskRepository
import com.embag.tdatabasebatime.Test.ScheduleDebugScreen
import com.embag.tdatabasebatime.ViewModel.AlgorithmViewModel
import com.embag.tdatabasebatime.ViewModel.TaskViewModel
import com.embag.tdatabasebatime.Views.ui.theme.TDataBaseBaTimeTheme

class MainActivity : ComponentActivity() {
    @SuppressLint("ViewModelConstructorInComposable")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TDataBaseBaTimeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val database = remember {
                        Room.databaseBuilder(
                            context = applicationContext,
                            klass = TaskDatabase::class.java,
                            name = TaskDatabase.DATABASE_NAME
                        )
                            .addMigrations(TaskDatabase.MIGRATION_2_3) // تغییر به MIGRATION_2_3
                            .fallbackToDestructiveMigration()
                            .build()
                    }

                    val repository = remember {
                        TaskRepository(
                            database.taskDao(),
                            database.scheduleDao(),
                            database.taskScheduleDao(),
                            database.categoryDao()
                        )
                    }
                    val viewModel = remember { TaskViewModel(repository, applicationContext) }

                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = "mainScreen"
                    ) {
                        composable("mainScreen") {
                            MainScreen(
                                navController = navController,
                                viewModel = viewModel,
                                onTaskClick = { task ->
                                    viewModel.setCurrentTask(task)
                                    navController.navigate("taskDetail")
                                },
                                onScheduleClick = { schedule ->
                                    viewModel.setCurrentSchedule(schedule.schedule)
                                    navController.navigate("scheduleDetail")
                                },
                                onAddTask = {
                                    viewModel.setCurrentTask(null)
                                    navController.navigate("addEditTask")
                                },
                                onAddSchedule = {
                                    viewModel.setCurrentSchedule(null)
                                    navController.navigate("addEditSchedule")
                                },
                                onBackupRestore = {
                                    navController.navigate("backupRestore")
                                }
                            )
                        }

                        composable("taskDetail") {
                            TaskDetailScreen(
                                viewModel = viewModel,
                                onEdit = {
                                    navController.navigate("addEditTask")
                                },
                                onBack = {
                                    navController.popBackStack()
                                }
                            )
                        }

                        composable("scheduleDetail") {
                            ScheduleDetailScreen(
                                viewModel = viewModel,
                                onEdit = {
                                    navController.navigate("addEditSchedule")
                                },
                                onBack = {
                                    navController.popBackStack()
                                }
                            )
                        }

                        composable("addEditTask") {
                            AddEditTaskScreen(
                                viewModel = viewModel,
                                onBack = {
                                    navController.popBackStack()
                                },
                                onManageCategories = {
                                    navController.navigate("categoryManagement")
                                },
                                onLinkSchedules = {
                                    navController.navigate("linkTaskToSchedule")
                                }
                            )
                        }

                        composable("addEditSchedule") {
                            AddEditScheduleScreen(
                                viewModel = viewModel,
                                onBack = {
                                    // به mainScreen برگرد اما تب زمان‌بندی‌ها را نشان بده
                                    navController.popBackStack()
                                    // یا اگر می‌خواهید به mainScreen برگردید:
                                    // navController.navigate("mainScreen") {
                                    //     popUpTo("mainScreen") { inclusive = true }
                                    // }
                                },
                                onLinkTasks = {
                                    navController.navigate("linkScheduleToTask")
                                }
                            )
                        }

                        composable("categoryManagement") {
                            CategoryManagementScreen(
                                viewModel = viewModel,
                                onBack = {
                                    navController.popBackStack()
                                }
                            )
                        }

                        composable("linkTaskToSchedule") {
                            LinkTaskToScheduleScreen(
                                viewModel = viewModel,
                                onBack = {
                                    navController.popBackStack()
                                }
                            )
                        }

                        composable("linkScheduleToTask") {
                            LinkScheduleToTaskScreen(
                                viewModel = viewModel,
                                onBack = {
                                    navController.popBackStack()
                                }
                            )
                        }
                        composable("backupRestore") {
                            BackupRestoreScreen(
                                viewModel = viewModel,
                                onBack = {
                                    navController.popBackStack()
                                }
                            )
                        }
                        composable("algorithmScreen") {
                            val algorithmRepository = AlgorithmRepository(
                                database.scheduleDao(),
                                database.taskDao(),
                                database.taskScheduleDao()
                            )
                            val algorithmViewModel = AlgorithmViewModel(algorithmRepository)

                            AlgorithmScreen(
                                viewModel = algorithmViewModel,
                                onBack = { navController.popBackStack() }
                            )
                        }
                        composable("scheduleDebug") {
                            ScheduleDebugScreen(
                                viewModel = viewModel,
                                onBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}