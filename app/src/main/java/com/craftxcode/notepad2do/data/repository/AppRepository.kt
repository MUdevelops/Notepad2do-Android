package com.craftxcode.notepad2do.data.repository

import com.craftxcode.notepad2do.data.model.Folder
import com.craftxcode.notepad2do.data.model.Note
import com.craftxcode.notepad2do.data.model.Task
import kotlinx.coroutines.flow.Flow

interface AppRepository {
    // Folder operations
    fun getAllFolders(): Flow<List<Folder>>
    suspend fun getFolderById(id: Long): Folder?
    suspend fun insertFolder(folder: Folder): Long
    suspend fun updateFolder(folder: Folder)
    suspend fun deleteFolder(folder: Folder)

    // Note operations
    fun getAllNotes(): Flow<List<Note>>
    fun getNotesByFolder(folderId: Long): Flow<List<Note>>
    fun searchNotes(query: String): Flow<List<Note>>
    fun getTrashedNotes(): Flow<List<Note>>
    suspend fun getNoteById(id: Long): Note?
    suspend fun insertNote(note: Note): Long
    suspend fun updateNote(note: Note)
    suspend fun deleteNote(note: Note)
    suspend fun moveNoteToTrash(id: Long)
    suspend fun restoreNoteFromTrash(id: Long)
    suspend fun emptyNoteTrash()

    // Task operations
    fun getAllTasks(): Flow<List<Task>>
    fun getTasksByFolder(folderId: Long): Flow<List<Task>>
    fun searchTasks(query: String): Flow<List<Task>>
    fun getTrashedTasks(): Flow<List<Task>>
    fun getTasksByPriority(priority: Int): Flow<List<Task>>
    suspend fun getTaskById(id: Long): Task?
    suspend fun insertTask(task: Task): Long
    suspend fun updateTask(task: Task)
    suspend fun deleteTask(task: Task)
    suspend fun moveTaskToTrash(id: Long)
    suspend fun restoreTaskFromTrash(id: Long)
    suspend fun emptyTaskTrash()
}
