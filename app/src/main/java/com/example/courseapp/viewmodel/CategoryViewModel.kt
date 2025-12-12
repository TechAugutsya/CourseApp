package com.example.courseapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.courseapp.data.repository.CategoryRepository
import com.example.courseapp.domain.models.Category
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CategoryUiState(
    val isLoading: Boolean = true,
    val categories: List<Category> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class CategoryViewModel @Inject constructor(private val repo: CategoryRepository) : ViewModel() {

    private val _isLoading = MutableStateFlow(true)
    private val _error = MutableStateFlow<String?>(null)

    val ui: StateFlow<CategoryUiState> = combine(
        repo.observeCategoriesFlow(),
        _isLoading,
        _error
    ) { categories, isLoading, error ->
        CategoryUiState(
            isLoading = isLoading,
            categories = categories,
            error = error
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        CategoryUiState(isLoading = true)
    )

    init {
        loadFromApi()
    }

    fun loadFromApi() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                repo.getCategories()
            } catch (e: Exception) {
                _error.value = e.localizedMessage ?: "Failed to load categories"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun load() = loadFromApi()
}