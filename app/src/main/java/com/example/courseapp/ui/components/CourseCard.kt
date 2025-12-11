package com.example.courseapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.courseapp.domain.models.Course

@Composable
fun CourseCard(course: Course, onClick: () -> Unit, onEdit: () -> Unit, onDelete: () -> Unit, categoryName: String?) {
    Card(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 6.dp)
        .clickable { onClick() }) {
        Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Column(modifier = Modifier.weight(1f)) {
                Text(course.title, style = MaterialTheme.typography.h6)
                Spacer(Modifier.height(4.dp))
                Text(course.description, maxLines = 3, style = MaterialTheme.typography.body2)
                Spacer(Modifier.height(6.dp))
                Text(categoryName ?: "Unknown", style = MaterialTheme.typography.caption)
            }
            Column(horizontalAlignment = androidx.compose.ui.Alignment.End, modifier = Modifier.padding(start = 8.dp)) {
                Text("Score", style = MaterialTheme.typography.caption)
                Text("${course.score}", style = MaterialTheme.typography.h6)
                Spacer(Modifier.height(8.dp))
                IconButton(onClick = onEdit) { Icon(Icons.Default.Edit, contentDescription = "Edit") }
                IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, contentDescription = "Delete") }
            }
        }
    }
}