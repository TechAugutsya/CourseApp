package com.example.courseapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.courseapp.data.repository.CourseRepository
import com.example.courseapp.domain.models.Course
import kotlinx.coroutines.launch

@Composable
fun CourseDetailsScreen(navController: NavController, courseId: String, repo: CourseRepository = hiltViewModel()) {
    val scope = rememberCoroutineScope()
    var course by remember { mutableStateOf<Course?>(null) }

    LaunchedEffect(courseId) {
        course = repo.getCourse(courseId)
    }

    if (course == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(course!!.title, style = MaterialTheme.typography.h5)
        Spacer(Modifier.height(8.dp))
        Text("Category: ${course!!.categoryId}")
        Spacer(Modifier.height(8.dp))
        Text("Lessons: ${course!!.lessons}")
        Spacer(Modifier.height(8.dp))
        Text("Score: ${course!!.score}")
        Spacer(Modifier.height(12.dp))
        Text(course!!.description)
        Spacer(Modifier.height(20.dp))
        Row {
            Button(onClick = { navController.navigate("edit/${course!!.id}") }) { Text("Edit") }
            Spacer(Modifier.width(8.dp))
            Button(onClick = {
                scope.launch {
                    repo.deleteCourse(course!!.id)
                    navController.popBackStack()
                }
            }) { Text("Delete") }
        }
    }
}