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

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // اضافه کردن فیلدهای جدید تکرار
                database.execSQL("ALTER TABLE schedules ADD COLUMN repeatType TEXT DEFAULT 'NONE'")
                database.execSQL("ALTER TABLE schedules ADD COLUMN repeatInterval INTEGER DEFAULT 1")
                database.execSQL("ALTER TABLE schedules ADD COLUMN repeatDaysOfWeek TEXT")
                database.execSQL("ALTER TABLE schedules ADD COLUMN repeatDayOfMonth INTEGER")
                database.execSQL("ALTER TABLE schedules ADD COLUMN repeatMonthOfYear INTEGER")
                database.execSQL("ALTER TABLE schedules ADD COLUMN repeatEndDate TEXT")
                database.execSQL("ALTER TABLE schedules ADD COLUMN repeatCount INTEGER")
                database.execSQL("ALTER TABLE schedules ADD COLUMN updatedAt TEXT DEFAULT datetime('now')")
            }
        }
    }
}