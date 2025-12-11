package com.example.courseapp


import android.R.style.Theme
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.courseapp.ui.screens.AddEditCourseScreen
import com.example.courseapp.ui.screens.CourseDetailsScreen
import com.example.courseapp.ui.screens.CourseListScreen
import com.example.courseapp.ui.theme.CourseAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CourseAppTheme { AppNav() }
        }
    }
}

@Composable
fun AppNav() {
    val nav = rememberNavController()
    NavHost(nav, startDestination = "list") {
        composable("list") { CourseListScreen(nav) }
        composable("add") { AddEditCourseScreen(nav, null) }
        composable("edit/{id}", arguments = listOf(navArgument("id") { type = NavType.StringType })) { back ->
            AddEditCourseScreen(nav, back.arguments?.getString("id"))
        }
        composable("details/{id}", arguments = listOf(navArgument("id") { type = NavType.StringType })) { back ->
            CourseDetailsScreen(nav, back.arguments?.getString("id") ?: "")
        }
    }
}
