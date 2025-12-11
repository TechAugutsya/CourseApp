package com.example.courseapp.di

import android.content.Context
import com.example.courseapp.data.local.db.AppDatabase
import com.example.courseapp.data.remote.RetrofitProvider
import com.example.courseapp.data.repository.CategoryRepository
import com.example.courseapp.data.repository.CourseRepository
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext ctx: Context) = AppDatabase.getInstance(ctx, kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO))

    @Provides
    fun provideCourseDao(db: AppDatabase) = db.courseDao()

    @Provides
    fun provideCategoryDao(db: AppDatabase) = db.categoryDao()

    @Provides
    @Singleton
    fun provideCategoryApi() = RetrofitProvider.categoryApi

    @Provides
    @Singleton
    fun provideCourseRepository(dao: com.example.courseapp.data.local.dao.CourseDao) = CourseRepository(dao)

    @Provides
    @Singleton
    fun provideCategoryRepository(api: com.example.courseapp.data.remote.CategoryApi, dao: com.example.courseapp.data.local.dao.CategoryDao) =
        CategoryRepository(api, dao)
}