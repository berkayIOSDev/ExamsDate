@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.myapplication

import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeParseException
import androidx.lifecycle.viewmodel.compose.viewModel
import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import com.example.myapplication.data.ExamEntity
import com.example.myapplication.presentation.ExamViewModel
import com.example.myapplication.ui.theme.ExamsDateTheme
import dagger.hilt.android.AndroidEntryPoint
import java.util.UUID

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ExamsDateTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ExamListScreen()
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ExamListScreen() {

    val viewModel = hiltViewModel<ExamViewModel>()
    val examUiState = viewModel.examUiState.collectAsState().value

    val showDialog = remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Sınav Takip Uygulaması") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog.value = true }) {
                Icon(Icons.Filled.Add, contentDescription = "Sınav Ekle")
            }
        }
    ) {
        if (showDialog.value) {
            AddExamDialog(showDialog, viewModel::insertExam)
        }

        LazyColumn(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 72.dp)
        ) {
            items(examUiState.exams) { exam ->
                ExamItem(exam)
            }
        }
    }
}

@Composable
fun ExamItem(exam: ExamEntity) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Sınav Adı: ${exam.name}", style = MaterialTheme.typography.headlineMedium)
            Text(text = "Tarih: ${exam.date}", style = MaterialTheme.typography.bodyLarge)
            Text(text = "Saat: ${exam.hour}", style = MaterialTheme.typography.bodyLarge)
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddExamDialog(showDialog: MutableState<Boolean>, onAddExam: (ExamEntity) -> Unit) {
    var name by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var errorText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { showDialog.value = false },
        title = { Text(text = "Yeni Sınav Ekle") },
        text = {
            Column {
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Sınav Adı") })
                TextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text("Tarih (yyyy-MM-dd)") })
                TextField(
                    value = time,
                    onValueChange = { time = it },
                    label = { Text("Saat (HH:mm)") })
                if (errorText.isNotEmpty()) {
                    Text(text = errorText, color = MaterialTheme.colorScheme.error)
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                if (isValidDate(date) && isValidTime(time)) {
                    onAddExam(ExamEntity(name = name, date = date, hour = time))
                    showDialog.value = false
                    errorText = ""
                } else {
                    errorText = "Lütfen geçerli bir tarih ve saat girin"
                }
            }) {
                Text("Ekle")
            }
        },
        dismissButton = {
            Button(onClick = { showDialog.value = false }) {
                Text("İptal")
            }
        }
    )
}

@RequiresApi(Build.VERSION_CODES.O)
fun isValidDate(dateStr: String): Boolean {
    return try {
        LocalDate.parse(dateStr)
        true
    } catch (e: DateTimeParseException) {
        false
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun isValidTime(timeStr: String): Boolean {
    return try {
        LocalTime.parse(timeStr)
        true
    } catch (e: DateTimeParseException) {
        false
    }
}


@Preview
@Composable
fun DefaultPreview() {
    ExamsDateTheme {
        ExamListScreen()
    }
}