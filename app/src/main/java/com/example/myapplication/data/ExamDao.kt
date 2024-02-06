package com.example.myapplication.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ExamDao {

    @Insert
    fun insertExam(exam: ExamEntity)

    @Query("SELECT * FROM exam")
    fun getAllExams(): List<ExamEntity>

}