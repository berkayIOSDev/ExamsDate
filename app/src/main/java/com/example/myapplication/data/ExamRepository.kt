package com.example.myapplication.data

import javax.inject.Inject

class ExamRepository @Inject constructor(
    private val examDao: ExamDao
) {

    fun insertExam(exam: ExamEntity) {
        examDao.insertExam(exam)
    }

     fun getAllExams(): List<ExamEntity> {
        return examDao.getAllExams()
    }
}