package com.example.courseapp.data.repository

import android.util.Log
import com.example.courseapp.data.local.dao.CategoryDao
import com.example.courseapp.data.local.dao.CourseDao
import com.example.courseapp.data.local.entities.CategoryEntity
import com.example.courseapp.data.remote.CategoryApi
import com.example.courseapp.data.remote.CategoryDto
import com.example.courseapp.domain.models.Category
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class CategoryRepository(
    private val api: CategoryApi,
    private val categoryDao: CategoryDao,
    private val courseDao: CourseDao
) {
    companion object {
        private const val TAG = "CategoryRepository"
    }

    suspend fun getCategories(): List<Category> {
        Log.d(TAG, "Fetching categories...")

        val apiCategories = try {
            Log.d(TAG, "Attempting to fetch from API...")
            val remote: List<CategoryDto> = api.getCategories()
            Log.d(TAG, "API response received: ${remote.size} categories")

            val entities = remote.map {
                CategoryEntity(it.name, it.name, System.currentTimeMillis())
            }

            categoryDao.insertAll(entities)
            Log.d(TAG, "Categories cached successfully")

            entities.map { Category(it.id, it.name) }
        } catch (e: Exception) {
            Log.e(TAG, "API fetch failed: ${e.message}", e)
            val cached = categoryDao.getAll().map { Category(it.id, it.name) }
            Log.d(TAG, "Using cached categories: ${cached.size} found")
            cached
        }

        val localCourseCategories = courseDao.getUniqueCategories()
            .map { Category(it, it) }

        Log.d(TAG, "Local course categories: ${localCourseCategories.size} found")

        val allCategories = (apiCategories + localCourseCategories)
            .distinctBy { it.name.lowercase() }
            .sortedBy { it.name }

        Log.d(TAG, "Total unique categories: ${allCategories.size}")
        return allCategories
    }


    fun observeCategoriesFlow(): Flow<List<Category>> =
        combine(
            categoryDao.getAllFlow(),
            courseDao.getUniqueCategoriesFlow()
        ) { cachedCategories, localCategories ->
            val apiCats = cachedCategories.map { Category(it.id, it.name) }

            val localCats = localCategories.map { Category(it, it) }

            val merged = (apiCats + localCats)
                .distinctBy { it.name.lowercase() }
                .sortedBy { it.name }

            Log.d(TAG, "Flow emitted: ${merged.size} categories")
            merged
        }
}