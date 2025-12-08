package com.embag.tdatabasebatime.Model

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.embag.tdatabasebatime.Model.DAO.TaskDao

@Database(
    entities = [Task::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class TaskDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao

    companion object {
        const val DATABASE_NAME = "task_database"
    }
}