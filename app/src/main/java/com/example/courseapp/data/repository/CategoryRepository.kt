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

    /**
     * Fetches categories from API first and caches them.
     * If API fails, falls back to cached categories.
     * Always includes unique categories from local courses.
     */
    suspend fun getCategories(): List<Category> {
        Log.d(TAG, "Fetching categories...")

        // Step 1: Try to fetch from API
        val apiCategories = try {
            Log.d(TAG, "Attempting to fetch from API...")
            val remote: List<CategoryDto> = api.getCategories()
            Log.d(TAG, "API response received: ${remote.size} categories")

            // Convert name to both id and name for consistency
            val entities = remote.map {
                CategoryEntity(it.name, it.name, System.currentTimeMillis())
            }

            // Cache the API categories
            categoryDao.insertAll(entities)
            Log.d(TAG, "Categories cached successfully")

            entities.map { Category(it.id, it.name) }
        } catch (e: Exception) {
            Log.e(TAG, "API fetch failed: ${e.message}", e)
            // If API fails, get from cache
            val cached = categoryDao.getAll().map { Category(it.id, it.name) }
            Log.d(TAG, "Using cached categories: ${cached.size} found")
            cached
        }

        // Step 2: Get unique categories from local courses
        val localCourseCategories = courseDao.getUniqueCategories()
            .map { Category(it, it) } // Use category name as both id and name

        Log.d(TAG, "Local course categories: ${localCourseCategories.size} found")

        // Step 3: Merge and deduplicate (API categories first, then local)
        val allCategories = (apiCategories + localCourseCategories)
            .distinctBy { it.name.lowercase() } // Case-insensitive deduplication
            .sortedBy { it.name }

        Log.d(TAG, "Total unique categories: ${allCategories.size}")
        return allCategories
    }

    /**
     * Observes all categories (from API cache + unique local course categories)
     * This flow updates automatically when either source changes
     */
    fun observeCategoriesFlow(): Flow<List<Category>> =
        combine(
            categoryDao.getAllFlow(),
            courseDao.getUniqueCategoriesFlow()
        ) { cachedCategories, localCategories ->
            // API categories from cache
            val apiCats = cachedCategories.map { Category(it.id, it.name) }

            // Categories from local courses
            val localCats = localCategories.map { Category(it, it) }

            // Merge and deduplicate
            val merged = (apiCats + localCats)
                .distinctBy { it.name.lowercase() }
                .sortedBy { it.name }

            Log.d(TAG, "Flow emitted: ${merged.size} categories")
            merged
        }
}