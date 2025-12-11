package com.example.courseapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.courseapp.ui.components.*
import com.example.courseapp.viewmodel.CourseListViewModel
import com.example.courseapp.viewmodel.CategoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseListScreen(
    navController: NavController,
    vm: CourseListViewModel = hiltViewModel(),
    cVm: CategoryViewModel = hiltViewModel()
) {
    val uiState by vm.uiState.collectAsState()
    val catState by cVm.ui.collectAsState()
    val searchQuery by vm.query.collectAsState()
    val selectedCategory by vm.selectedCategory.collectAsState()

    var showConfirmFor by remember { mutableStateOf<String?>(null) }
    var showFilterSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "My Courses",
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                actions = {
                    // Filter button
                    IconButton(onClick = { showFilterSheet = true }) {
                        Text(
                            text = "🔍",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { navController.navigate("add") },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Add Course") },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { vm.setQuery(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                placeholder = { Text("Search courses...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )

            // Active filter chip
            selectedCategory?.let { category ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Filter:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    FilterChip(
                        selected = true,
                        onClick = { vm.setCategory(null) },
                        label = { Text(category) },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Remove filter",
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    )
                }
                Spacer(Modifier.height(8.dp))
            }

            // Content
            when {
                uiState.isLoading -> {
                    LoadingScreen()
                }

                uiState.courses.isEmpty() -> {
                    if (searchQuery.isNotBlank() || selectedCategory != null) {
                        NoResultsEmptyState(searchQuery)
                    } else {
                        EmptyState(
                            message = "No courses yet",
                            subtitle = "Start building your course library",
                            actionText = "Add Your First Course",
                            onAction = { navController.navigate("add") },
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null,
                                    modifier = Modifier.size(64.dp),
                                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                                )
                            }
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 88.dp)
                    ) {
                        items(uiState.courses, key = { it.id }) { course ->
                            // Category ID is now the category name itself
                            CourseCard(
                                course = course,
                                onClick = { navController.navigate("details/${course.id}") },
                                onEdit = { navController.navigate("edit/${course.id}") },
                                onDelete = { showConfirmFor = course.id },
                                categoryName = course.categoryId
                            )
                        }
                    }
                }
            }
        }
    }

    // Filter Bottom Sheet
    if (showFilterSheet) {
        ModalBottomSheet(
            onDismissRequest = { showFilterSheet = false }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Filter by Category",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // All categories option
                FilterChip(
                    selected = selectedCategory == null,
                    onClick = {
                        vm.setCategory(null)
                        showFilterSheet = false
                    },
                    label = { Text("All Categories") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                )

                // Individual categories
                catState.categories.forEach { category ->
                    FilterChip(
                        selected = selectedCategory == category.name,
                        onClick = {
                            vm.setCategory(category.name)
                            showFilterSheet = false
                        },
                        label = { Text(category.name) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    )
                }

                Spacer(Modifier.height(16.dp))
            }
        }
    }

    // Delete Confirmation Dialog
    if (showConfirmFor != null) {
        AlertDialog(
            onDismissRequest = { showConfirmFor = null },
            icon = {
                Text(
                    text = "⚠️",
                    style = MaterialTheme.typography.headlineMedium
                )
            },
            title = {
                Text(
                    text = "Delete Course?",
                    style = MaterialTheme.typography.headlineSmall
                )
            },
            text = {
                Text(
                    text = "This action cannot be undone. The course will be permanently deleted.",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        vm.deleteCourse(showConfirmFor!!) { showConfirmFor = null }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmFor = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}