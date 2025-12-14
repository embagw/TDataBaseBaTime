package com.embag.tdatabasebatime.Model

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.embag.tdatabasebatime.Model.DAO.CategoryDao
import com.embag.tdatabasebatime.Model.DAO.ScheduleDao
import com.embag.tdatabasebatime.Model.DAO.TaskDao
import com.embag.tdatabasebatime.Model.DAO.TaskScheduleDao
import com.embag.tdatabasebatime.Model.Entity.Category
import com.embag.tdatabasebatime.Model.Entity.Converters
import com.embag.tdatabasebatime.Model.Entity.Schedule
import com.embag.tdatabasebatime.Model.Entity.Task
import com.embag.tdatabasebatime.Model.Entity.TaskScheduleCrossRef
@Database(
    entities = [
        Task::class,
        Schedule::class,
        TaskScheduleCrossRef::class,
        Category::class
    ],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class TaskDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun scheduleDao(): ScheduleDao
    abstract fun taskScheduleDao(): TaskScheduleDao
    abstract fun categoryDao(): CategoryDao

    companion object {
        const val DATABASE_NAME = "task_database"

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // 1. ایجاد جدول categories
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS categories (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        name TEXT NOT NULL,
                        color TEXT,
                        icon TEXT
                    )
                """)

                // 2. درج دسته‌بندی‌های پیش‌فرض
                database.execSQL("INSERT INTO categories (name, color) VALUES ('وظایف عمومی', '#2196F3')")
                database.execSQL("INSERT INTO categories (name, color) VALUES ('کاری', '#4CAF50')")
                database.execSQL("INSERT INTO categories (name, color) VALUES ('شخصی', '#9C27B0')")
                database.execSQL("INSERT INTO categories (name, color) VALUES ('خرید', '#FF9800')")

                // 3. تغییر جدول tasks (اضافه کردن categoryId و حذف category)
                database.execSQL("ALTER TABLE tasks ADD COLUMN categoryId INTEGER")
                database.execSQL("ALTER TABLE tasks ADD COLUMN createdAt TEXT")
                database.execSQL("ALTER TABLE tasks ADD COLUMN updatedAt TEXT")

                // 4. تنظیم categoryId برای تسک‌های موجود
                database.execSQL("""
                    UPDATE tasks 
                    SET categoryId = (SELECT id FROM categories WHERE name = 'وظایف عمومی' LIMIT 1),
                        createdAt = datetime('now'),
                        updatedAt = datetime('now')
                    WHERE categoryId IS NULL
                """)

                // 5. تغییر جدول schedules (اضافه کردن فیلدهای جدید)
                database.execSQL("ALTER TABLE schedules ADD COLUMN title TEXT DEFAULT 'زمان‌بندی جدید'")
                database.execSQL("ALTER TABLE schedules ADD COLUMN description TEXT")
                database.execSQL("ALTER TABLE schedules ADD COLUMN type TEXT DEFAULT 'SCHEDULED'")
                database.execSQL("ALTER TABLE schedules ADD COLUMN scheduledDateTime TEXT")
                database.execSQL("ALTER TABLE schedules ADD COLUMN estimatedMinutes INTEGER")
                database.execSQL("ALTER TABLE schedules ADD COLUMN count INTEGER")
                database.execSQL("ALTER TABLE schedules ADD COLUMN currentCount INTEGER DEFAULT 0")
                database.execSQL("ALTER TABLE schedules ADD COLUMN eventDate TEXT")
                database.execSQL("ALTER TABLE schedules ADD COLUMN createdAt TEXT DEFAULT datetime('now')")

                // 6. به‌روزرسانی زمان‌بندی‌های موجود
                database.execSQL("""
                    UPDATE schedules 
                    SET title = 'زمان‌بندی ' || id,
                        scheduledDateTime = startTime,
                        type = 'SCHEDULED'
                    WHERE type IS NULL
                """)
            }
        }
    }
}