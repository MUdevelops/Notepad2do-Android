package com.craftxcode.notepad2do.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.craftxcode.notepad2do.data.model.Folder
import com.craftxcode.notepad2do.data.model.Note
import com.craftxcode.notepad2do.data.model.SortOrder
import com.craftxcode.notepad2do.data.model.Task
import com.craftxcode.notepad2do.data.model.ViewType
import com.craftxcode.notepad2do.ui.components.*
import com.craftxcode.notepad2do.ui.viewmodel.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    noteViewModel: NoteViewModel,
    taskViewModel: TaskViewModel,
    folderViewModel: FolderViewModel,
    settingsViewModel: SettingsViewModel,
    onNoteClick: (Long) -> Unit,
    onTaskClick: (Long) -> Unit,
    onTrashClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    val notes by noteViewModel.notes.collectAsStateWithLifecycle()
    val tasks by taskViewModel.tasks.collectAsStateWithLifecycle()
    val folders by folderViewModel.allFolders.collectAsStateWithLifecycle()
    val viewType by settingsViewModel.viewType.collectAsStateWithLifecycle()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedTab by remember { mutableStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedFolderId by remember { mutableStateOf<Long?>(null) }
    var showAddFolderDialog by remember { mutableStateOf(false) }
    var newFolderName by remember { mutableStateOf("") }

    if (showAddFolderDialog) {
        AlertDialog(
            onDismissRequest = { showAddFolderDialog = false },
            title = { Text("New Folder") },
            text = {
                TextField(
                    value = newFolderName,
                    onValueChange = { newFolderName = it },
                    placeholder = { Text("Folder Name") }
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (newFolderName.isNotBlank()) {
                        folderViewModel.insertFolder(
                            Folder(
                                name = newFolderName,
                                icon = "folder",
                                color = Color.Gray.toArgb()
                            )
                        )
                        newFolderName = ""
                        showAddFolderDialog = false
                    }
                }) { Text("Create") }
            },
            dismissButton = {
                TextButton(onClick = { showAddFolderDialog = false }) { Text("Cancel") }
            }
        )
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Folders", style = MaterialTheme.typography.titleLarge)
                    IconButton(onClick = { showAddFolderDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Folder")
                    }
                }
                NavigationDrawerItem(
                    label = { Text("All Notes") },
                    selected = selectedFolderId == null,
                    onClick = {
                        selectedFolderId = null
                        noteViewModel.setFolderFilter(null)
                        taskViewModel.setFolderFilter(null)
                        scope.launch { drawerState.close() }
                    },
                    icon = { Icon(Icons.AutoMirrored.Filled.Notes, contentDescription = null) }
                )
                folders.forEach { folder ->
                    NavigationDrawerItem(
                        label = { Text(folder.name) },
                        selected = selectedFolderId == folder.id,
                        onClick = {
                            selectedFolderId = folder.id
                            noteViewModel.setFolderFilter(folder.id)
                            taskViewModel.setFolderFilter(folder.id)
                            scope.launch { drawerState.close() }
                        },
                        icon = { Icon(Icons.Default.Folder, contentDescription = null) }
                    )
                }
                HorizontalDivider()
                NavigationDrawerItem(
                    label = { Text("Trash") },
                    selected = false,
                    onClick = {
                        onTrashClick()
                        scope.launch { drawerState.close() }
                    },
                    icon = { Icon(Icons.Default.Delete, contentDescription = null) }
                )
                NavigationDrawerItem(
                    label = { Text("Settings") },
                    selected = false,
                    onClick = {
                        onSettingsClick()
                        scope.launch { drawerState.close() }
                    },
                    icon = { Icon(Icons.Default.Settings, contentDescription = null) }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Notepad2do") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            settingsViewModel.updateViewType(
                                if (viewType == ViewType.LIST) ViewType.GRID else ViewType.LIST
                            )
                        }) {
                            Icon(
                                if (viewType == ViewType.LIST) Icons.Default.GridView else Icons.AutoMirrored.Filled.ViewList,
                                contentDescription = "Toggle View"
                            )
                        }
                        var sortMenuExpanded by remember { mutableStateOf(false) }
                        IconButton(onClick = { sortMenuExpanded = true }) {
                            Icon(Icons.AutoMirrored.Filled.Sort, contentDescription = "Sort")
                        }
                        DropdownMenu(expanded = sortMenuExpanded, onDismissRequest = { sortMenuExpanded = false }) {
                            DropdownMenuItem(
                                text = { Text("Date Created") },
                                onClick = {
                                    noteViewModel.setSortOrder(SortOrder.DATE_CREATED)
                                    taskViewModel.setSortOrder(SortOrder.DATE_CREATED)
                                    settingsViewModel.updateSortOrder(SortOrder.DATE_CREATED)
                                    sortMenuExpanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Date Modified") },
                                onClick = {
                                    noteViewModel.setSortOrder(SortOrder.DATE_MODIFIED)
                                    taskViewModel.setSortOrder(SortOrder.DATE_MODIFIED)
                                    settingsViewModel.updateSortOrder(SortOrder.DATE_MODIFIED)
                                    sortMenuExpanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Title") },
                                onClick = {
                                    noteViewModel.setSortOrder(SortOrder.TITLE)
                                    taskViewModel.setSortOrder(SortOrder.TITLE)
                                    settingsViewModel.updateSortOrder(SortOrder.TITLE)
                                    sortMenuExpanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Priority") },
                                onClick = {
                                    noteViewModel.setSortOrder(SortOrder.PRIORITY)
                                    taskViewModel.setSortOrder(SortOrder.PRIORITY)
                                    settingsViewModel.updateSortOrder(SortOrder.PRIORITY)
                                    sortMenuExpanded = false
                                }
                            )
                        }
                    }
                )
            },
            floatingActionButton = {
                NotepadFAB(
                    onAddNote = { onNoteClick(-1L) },
                    onAddTask = { onTaskClick(-1L) }
                )
            }
        ) { padding ->
            Column(modifier = Modifier.padding(padding)) {
                SearchBar(
                    query = searchQuery,
                    onQueryChange = {
                        searchQuery = it
                        noteViewModel.searchNotes(it)
                        taskViewModel.searchTasks(it)
                    }
                )
                PrimaryTabRow(selectedTabIndex = selectedTab) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Notes") },
                        icon = { Icon(Icons.AutoMirrored.Filled.Notes, contentDescription = null) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Tasks") },
                        icon = { Icon(Icons.Default.Checklist, contentDescription = null) }
                    )
                }
                if (selectedTab == 0) {
                    NoteList(
                        notes = notes,
                        viewType = viewType,
                        onNoteClick = onNoteClick,
                        onPinClick = { noteViewModel.pinNote(it) },
                        onDeleteClick = { noteViewModel.moveNoteToTrash(it.id) },
                        onDuplicateClick = { noteViewModel.upsertNote(it.copy(id = 0L, isPinned = false)) }
                    )
                } else {
                    TaskList(
                        tasks = tasks,
                        viewType = viewType,
                        onTaskClick = onTaskClick,
                        onCheckedChange = { taskViewModel.toggleTaskCompletion(it) },
                        onPinClick = { taskViewModel.pinTask(it) },
                        onDeleteClick = { taskViewModel.moveTaskToTrash(it.id) },
                        onDuplicateClick = { taskViewModel.upsertTask(it.copy(id = 0L, isPinned = false)) }
                    )
                }
            }
        }
    }
}

