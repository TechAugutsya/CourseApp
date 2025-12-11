package com.example.courseapp.data.local.dao

import androidx.room.*
import com.example.courseapp.data.local.entities.CourseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CourseDao {
    @Query("SELECT * FROM courses ORDER BY createdAt DESC")
    fun getAllFlow(): Flow<List<CourseEntity>>

    @Query("SELECT * FROM courses ORDER BY createdAt DESC")
    suspend fun getAll(): List<CourseEntity>

    @Query("SELECT * FROM courses WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): CourseEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(course: CourseEntity)

    @Update
    suspend fun update(course: CourseEntity)

    @Query("DELETE FROM courses WHERE id = :id")
    suspend fun delete(id: String)
}