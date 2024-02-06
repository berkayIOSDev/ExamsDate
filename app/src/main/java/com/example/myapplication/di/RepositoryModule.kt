package com.example.myapplication.di

import com.example.myapplication.data.ExamDao
import com.example.myapplication.data.ExamRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideExamRepository(examDao: ExamDao): ExamRepository {
        return ExamRepository(examDao)
    }

}