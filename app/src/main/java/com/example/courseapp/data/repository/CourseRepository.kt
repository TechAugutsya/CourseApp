package com.example.courseapp.data.repository

import com.example.courseapp.data.local.dao.CourseDao
import com.example.courseapp.domain.models.Course
import com.example.courseapp.domain.models.toCourse
import com.example.courseapp.domain.models.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CourseRepository(private val dao: CourseDao) {

    fun observeCourses(): Flow<List<Course>> =
        dao.getAllFlow().map { it.map { e -> e.toCourse() } }

    suspend fun getCourses(): List<Course> =
        dao.getAll().map { it.toCourse() }

    suspend fun getCourse(id: String): Course? =
        dao.getById(id)?.toCourse()

    suspend fun addCourse(course: Course) =
        dao.insert(course.toEntity())

    suspend fun updateCourse(course: Course) =
        dao.update(course.toEntity())

    suspend fun deleteCourse(id: String) =
        dao.delete(id)
}