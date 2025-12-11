package com.example.courseapp.ui.screens


import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.courseapp.viewmodel.AddEditCourseViewModel
import com.example.courseapp.viewmodel.CategoryViewModel

@Composable
fun AddEditCourseScreen(navController: NavController, courseId: String?, vm: AddEditCourseViewModel = hiltViewModel(), catVm: CategoryViewModel = hiltViewModel()) {
    val ui by vm.ui.collectAsState()
    val cats by catVm.ui.collectAsState()
    LaunchedEffect(Unit) { vm.loadCourseIfExists(courseId) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(value = ui.title, onValueChange = vm::setTitle, label = { Text("Title") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = ui.description, onValueChange = vm::setDescription, label = { Text("Description") }, modifier = Modifier.fillMaxWidth().height(160.dp))
        Spacer(Modifier.height(8.dp))

        // Category dropdown
        var expanded by remember { mutableStateOf(false) }
        OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
            Text(text = cats.categories.firstOrNull { it.id == ui.categoryId }?.name ?: "Select category")
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            cats.categories.forEach { c ->
                DropdownMenuItem(onClick = { vm.setCategory(c.id); expanded = false }) {
                    Text(c.name)
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = ui.lessons, onValueChange = vm::setLessons, label = { Text("Number of lessons") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(12.dp))
        Row {
            Button(onClick = {
                vm.save(existingId = courseId, onSaved = { navController.popBackStack() }, onError = { /* show snackbar */ })
            }, enabled = !ui.isSaving) {
                if (ui.isSaving) CircularProgressIndicator(modifier = Modifier.size(20.dp)) else Text("Save")
            }
            Spacer(Modifier.width(8.dp))
            OutlinedButton(onClick = { navController.popBackStack() }) { Text("Cancel") }
        }
    }
}