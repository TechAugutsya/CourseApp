package com.example.courseapp.domain.models

import com.example.courseapp.data.local.entities.CourseEntity

data class Course(
    val id: String,
    val title: String,
    val description: String,
    val categoryId: String,
    val lessons: Int,
    val score: Int,
    val createdAt: Long,
    val updatedAt: Long?
)

fun CourseEntity.toCourse() = Course(id, title, description, categoryId, lessons, score, createdAt, updatedAt)
fun Course.toEntity() = CourseEntity(id, title, description, categoryId, lessons, score, createdAt, updatedAt)
