package com.example.courseapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.courseapp.ui.components.CourseCard
import com.example.courseapp.ui.components.EmptyState
import com.example.courseapp.viewmodel.CourseListViewModel
import com.example.courseapp.viewmodel.CategoryViewModel

@Composable
fun CourseListScreen(navController: NavController,
                     vm: CourseListViewModel = hiltViewModel(),
                     cVm: CategoryViewModel = hiltViewModel()) {

    val courses by vm.courses.collectAsState()
    val catState by cVm.ui.collectAsState()

    var showConfirmFor by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {
        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(value = vm.query.collectAsState().value, onValueChange = { vm.setQuery(it) }, modifier = Modifier.weight(1f), placeholder = { Text("Search...") })
            Spacer(Modifier.width(8.dp))
            Button(onClick = { navController.navigate("add") }) { Text("Add") }
        }
        Spacer(Modifier.height(8.dp))
        // Category filter
        val categories = catState.categories
        var expanded by remember { mutableStateOf(false) }
        Box {
            OutlinedButton(onClick = { expanded = true }) {
                Text(text = categories.firstOrNull { it.id == vm.selectedCategory.collectAsState().value }?.name ?: "All")
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                DropdownMenuItem(onClick = { vm.setCategory(null); expanded = false }) { Text("All") }
                categories.forEach { c ->
                    DropdownMenuItem(onClick = { vm.setCategory(c.id); expanded = false }) { Text(c.name) }
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        when {
            courses.isEmpty() -> EmptyState(message = "No courses yet", actionText = "Add Course") { navController.navigate("add") }
            else -> {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(courses) { course ->
                        val catName = categories.firstOrNull { it.id == course.categoryId }?.name
                        CourseCard(course = course, onClick = { navController.navigate("details/${course.id}") }, onEdit = { navController.navigate("edit/${course.id}") }, onDelete = { showConfirmFor = course.id }, categoryName = catName)
                    }
                }
            }
        }
    }

    if (showConfirmFor != null) {
        AlertDialog(
            onDismissRequest = { showConfirmFor = null },
            title = { Text("Delete Course") },
            text = { Text("Are you sure you want to delete this course? This cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    vm.deleteCourse(showConfirmFor!!) { showConfirmFor = null }
                }) { Text("Delete") }
            },
            dismissButton = { TextButton(onClick = { showConfirmFor = null }) { Text("Cancel") } }
        )
    }
}