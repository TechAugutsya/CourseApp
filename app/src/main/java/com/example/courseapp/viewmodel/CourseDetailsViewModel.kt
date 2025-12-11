package com.example.courseapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.courseapp.data.repository.CourseRepository
import com.example.courseapp.domain.models.Course
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CourseDetailsViewModel @Inject constructor(
    private val repository: CourseRepository
) : ViewModel() {

    private val _course = MutableStateFlow<Course?>(null)
    val course: StateFlow<Course?> = _course

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun loadCourse(courseId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _course.value = repository.getCourse(courseId)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteCourse(courseId: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.deleteCourse(courseId)
            onComplete()
        }
    }
}
