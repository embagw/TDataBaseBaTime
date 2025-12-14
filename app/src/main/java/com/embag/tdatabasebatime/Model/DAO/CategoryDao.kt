package com.embag.tdatabasebatime.Model.DAO

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.embag.tdatabasebatime.Model.Entity.Category
import com.embag.tdatabasebatime.Model.Entity.CategoryWithTaskCount
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: Category): Long

    @Update
    suspend fun updateCategory(category: Category)

    @Delete
    suspend fun deleteCategory(category: Category)

    @Query("SELECT * FROM categories ORDER BY name ASC")
    fun getAllCategories(): Flow<List<Category>>

    @Query("SELECT * FROM categories WHERE id = :categoryId")
    suspend fun getCategoryById(categoryId: Long): Category?

    @Query("DELETE FROM categories WHERE id = :categoryId")
    suspend fun deleteCategoryById(categoryId: Long)

    @Transaction
    @Query("""
        SELECT c.*, COUNT(t.id) as task_count
        FROM categories c
        LEFT JOIN tasks t ON c.id = t.categoryId
        GROUP BY c.id
        ORDER BY c.name ASC
    """)
    fun getCategoriesWithTaskCount(): Flow<List<CategoryWithTaskCount>>
}