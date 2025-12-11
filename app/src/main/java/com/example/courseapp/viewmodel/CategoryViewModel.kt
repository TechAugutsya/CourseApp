package com.example.courseapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.courseapp.data.repository.CategoryRepository
import com.example.courseapp.domain.models.Category
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CategoryUiState(val isLoading: Boolean = false, val categories: List<Category> = emptyList(), val error: String? = null)

@HiltViewModel
class CategoryViewModel @Inject constructor(private val repo: CategoryRepository) : ViewModel() {
    private val _ui = MutableStateFlow(CategoryUiState())
    val ui: StateFlow<CategoryUiState> = _ui

    init { load() }

    fun load() {
        viewModelScope.launch {
            _ui.value = _ui.value.copy(isLoading = true, error = null)
            try {
                val list = repo.getCategories()
                _ui.value = _ui.value.copy(isLoading = false, categories = list)
            } catch (e: Exception) {
                _ui.value = _ui.value.copy(isLoading = false, error = e.localizedMessage ?: "Error")
            }
        }
    }
}