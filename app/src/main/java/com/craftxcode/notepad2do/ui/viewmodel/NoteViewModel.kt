package com.craftxcode.notepad2do.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.craftxcode.notepad2do.data.model.Note
import com.craftxcode.notepad2do.data.model.SortOrder
import com.craftxcode.notepad2do.data.repository.AppRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class NoteViewModel(private val repository: AppRepository) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _folderFilter = MutableStateFlow<Long?>(null)
    private val _sortOrder = MutableStateFlow(SortOrder.DATE_MODIFIED)

    val notes: StateFlow<List<Note>> = combine(
        repository.getAllNotes(),
        _searchQuery,
        _folderFilter,
        _sortOrder
    ) { notes, query, folderId, sortOrder ->
        notes.filter { note ->
            (folderId == null || note.folderId == folderId) &&
            (note.title.contains(query, ignoreCase = true) || note.content.contains(query, ignoreCase = true))
        }.sortedWith(getComparator(sortOrder))
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private fun getComparator(sortOrder: SortOrder): Comparator<Note> {
        return when (sortOrder) {
            SortOrder.DATE_MODIFIED -> compareByDescending<Note> { it.isPinned }.thenByDescending { it.modifiedAt }
            SortOrder.DATE_CREATED -> compareByDescending<Note> { it.isPinned }.thenByDescending { it.createdAt }
            SortOrder.TITLE -> compareByDescending<Note> { it.isPinned }.thenBy { it.title.lowercase() }
            SortOrder.PRIORITY -> compareByDescending<Note> { it.isPinned }.thenByDescending { it.modifiedAt } // Notes don't have priority
        }
    }

    fun upsertNote(note: Note) {
        viewModelScope.launch {
            if (note.id == 0L) {
                repository.insertNote(note)
            } else {
                repository.updateNote(note.copy(modifiedAt = System.currentTimeMillis()))
            }
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            repository.deleteNote(note)
        }
    }

    fun moveNoteToTrash(id: Long) {
        viewModelScope.launch {
            repository.moveNoteToTrash(id)
        }
    }

    fun pinNote(note: Note) {
        viewModelScope.launch {
            repository.updateNote(note.copy(isPinned = !note.isPinned, modifiedAt = System.currentTimeMillis()))
        }
    }

    fun searchNotes(query: String) {
        _searchQuery.value = query
    }

    fun setSortOrder(sortOrder: SortOrder) {
        _sortOrder.value = sortOrder
    }

    fun setFolderFilter(folderId: Long?) {
        _folderFilter.value = folderId
    }
}
