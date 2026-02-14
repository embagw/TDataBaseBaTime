package com.embag.tdatabasebatime.Model.BackUP


import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
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

    // ایجاد پوشه backup
    private fun getBackupDirectory(): File {
        val downloadsDir = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
        } else {
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        }

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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (!Environment.isExternalStorageManager()) {
                    val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                    intent.data = Uri.parse("package:" + context.packageName)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    context.startActivity(intent)

                    Toast.makeText(
                        context,
                        "برای ایجاد پشتیبان، لطفاً مجوز \"مدیریت همه فایل‌ها\" را فعال کنید.",
                        Toast.LENGTH_LONG
                    ).show()

                    return false
                }
            }

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

            Toast.makeText(
                context,
                "پشتیبان با موفقیت ایجاد شد: ${backupFile.name}\nمسیر: ${backupDir.absolutePath}",
                Toast.LENGTH_LONG
            ).show()

            true
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(
                context,
                "خطا در ایجاد پشتیبان: ${e.localizedMessage}",
                Toast.LENGTH_SHORT
            ).show()
            false
        }
    }

    // ** بستن دیتابیس Room**
    private fun closeDatabase() {
        try {
            // روش مستقیم برای بستن connection های SQLite
            val dbFile = getDatabasePath()
            val journalFile = File(dbFile.parent, "${DATABASE_NAME}-journal")
            val walFile = File(dbFile.parent, "${DATABASE_NAME}-wal")
            val shmFile = File(dbFile.parent, "${DATABASE_NAME}-shm")

            // حذف فایل‌های journal که ممکن است قفل باشند
            if (journalFile.exists()) journalFile.delete()
            if (walFile.exists()) walFile.delete()
            if (shmFile.exists()) shmFile.delete()

            // کمی تاخیر برای اطمینان از آزاد شدن قفل‌ها
            Thread.sleep(300)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // **بازیابی از backup**
    fun restoreFromBackup(backupFile: File): Boolean {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (!Environment.isExternalStorageManager()) {
                    val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                    intent.data = Uri.parse("package:" + context.packageName)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    context.startActivity(intent)

                    Toast.makeText(
                        context,
                        "برای بازیابی پشتیبان، لطفاً مجوز \"مدیریت همه فایل‌ها\" را فعال کنید.",
                        Toast.LENGTH_LONG
                    ).show()

                    return false
                }
            }

            val databaseFile = getDatabasePath()

            // **1. ابتدا دیتابیس فعلی را می‌بندیم**
            closeDatabase()

            // **2. کپی فایل بک‌آپ به محل دیتابیس**
            FileInputStream(backupFile).use { inputStream ->
                FileOutputStream(databaseFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }

            // **3. حذف فایل‌های journal قدیمی**
            val journalFile = File(databaseFile.parent, "${DATABASE_NAME}-journal")
            val walFile = File(databaseFile.parent, "${DATABASE_NAME}-wal")
            val shmFile = File(databaseFile.parent, "${DATABASE_NAME}-shm")

            if (journalFile.exists()) journalFile.delete()
            if (walFile.exists()) walFile.delete()
            if (shmFile.exists()) shmFile.delete()

            // **4. منتظر می‌مانیم و سپس برنامه را مجبور به بارگیری مجدد دیتابیس می‌کنیم**
            Thread.sleep(500)

            Toast.makeText(
                context,
                "بازیابی با موفقیت انجام شد.\nلطفاً برنامه را کاملاً ببندید و دوباره باز کنید.",
                Toast.LENGTH_LONG
            ).show()

            true
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(
                context,
                "خطا در بازیابی: ${e.localizedMessage}\nممکن است دیتابیس قفل باشد.",
                Toast.LENGTH_LONG
            ).show()
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