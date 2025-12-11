package com.example.courseapp.data.remote

import com.squareup.moshi.Moshi
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object RetrofitProvider {
    private const val BASE = "https://8dba06d5-9a21-4376-bd5c-e9221fd1b620.mock.pstmn.io/"

    private val moshi = Moshi.Builder().build()

    val categoryApi: CategoryApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(CategoryApi::class.java)
    }
}