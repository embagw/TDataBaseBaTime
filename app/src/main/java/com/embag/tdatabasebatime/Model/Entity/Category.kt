package com.embag.tdatabasebatime.Model.Entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val color: String? = null, // برای ذخیره رنگ به صورت hex
    val icon: String? = null // نام آیکون
)

data class CategoryWithTaskCount(
    @Embedded val category: Category,
    @ColumnInfo(name = "task_count") val taskCount: Int
)