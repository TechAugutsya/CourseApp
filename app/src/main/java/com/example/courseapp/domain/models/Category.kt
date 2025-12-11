package com.example.courseapp.domain.models

import com.example.courseapp.data.local.entities.CategoryEntity

data class Category(val id: String, val name: String)
fun CategoryEntity.toCategory() = Category(id, name)