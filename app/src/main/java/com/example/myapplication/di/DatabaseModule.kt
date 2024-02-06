package com.example.myapplication.di

import android.content.Context
import androidx.room.Room
import com.example.myapplication.data.ExamDao
import com.example.myapplication.data.ExamDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): ExamDatabase {
        return Room.databaseBuilder(
            context,
            ExamDatabase::class.java,
            "exam_database.db"
        ).allowMainThreadQueries().fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideNoteDao(noteDatabase: ExamDatabase): ExamDao {
        return noteDatabase.examDao()
    }

}