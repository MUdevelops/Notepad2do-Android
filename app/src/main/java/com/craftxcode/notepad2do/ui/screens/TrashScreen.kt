package com.craftxcode.notepad2do.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.craftxcode.notepad2do.data.model.Note
import com.craftxcode.notepad2do.data.model.Task
import com.craftxcode.notepad2do.data.model.ViewType
import com.craftxcode.notepad2do.ui.components.NoteItem
import com.craftxcode.notepad2do.ui.components.TaskItem
import com.craftxcode.notepad2do.ui.viewmodel.TrashViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrashScreen(
    trashViewModel: TrashViewModel,
    onBack: () -> Unit
) {
    val trashedNotes by trashViewModel.trashedNotes.collectAsStateWithLifecycle()
    val trashedTasks by trashViewModel.trashedTasks.collectAsStateWithLifecycle()
    
    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Trash") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { trashViewModel.emptyTrash() }) {
                        Icon(Icons.Default.DeleteForever, contentDescription = "Empty Trash")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            PrimaryTabRow(selectedTabIndex = selectedTab) {
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }) {
                    Text("Notes (${trashedNotes.size})", modifier = Modifier.padding(16.dp))
                }
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }) {
                    Text("Tasks (${trashedTasks.size})", modifier = Modifier.padding(16.dp))
                }
            }
            if (selectedTab == 0) {
                TrashedNoteList(trashedNotes, { trashViewModel.restoreItem(it) }, { trashViewModel.deletePermanently(it) })
            } else {
                TrashedTaskList(trashedTasks, { trashViewModel.restoreItem(it) }, { trashViewModel.deletePermanently(it) })
            }
        }
    }
}

@Composable
fun TrashedNoteList(notes: List<Note>, onRestore: (Note) -> Unit, onDelete: (Note) -> Unit) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(notes) { note ->
            Box {
                NoteItem(note = note, viewType = ViewType.LIST, onClick = {}, showMenuIcon = false)
                Row(
                    modifier = Modifier.align(Alignment.CenterEnd).padding(end = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(onClick = { onRestore(note) }) {
                        Icon(Icons.Default.Restore, contentDescription = "Restore")
                    }
                    IconButton(onClick = { onDelete(note) }) {
                        Icon(Icons.Default.DeleteForever, contentDescription = "Delete Permanently", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}

@Composable
fun TrashedTaskList(tasks: List<Task>, onRestore: (Task) -> Unit, onDelete: (Task) -> Unit) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(tasks) { task ->
            Box {
                TaskItem(task = task, viewType = ViewType.LIST, onCheckedChange = {}, onClick = {}, showMenuIcon = false)
                Row(
                    modifier = Modifier.align(Alignment.CenterEnd).padding(end = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(onClick = { onRestore(task) }) {
                        Icon(Icons.Default.Restore, contentDescription = "Restore")
                    }
                    IconButton(onClick = { onDelete(task) }) {
                        Icon(Icons.Default.DeleteForever, contentDescription = "Delete Permanently", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}
