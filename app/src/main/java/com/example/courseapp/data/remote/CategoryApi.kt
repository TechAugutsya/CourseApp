package com.example.courseapp.data.remote

import retrofit2.http.GET

interface CategoryApi {
    @GET(".")
    suspend fun getRoot(): Map<String, Any>
    @GET("categories")
    suspend fun getCategories(): List<CategoryDto>
}

data class CategoryDto(val id: String, val name: String)