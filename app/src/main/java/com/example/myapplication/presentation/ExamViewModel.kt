package com.example.myapplication.presentation

import androidx.lifecycle.ViewModel
import com.example.myapplication.data.ExamEntity
import com.example.myapplication.data.ExamRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ExamViewModel @Inject constructor(
    private val repository: ExamRepository,
) : ViewModel() {

    private val _examUiState = MutableStateFlow(ExamUiState())
    val examUiState = _examUiState.asStateFlow()

    init {
        getAllExams()
    }

    fun insertExam(exam: ExamEntity) {
        repository.insertExam(exam)
        this.getAllExams()
    }

    private fun getAllExams() {
        _examUiState.value = _examUiState.value.copy(
            exams = repository.getAllExams()
        )
    }

}

data class ExamUiState(
    val exams: List<ExamEntity> = emptyList(),
)