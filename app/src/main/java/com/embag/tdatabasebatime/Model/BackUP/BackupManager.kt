package com.embag.tdatabasebatime.Model.BackUP

import android.content.Context
import android.os.Environment
import android.widget.Toast
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BackupManager(private val context: Context) {

    companion object {
        private const val BACKUP_FOLDER_NAME = "TaskManagerBackups"
        private const val DATABASE_NAME = "task_database"
    }

    // گرفتن مسیر دیتابیس
    private fun getDatabasePath(): File {
        return context.getDatabasePath(DATABASE_NAME)
    }

    // ایجاد پوشه backup در Downloads
    private fun getBackupDirectory(): File {
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val backupDir = File(downloadsDir, BACKUP_FOLDER_NAME)
        if (!backupDir.exists()) {
            backupDir.mkdirs()
        }
        return backupDir
    }

    // ایجاد نام فایل با تاریخ
    private fun generateBackupFileName(): String {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        return "task_backup_$timeStamp.db"
    }

    // گرفتن لیست فایل‌های backup
    fun getBackupFiles(): List<File> {
        val backupDir = getBackupDirectory()
        return backupDir.listFiles { file ->
            file.isFile && file.name.endsWith(".db")
        }?.sortedByDescending { it.lastModified() } ?: emptyList()
    }

    // ایجاد backup
    fun createBackup(): Boolean {
        return try {
            val databaseFile = getDatabasePath()
            if (!databaseFile.exists()) {
                Toast.makeText(context, "فایل دیتابیس وجود ندارد", Toast.LENGTH_SHORT).show()
                return false
            }

            val backupDir = getBackupDirectory()
            val backupFile = File(backupDir, generateBackupFileName())

            FileInputStream(databaseFile).use { inputStream ->
                FileOutputStream(backupFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }

            Toast.makeText(context, "پشتیبان با موفقیت ایجاد شد: ${backupFile.name}", Toast.LENGTH_LONG).show()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "خطا در ایجاد پشتیبان: ${e.message}", Toast.LENGTH_SHORT).show()
            false
        }
    }

    // بازیابی از backup
    fun restoreFromBackup(backupFile: File): Boolean {
        return try {
            val databaseFile = getDatabasePath()

            // بستن دیتابیس قبل از بازیابی
            // این کار باید در Repository یا ViewModel انجام شود

            FileInputStream(backupFile).use { inputStream ->
                FileOutputStream(databaseFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }

            Toast.makeText(context, "بازیابی با موفقیت انجام شد", Toast.LENGTH_LONG).show()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "خطا در بازیابی: ${e.message}", Toast.LENGTH_SHORT).show()
            false
        }
    }

    // حذف فایل backup
    fun deleteBackupFile(file: File): Boolean {
        return try {
            file.delete()
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}