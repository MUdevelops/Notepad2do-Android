package com.craftxcode.notepad2do.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.craftxcode.notepad2do.data.model.Note
import com.craftxcode.notepad2do.ui.components.ColorPicker
import com.craftxcode.notepad2do.ui.viewmodel.FolderViewModel
import com.craftxcode.notepad2do.ui.viewmodel.NoteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditScreen(
    noteId: Long,
    noteViewModel: NoteViewModel,
    folderViewModel: FolderViewModel,
    onBack: () -> Unit
) {
    val notes by noteViewModel.notes.collectAsStateWithLifecycle()
    val folders by folderViewModel.allFolders.collectAsStateWithLifecycle()

    val existingNote = remember(noteId, notes) {
        notes.find { it.id == noteId }
    }

    var title by remember(existingNote) { mutableStateOf(existingNote?.title ?: "") }
    var content by remember(existingNote) { mutableStateOf(existingNote?.content ?: "") }
    var color by remember(existingNote) { mutableStateOf(existingNote?.color ?: Color.White.toArgb()) }
    var folderId by remember(existingNote) { mutableStateOf(existingNote?.folderId ?: folders.firstOrNull()?.id ?: 0L) }
    var isPinned by remember(existingNote) { mutableStateOf(existingNote?.isPinned ?: false) }
    var tags by remember(existingNote) { mutableStateOf(existingNote?.tags?.joinToString(", ") ?: "") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (noteId == -1L) "New Note" else "Edit Note") },
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
                            val note = Note(
                                id = if (noteId == -1L) 0L else noteId,
                                title = title,
                                content = content,
                                color = color,
                                folderId = folderId,
                                isPinned = isPinned,
                                tags = tags.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                            )
                            noteViewModel.upsertNote(note)
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
        ) {
            TextField(
                value = title,
                onValueChange = { title = it },
                placeholder = { Text("Title") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                textStyle = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = content,
                onValueChange = { content = it },
                placeholder = { Text("Content") },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text("Folder", style = MaterialTheme.typography.titleSmall)
            var expanded by remember { mutableStateOf(false) }
            Box {
                OutlinedButton(onClick = { expanded = true }) {
                    Text(folders.find { it.id == folderId }?.name ?: "Select Folder")
                }
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    folders.forEach { folder ->
                        DropdownMenuItem(
                            text = { Text(folder.name) },
                            onClick = {
                                folderId = folder.id
                                expanded = false
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