@Composable
fun NoteList(
    notes: List<Note>,
    viewType: ViewType,
    onNoteClick: (Long) -> Unit,
    onPinClick: (Note) -> Unit,
    onDeleteClick: (Note) -> Unit,
    onDuplicateClick: (Note) -> Unit
) {
    if (viewType == ViewType.LIST) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(notes) { note ->
                NoteItem(
                    note = note,
                    viewType = viewType,
                    onClick = { onNoteClick(note.id) },
                    onPinClick = { onPinClick(note) },
                    onDeleteClick = { onDeleteClick(note) },
                    onDuplicateClick = { onDuplicateClick(note) }
                )
            }
        }
    } else {
        LazyVerticalGrid(columns = GridCells.Fixed(2), modifier = Modifier.fillMaxSize()) {
            items(notes) { note ->
                NoteItem(
                    note = note,
                    viewType = viewType,
                    onClick = { onNoteClick(note.id) },
                    onPinClick = { onPinClick(note) },
                    onDeleteClick = { onDeleteClick(note) },
                    onDuplicateClick = { onDuplicateClick(note) }
                )
            }
        }
    }
}

@Composable
fun TaskList(
    tasks: List<Task>,
    viewType: ViewType,
    onTaskClick: (Long) -> Unit,
    onCheckedChange: (Task) -> Unit,
    onPinClick: (Task) -> Unit,
    onDeleteClick: (Task) -> Unit,
    onDuplicateClick: (Task) -> Unit
) {
    if (viewType == ViewType.LIST) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(tasks) { task ->
                TaskItem(
                    task = task,
                    viewType = viewType,
                    onCheckedChange = { onCheckedChange(task) },
                    onClick = { onTaskClick(task.id) },
                    onPinClick = { onPinClick(task) },
                    onDeleteClick = { onDeleteClick(task) },
                    onDuplicateClick = { onDuplicateClick(task) }
                )
            }
        }
    } else {
        LazyVerticalGrid(columns = GridCells.Fixed(2), modifier = Modifier.fillMaxSize()) {
            items(tasks) { task ->
                TaskItem(
                    task = task,
                    viewType = viewType,
                    onCheckedChange = { onCheckedChange(task) },
                    onClick = { onTaskClick(task.id) },
                    onPinClick = { onPinClick(task) },
                    onDeleteClick = { onDeleteClick(task) },
                    onDuplicateClick = { onDuplicateClick(task) }
                )
            }
        }
    }
}

