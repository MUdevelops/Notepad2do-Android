package com.craftxcode.notepad2do.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.craftxcode.notepad2do.data.model.Note
import com.craftxcode.notepad2do.data.model.Task
import com.craftxcode.notepad2do.data.repository.AppRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class TrashViewModel(private val repository: AppRepository) : ViewModel() {

    val trashedNotes: StateFlow<List<Note>> = repository.getTrashedNotes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val trashedTasks: StateFlow<List<Task>> = repository.getTrashedTasks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun restoreItem(item: Any) {
        viewModelScope.launch {
            when (item) {
                is Note -> repository.restoreNoteFromTrash(item.id)
                is Task -> repository.restoreTaskFromTrash(item.id)
            }
        }
    }

    fun deletePermanently(item: Any) {
        viewModelScope.launch {
            when (item) {
                is Note -> repository.deleteNote(item)
                is Task -> repository.deleteTask(item)
            }
        }
    }

    fun emptyTrash() {
        viewModelScope.launch {
            repository.emptyNoteTrash()
            repository.emptyTaskTrash()
        }
    }
}
