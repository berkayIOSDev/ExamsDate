package com.example.myapplication.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ExamEntity::class], version = 1, exportSchema = false)
abstract class ExamDatabase : RoomDatabase() {
    abstract fun examDao() : ExamDao
}