@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)

package com.example.myapplication

import android.Manifest
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeParseException
import androidx.lifecycle.viewmodel.compose.viewModel
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import com.example.myapplication.data.ExamEntity
import com.example.myapplication.presentation.ExamViewModel
import com.example.myapplication.ui.theme.ExamsDateTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import dagger.hilt.android.AndroidEntryPoint
import java.security.AllPermission
import java.util.UUID
import kotlin.random.Random

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

@OptIn(ExperimentalPermissionsApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ExamListScreen() {

    val context = LocalContext.current

    val viewModel = hiltViewModel<ExamViewModel>()
    val examUiState = viewModel.examUiState.collectAsState().value

    val showDialog = remember { mutableStateOf(false) }
    var notificationDialog = remember { mutableStateOf(false) }


    val postNotificationPermission=
        rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS)

    LaunchedEffect(key1 = true ){
        if(!postNotificationPermission.status.isGranted){
            postNotificationPermission.launchPermissionRequest()
        }
    }

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
        if (notificationDialog.value) {
            NotificationAddExamDialog(notificationDialog, examUiState.exams, context)
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 72.dp)
                    .weight(1f)
            ) {
                items(examUiState.exams) { exam ->
                    ExamItem(exam)
                }
            }

            ElevatedButton(
                onClick = { notificationDialog.value = true },
                modifier = Modifier.padding(bottom = 8.dp),
                colors = ButtonDefaults.elevatedButtonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Text(
                    text = "Bildirim Gonder",
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = Color.White
                )
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
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
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
                    errorText = "Lütfen geçerli bir tarih veya saat girin"
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
@Composable
fun NotificationAddExamDialog(
    notificationDialog: MutableState<Boolean>,
    list: List<ExamEntity>,
    context: Context,
) {

    val selectedItem = remember { mutableStateOf("") }
    val numberInput = remember { mutableStateOf("") }
    val dropdownExpanded = remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = { notificationDialog.value = false },
        title = { Text(text = "Bildirim  Gonder") },
        text = {
            Column {
                OutlinedTextField(
                    value = selectedItem.value,
                    onValueChange = { },
                    label = { Text("Ders Adi") },
                    trailingIcon = {
                        Icon(
                            Icons.Filled.ArrowDropDown,
                            "dropdown",
                            Modifier.clickable { dropdownExpanded.value = true })
                    }
                )
                DropdownMenu(
                    expanded = dropdownExpanded.value,
                    onDismissRequest = { dropdownExpanded.value = false },
                    offset = DpOffset(x = 10.dp, y = 20.dp)
                ) {
                    list.forEach { item ->
                        DropdownMenuItem(
                            onClick = {
                                selectedItem.value = item.name
                                dropdownExpanded.value = false
                            }, text = { Text(text = item.name) }
                        )
                    }
                }
                OutlinedTextField(
                    value = numberInput.value,
                    onValueChange = { numberInput.value = it },
                    label = { Text("Zaman") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    suffix = { Text("Dakika") },
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                notificationDialog.value = false
                showBasicNotification(context, "exam_channel", selectedItem.value, numberInput.value)
            }
            ) {
                Text("Gonder")
            }
        },
        dismissButton = {
            TextButton(onClick = { notificationDialog.value = false }) {
                Text("İptal")
            }
        }
    )
}

fun showBasicNotification(
    context: Context,
    channelId: String,
    title: String,
    content: String,
) {
    val notificationManager = context.getSystemService(NotificationManager::class.java)
    val notification = NotificationCompat.Builder(context, channelId)
        .setContentTitle("$title sinavi icin son cagri")
        .setContentText("$content dakika kaldi")
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setPriority(NotificationManager.IMPORTANCE_HIGH)
        .setAutoCancel(true)
        .build()

    notificationManager.notify(
        Random.nextInt(),
        notification
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
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DefaultPreview() {
    ExamsDateTheme {
        ExamListScreen()
    }
}