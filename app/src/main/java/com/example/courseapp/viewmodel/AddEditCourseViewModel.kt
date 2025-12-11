package com.example.courseapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.courseapp.data.repository.CategoryRepository
import com.example.courseapp.data.repository.CourseRepository
import com.example.courseapp.domain.models.Course
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class AddEditUiState(
    val title: String = "",
    val description: String = "",
    val categoryId: String? = null,
    val lessons: String = "1",
    val isSaving: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class AddEditCourseViewModel @Inject constructor(
    private val courseRepo: CourseRepository,
    private val categoryRepo: CategoryRepository
) : ViewModel() {

    private val _ui = MutableStateFlow(AddEditUiState())
    val ui: StateFlow<AddEditUiState> = _ui

    fun loadCourseIfExists(id: String?) {
        if (id == null) return
        viewModelScope.launch {
            val c = courseRepo.getCourse(id)
            c?.let {
                _ui.value = _ui.value.copy(
                    title = it.title,
                    description = it.description,
                    categoryId = it.categoryId,
                    lessons = it.lessons.toString()
                )
            }
        }
    }

    fun setTitle(s: String) { _ui.value = _ui.value.copy(title = s) }
    fun setDescription(s: String) { _ui.value = _ui.value.copy(description = s) }
    fun setCategory(id: String) { _ui.value = _ui.value.copy(categoryId = id) }
    fun setLessons(s: String) { _ui.value = _ui.value.copy(lessons = s) }

    private fun calcScore(title: String, lessons: Int) = title.length * lessons

    fun save(existingId: String? = null, onSaved: () -> Unit = {}, onError: (String) -> Unit = {}) {
        viewModelScope.launch {
            val state = _ui.value
            val title = state.title.trim()
            val desc = state.description.trim()
            val lessons = state.lessons.toIntOrNull() ?: 0
            val cat = state.categoryId

            if (title.isEmpty()) { onError("Title is required"); return@launch }
            if (desc.isEmpty()) { onError("Description is required"); return@launch }
            if (cat.isNullOrBlank()) { onError("Category required"); return@launch }
            if (lessons <= 0) { onError("Lessons must be > 0"); return@launch }

            _ui.value = _ui.value.copy(isSaving = true)
            val score = calcScore(title, lessons)
            val now = System.currentTimeMillis()
            val course = Course(
                id = existingId ?: UUID.randomUUID().toString(),
                title = title,
                description = desc,
                categoryId = cat,
                lessons = lessons,
                score = score,
                createdAt = now,
                updatedAt = now
            )
            try {
                if (existingId == null) courseRepo.addCourse(course) else courseRepo.updateCourse(course)
                onSaved()
            } catch (e: Exception) {
                onError(e.localizedMessage ?: "Failed to save")
            } finally {
                _ui.value = _ui.value.copy(isSaving = false)
            }
        }
    }
}