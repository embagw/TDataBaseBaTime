package com.embag.tdatabasebatime.Model

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.embag.tdatabasebatime.Model.DAO.ScheduleDao
import com.embag.tdatabasebatime.Model.DAO.TaskDao
import com.embag.tdatabasebatime.Model.DAO.TaskScheduleDao
import com.embag.tdatabasebatime.Model.Entity.Converters
import com.embag.tdatabasebatime.Model.Entity.Schedule
import com.embag.tdatabasebatime.Model.Entity.Task
import com.embag.tdatabasebatime.Model.Entity.TaskScheduleCrossRef

@Database(
    entities = [
        Task::class,
        Schedule::class,
        TaskScheduleCrossRef::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class TaskDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun scheduleDao(): ScheduleDao
    abstract fun taskScheduleDao(): TaskScheduleDao

    companion object {
        const val DATABASE_NAME = "task_database"

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // ایجاد جدول schedules
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS schedules (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        startTime TEXT NOT NULL,
                        endTime TEXT NOT NULL,
                        durationMinutes INTEGER NOT NULL,
                        recurrencePattern TEXT,
                        isActive INTEGER NOT NULL DEFAULT 1
                    )
                """)

                // ایجاد جدول واسط با ایندکس
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS task_schedule (
                        taskId INTEGER NOT NULL,
                        scheduleId INTEGER NOT NULL,
                        PRIMARY KEY(taskId, scheduleId),
                        FOREIGN KEY(taskId) REFERENCES tasks(id) ON DELETE CASCADE,
                        FOREIGN KEY(scheduleId) REFERENCES schedules(id) ON DELETE CASCADE
                    )
                """)

                // ایجاد ایندکس‌ها
                database.execSQL(
                    "CREATE INDEX IF NOT EXISTS index_task_schedule_taskId ON task_schedule(taskId)"
                )
                database.execSQL(
                    "CREATE INDEX IF NOT EXISTS index_task_schedule_scheduleId ON task_schedule(scheduleId)"
                )

                // اضافه کردن ستون estimatedDurationMinutes به tasks اگر وجود ندارد
                try {
                    database.execSQL(
                        "ALTER TABLE tasks ADD COLUMN estimatedDurationMinutes INTEGER DEFAULT 0"
                    )
                } catch (e: Exception) {
                    // ستون ممکن است از قبل وجود داشته باشد
                }
            }
        }
    }
}