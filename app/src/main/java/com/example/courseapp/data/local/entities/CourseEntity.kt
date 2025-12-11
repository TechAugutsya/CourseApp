package com.example.courseapp.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "courses")
data class CourseEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val categoryId: String,
    val lessons: Int,
    val score: Int,
    val createdAt: Long,
    val updatedAt: Long?
)