package com.example.courseapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.courseapp.data.repository.CourseRepository
import com.example.courseapp.domain.models.Course
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import javax.inject.Inject

data class CourseListUiState(
    val isLoading: Boolean = true,
    val courses: List<Course> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class CourseListViewModel @Inject constructor(
    private val repo: CourseRepository
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory

    private val _isInitialLoading = MutableStateFlow(true)

    val uiState = combine(
        repo.observeCourses(),
        _query,
        _selectedCategory,
        _isInitialLoading
    ) { allCourses, searchQuery, categoryId, isLoading ->
        val filteredCourses = allCourses
            .let { list ->
                if (searchQuery.isBlank()) list
                else list.filter {
                    it.title.contains(searchQuery, ignoreCase = true) ||
                            it.description.contains(searchQuery, ignoreCase = true)
                }
            }
            .let { list ->
                if (categoryId.isNullOrBlank()) list
                else list.filter { it.categoryId == categoryId }
            }

        CourseListUiState(
            isLoading = isLoading,
            courses = filteredCourses,
            error = null
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        CourseListUiState(isLoading = true, courses = emptyList())
    )

    init {
        viewModelScope.launch {
            delay(500)
            _isInitialLoading.value = false
        }
    }

    fun setQuery(q: String) {
        _query.value = q
    }

    fun setCategory(catId: String?) {
        _selectedCategory.value = catId
    }

    fun deleteCourse(id: String, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            repo.deleteCourse(id)
            onComplete()
        }
    }
}