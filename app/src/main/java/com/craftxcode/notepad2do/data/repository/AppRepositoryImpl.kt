package com.craftxcode.notepad2do.data.repository

import com.craftxcode.notepad2do.data.local.FolderDao
import com.craftxcode.notepad2do.data.local.NoteDao
import com.craftxcode.notepad2do.data.local.TaskDao
import com.craftxcode.notepad2do.data.model.Folder
import com.craftxcode.notepad2do.data.model.Note
import com.craftxcode.notepad2do.data.model.Task
import kotlinx.coroutines.flow.Flow

class AppRepositoryImpl(
    private val folderDao: FolderDao,
    private val noteDao: NoteDao,
    private val taskDao: TaskDao
) : AppRepository {
    override fun getAllFolders(): Flow<List<Folder>> = folderDao.getAllFolders()
    override suspend fun getFolderById(id: Long): Folder? = folderDao.getFolderById(id)
    override suspend fun insertFolder(folder: Folder): Long = folderDao.insertFolder(folder)
    override suspend fun updateFolder(folder: Folder) = folderDao.updateFolder(folder)
    override suspend fun deleteFolder(folder: Folder) = folderDao.deleteFolder(folder)

    override fun getAllNotes(): Flow<List<Note>> = noteDao.getAllNotes()
    override fun getNotesByFolder(folderId: Long): Flow<List<Note>> = noteDao.getNotesByFolder(folderId)
    override fun searchNotes(query: String): Flow<List<Note>> = noteDao.searchNotes(query)
    override fun getTrashedNotes(): Flow<List<Note>> = noteDao.getTrashedNotes()
    override suspend fun getNoteById(id: Long): Note? = noteDao.getNoteById(id)
    override suspend fun insertNote(note: Note): Long = noteDao.insertNote(note)
    override suspend fun updateNote(note: Note) = noteDao.updateNote(note)
    override suspend fun deleteNote(note: Note) = noteDao.deleteNote(note)
    override suspend fun moveNoteToTrash(id: Long) = noteDao.moveToTrash(id)
    override suspend fun restoreNoteFromTrash(id: Long) = noteDao.restoreFromTrash(id)
    override suspend fun emptyNoteTrash() = noteDao.emptyTrash()

    override fun getAllTasks(): Flow<List<Task>> = taskDao.getAllTasks()
    override fun getTasksByFolder(folderId: Long): Flow<List<Task>> = taskDao.getTasksByFolder(folderId)
    override fun searchTasks(query: String): Flow<List<Task>> = taskDao.searchTasks(query)
    override fun getTrashedTasks(): Flow<List<Task>> = taskDao.getTrashedTasks()
    override fun getTasksByPriority(priority: Int): Flow<List<Task>> = taskDao.getTasksByPriority(priority)
    override suspend fun getTaskById(id: Long): Task? = taskDao.getTaskById(id)
    override suspend fun insertTask(task: Task): Long = taskDao.insertTask(task)
    override suspend fun updateTask(task: Task) = taskDao.updateTask(task)
    override suspend fun deleteTask(task: Task) = taskDao.deleteTask(task)
    override suspend fun moveTaskToTrash(id: Long) = taskDao.moveToTrash(id)
    override suspend fun restoreTaskFromTrash(id: Long) = taskDao.restoreFromTrash(id)
    override suspend fun emptyTaskTrash() = taskDao.emptyTrash()
}
