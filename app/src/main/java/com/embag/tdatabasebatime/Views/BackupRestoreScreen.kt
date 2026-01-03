package com.embag.tdatabasebatime.Views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.embag.tdatabasebatime.ViewModel.TaskViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackupRestoreScreen(
    viewModel: TaskViewModel,
    onBack: () -> Unit
) {
    var backupFiles by remember { mutableStateOf<List<File>>(emptyList()) }
    var showRestoreDialog by remember { mutableStateOf(false) }
    var selectedBackupFile by remember { mutableStateOf<File?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // بارگیری فایل‌های backup
    LaunchedEffect(Unit) {
        backupFiles = viewModel.getBackupFiles()
    }

    // دیالوگ تأیید restore
    if (showRestoreDialog) {
        AlertDialog(
            onDismissRequest = { showRestoreDialog = false },
            title = { Text("بازگردانی پشتیبان") },
            text = {
                Text("آیا از بازگردانی این پشتیبان اطمینان دارید؟\nداده‌های فعلی جایگزین خواهند شد.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        selectedBackupFile?.let { file ->
                            viewModel.restoreFromBackup(file)
                            showRestoreDialog = false
                            // بارگیری مجدد لیست فایل‌ها
                            backupFiles = viewModel.getBackupFiles()
                        }
                    }
                ) {
                    Text("بازگردانی")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showRestoreDialog = false
                        selectedBackupFile = null
                    }
                ) {
                    Text("لغو")
                }
            }
        )
    }

    // دیالوگ تأیید حذف
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("حذف پشتیبان") },
            text = {
                Text("آیا از حذف این فایل پشتیبان اطمینان دارید؟")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        selectedBackupFile?.let { file ->
                            viewModel.deleteBackupFile(file)
                            showDeleteDialog = false
                            selectedBackupFile = null
                            // بارگیری مجدد لیست فایل‌ها
                            backupFiles = viewModel.getBackupFiles()
                        }
                    }
                ) {
                    Text("حذف")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        selectedBackupFile = null
                    }
                ) {
                    Text("لغو")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("مدیریت پشتیبان‌ها") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // دکمه ایجاد پشتیبان جدید
            Button(
                onClick = {
                    viewModel.createBackup()
                    // بارگیری مجدد لیست فایل‌ها
                    backupFiles = viewModel.getBackupFiles()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                )
            ) {
                Text("ایجاد پشتیبان جدید")
            }

            Text(
                text = "فایل‌های پشتیبان موجود:",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            if (backupFiles.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "هیچ فایل پشتیبانی یافت نشد",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(backupFiles) { file ->
                        BackupFileItem(
                            file = file,
                            onRestore = {
                                selectedBackupFile = file
                                showRestoreDialog = true
                            },
                            onDelete = {
                                selectedBackupFile = file
                                showDeleteDialog = true
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BackupFileItem(
    file: File,
    onRestore: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // نام فایل
            Text(
                text = file.name.replace(".db", ""),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(4.dp))

            // اطلاعات فایل
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // اندازه فایل
                Text(
                    text = "حجم: ${"%.2f".format(file.length() / 1024.0 / 1024.0)} مگابایت",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // تاریخ آخرین تغییر
                val date = Date(file.lastModified())
                val formatter = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())
                Text(
                    text = formatter.format(date),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // دکمه‌های عملیات
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // دکمه بازگردانی
                OutlinedButton(
                    onClick = onRestore,
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Icon(
                        Icons.Default.Restore,
                        contentDescription = "Restore",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("بازگردانی")
                }

                // دکمه حذف
                OutlinedButton(
                    onClick = onDelete,
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("حذف")
                }
            }
        }
    }
}