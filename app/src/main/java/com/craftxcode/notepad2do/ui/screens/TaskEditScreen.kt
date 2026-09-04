package com.craftxcode.notepad2do.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.craftxcode.notepad2do.data.model.Task
import com.craftxcode.notepad2do.ui.components.ColorPicker
import com.craftxcode.notepad2do.ui.components.PriorityIcon
import com.craftxcode.notepad2do.ui.viewmodel.FolderViewModel
import com.craftxcode.notepad2do.ui.viewmodel.TaskViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskEditScreen(
    taskId: Long,
    taskViewModel: TaskViewModel,
    folderViewModel: FolderViewModel,
    onBack: () -> Unit
) {
    val tasks by taskViewModel.tasks.collectAsStateWithLifecycle()
    val folders by folderViewModel.allFolders.collectAsStateWithLifecycle()

    val existingTask = remember(taskId, tasks) {
        tasks.find { it.id == taskId }
    }

    var title by remember(existingTask) { mutableStateOf(existingTask?.title ?: "") }
    var description by remember(existingTask) { mutableStateOf(existingTask?.description ?: "") }
    var color by remember(existingTask) { mutableStateOf(existingTask?.color ?: Color.White.toArgb()) }
    var folderId by remember(existingTask) { mutableStateOf(existingTask?.folderId ?: folders.firstOrNull()?.id ?: 0L) }
    var isPinned by remember(existingTask) { mutableStateOf(existingTask?.isPinned ?: false) }
    var priority by remember(existingTask) { mutableStateOf(existingTask?.priority ?: 1) }
    var dueDate by remember(existingTask) { mutableStateOf(existingTask?.dueDate) }
    var isCompleted by remember(existingTask) { mutableStateOf(existingTask?.isCompleted ?: false) }
    var tags by remember(existingTask) { mutableStateOf(existingTask?.tags?.joinToString(", ") ?: "") }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = dueDate ?: System.currentTimeMillis()
    )
    var showDatePicker by remember { mutableStateOf(false) }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    dueDate = datePickerState.selectedDateMillis
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (taskId == -1L) "New Task" else "Edit Task") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { isPinned = !isPinned }) {
                        Icon(
                            if (isPinned) Icons.Default.PushPin else Icons.Outlined.PushPin,
                            contentDescription = "Pin"
                        )
                    }
                    Button(
                        onClick = {
                            val task = Task(
                                id = if (taskId == -1L) 0L else taskId,
                                title = title,
                                description = description,
                                color = color,
                                folderId = folderId,
                                isPinned = isPinned,
                                priority = priority,
                                dueDate = dueDate,
                                isCompleted = isCompleted,
                                tags = tags.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                            )
                            taskViewModel.upsertTask(task)
                            onBack()
                        },
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text("Save")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = isCompleted, onCheckedChange = { isCompleted = it })
                TextField(
                    value = title,
                    onValueChange = { title = it },
                    placeholder = { Text("Task Title") },
                    modifier = Modifier.weight(1f),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    textStyle = MaterialTheme.typography.headlineSmall
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = description,
                onValueChange = { description = it },
                placeholder = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            Text("Priority", style = MaterialTheme.typography.titleSmall)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(0, 1, 2).forEach { p ->
                    FilterChip(
                        selected = priority == p,
                        onClick = { priority = p },
                        label = { Text(when(p) { 0 -> "Low"; 1 -> "Medium"; else -> "High" }) },
                        leadingIcon = { PriorityIcon(p, Modifier.size(18.dp)) }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            Text("Due Date", style = MaterialTheme.typography.titleSmall)
            OutlinedButton(
                onClick = { showDatePicker = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.CalendarToday, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    dueDate?.let {
                        SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(it))
                    } ?: "No Due Date"
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Folder", style = MaterialTheme.typography.titleSmall)
            var folderExpanded by remember { mutableStateOf(false) }
            Box {
                OutlinedButton(onClick = { folderExpanded = true }) {
                    Text(folders.find { it.id == folderId }?.name ?: "Select Folder")
                }
                DropdownMenu(expanded = folderExpanded, onDismissRequest = { folderExpanded = false }) {
                    folders.forEach { folder ->
                        DropdownMenuItem(
                            text = { Text(folder.name) },
                            onClick = {
                                folderId = folder.id
                                folderExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Tags (comma separated)", style = MaterialTheme.typography.titleSmall)
            TextField(
                value = tags,
                onValueChange = { tags = it },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            Text("Color", style = MaterialTheme.typography.titleSmall)
            ColorPicker(
                selectedColor = color,
                onColorSelected = { color = it }
            )
        }
    }
}
