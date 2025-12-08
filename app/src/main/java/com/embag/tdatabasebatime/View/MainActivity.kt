package com.embag.tdatabasebatime.View

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
import com.embag.tdatabasebatime.Repository.TaskRepository
import com.embag.tdatabasebatime.View.ui.theme.TDataBaseBaTimeTheme
import com.embag.tdatabasebatime.ViewModel.TaskViewModel

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TDataBaseBaTimeTheme {
                Surface (
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val database = remember {
                        Room.databaseBuilder(
                            context = applicationContext,
                            klass = TaskDatabase::class.java,
                            name = TaskDatabase.DATABASE_NAME
                        ).build()
                    }

                    val repository = remember { TaskRepository(database.taskDao()) }
                    val viewModel = remember { TaskViewModel(repository) }

                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = "taskList"
                    ) {
                        composable("taskList") {
                            TaskListScreen(
                                viewModel = viewModel,
                                onTaskClick = { task ->
                                    viewModel.setCurrentTask(task)
                                    navController.navigate("taskDetail")
                                },
                                onAddTask = {
                                    viewModel.setCurrentTask(null)
                                    navController.navigate("addEditTask")
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

                        composable("addEditTask") {
                            AddEditTaskScreen(
                                viewModel = viewModel,
                                onBack = {
                                    navController.popBackStack()
                                }
                            )
                        }
                    }
                }
            }
        }
    }

}