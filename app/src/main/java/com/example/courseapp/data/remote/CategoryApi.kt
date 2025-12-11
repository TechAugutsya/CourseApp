package com.example.courseapp.data.remote

import retrofit2.http.GET

interface CategoryApi {
    @GET(".")
    suspend fun getRoot(): Map<String, Any>
    // We'll use a custom parser since your Postman endpoint returns { "course_list": [...] } or might return array.
    @GET("categories")
    suspend fun getCategories(): List<CategoryDto>
}

data class CategoryDto(val id: String, val name: String)