package com.example.courseapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.courseapp.data.repository.CourseRepository
import com.example.courseapp.domain.models.Course
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CourseListViewModel @Inject constructor(
    private val repo: CourseRepository
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory

    val courses = repo.observeCourses()
        .combine(_query) { list, q ->
            if (q.isBlank()) list else list.filter { it.title.contains(q, ignoreCase = true) || it.description.contains(q, ignoreCase = true) }
        }.combine(_selectedCategory) { list, cat ->
            if (cat.isNullOrBlank()) list else list.filter { it.categoryId == cat }
        }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun setQuery(q: String) { _query.value = q }
    fun setCategory(catId: String?) { _selectedCategory.value = catId }

    fun deleteCourse(id: String, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            repo.deleteCourse(id)
            onComplete()
        }
    }
}