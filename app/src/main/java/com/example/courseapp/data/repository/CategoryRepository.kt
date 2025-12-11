package com.example.courseapp.data.repository

import com.example.courseapp.data.local.dao.CategoryDao
import com.example.courseapp.data.local.entities.CategoryEntity
import com.example.courseapp.data.remote.CategoryApi
import com.example.courseapp.data.remote.CategoryDto
import com.example.courseapp.domain.models.Category
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CategoryRepository(
    private val api: CategoryApi,
    private val dao: CategoryDao
) {
    suspend fun getCategories(): List<Category> {
        return try {
            val remote: List<CategoryDto> = api.getCategories()
            val entities = remote.map { CategoryEntity(it.id, it.name, System.currentTimeMillis()) }
            dao.insertAll(entities)
            entities.map { Category(it.id, it.name) }
        } catch (e: Exception) {
            dao.getAll().map { Category(it.id, it.name) }
        }
    }

    fun observeCategoriesFlow(): Flow<List<Category>> =
        dao.getAllFlow().map { it.map { entity -> Category(entity.id, entity.name) } }
}