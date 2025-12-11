package com.example.courseapp.data.local.db

import android.content.Context
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.courseapp.data.local.dao.CategoryDao
import com.example.courseapp.data.local.dao.CourseDao
import com.example.courseapp.data.local.entities.CategoryEntity
import com.example.courseapp.data.local.entities.CourseEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.*

@Database(entities = [CourseEntity::class, CategoryEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun courseDao(): CourseDao
    abstract fun categoryDao(): CategoryDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "courses_db"
                ).addCallback(DatabaseCallback(scope)).build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback(private val scope: CoroutineScope) : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            // Prepopulate with the 10 courses you provided and no categories (categories come from API)
            INSTANCE?.let { database ->
                scope.launch {
                    val courseDao = database.courseDao()
                    val now = System.currentTimeMillis()
                    val pre = listOf(
                        CourseEntity("1","Kotlin is very good for Beginners","A complete introduction to Kotlin programming covering variables, functions, classes, collections, and object-oriented design. Ideal for new Android developers who want a strong foundation.","1",15,255,1710000000000,1710005000000),
                        CourseEntity("2","Advanced Jetpack Compose","A deep dive into Jetpack Compose including state management, animations, navigation, performance optimization, and building scalable UI architectures.","1",20,400,1710100000000,1710107000000),
                        CourseEntity("3","UI/UX Fundamentals","Learn the principles of design, color psychology, spacing, alignment, typography, hierarchy, and how to create intuitive and beautiful user interfaces.","2",12,180,1710200000000,null),
                        CourseEntity("4","Mastering Android Architecture","A complete guide to MVVM, Repository pattern, UseCases, DI with Hilt, Room, and best practices for building scalable, maintainable Android applications.","1",18,432,1710300000000,1710312000000),
                        CourseEntity("5","Business Strategy Essentials","Understand how companies build long-term strategies, analyze competitors, define market positions, and create sustainable business models.","3",10,250,1710400000000,null),
                        CourseEntity("6","Marketing in the Digital Age","A modern course on digital marketing covering SEO, social media marketing, content strategies, analytics, conversion optimization, and brand communication.","4",14,308,1710500000000,null),
                        CourseEntity("7","Full-Stack Android Development","Learn how to build full-stack Android apps with REST APIs, authentication, networking, Room database, offline-first architecture, and cloud integration.","1",22,506,1710600000000,1710610000000),
                        CourseEntity("8","Creative Graphic Design","Explore advanced design techniques, Adobe tools, color grading, poster design, creative composition, and brand identity creation.","2",16,352,1710700000000,null),
                        CourseEntity("9","Project Management Basics","Learn task planning, risk management, team coordination, Agile methodologies, Scrum frameworks, and efficient project delivery techniques.","3",11,253,1710800000000,1710810000000),
                        CourseEntity("10","Jetpack Navigation Mastery","A hands-on course explaining Android Navigation component, multi-module navigation, deep linking, back stack control, and best practices for routing.","1",13,273,1710900000000,null)
                    )
                    pre.forEach { courseDao.insert(it) }
                }
            }
        }
    }
}