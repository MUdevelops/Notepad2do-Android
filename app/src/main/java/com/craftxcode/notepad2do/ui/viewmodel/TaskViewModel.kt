package com.craftxcode.notepad2do.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.craftxcode.notepad2do.data.model.SortOrder
import com.craftxcode.notepad2do.data.model.Task
import com.craftxcode.notepad2do.data.repository.AppRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class TaskViewModel(private val repository: AppRepository) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _folderFilter = MutableStateFlow<Long?>(null)
    private val _sortOrder = MutableStateFlow(SortOrder.PRIORITY)

    val tasks: StateFlow<List<Task>> = combine(
        repository.getAllTasks(),
        _searchQuery,
        _folderFilter,
        _sortOrder
    ) { tasks, query, folderId, sortOrder ->
        tasks.filter { task ->
            (folderId == null || task.folderId == folderId) &&
            (task.title.contains(query, ignoreCase = true) || task.description.contains(query, ignoreCase = true))
        }.sortedWith(getComparator(sortOrder))
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private fun getComparator(sortOrder: SortOrder): Comparator<Task> {
        return when (sortOrder) {
            SortOrder.PRIORITY -> compareByDescending<Task> { it.isPinned }.thenByDescending { it.priority }.thenByDescending { it.modifiedAt }
            SortOrder.DATE_MODIFIED -> compareByDescending<Task> { it.isPinned }.thenByDescending { it.modifiedAt }
            SortOrder.DATE_CREATED -> compareByDescending<Task> { it.isPinned }.thenByDescending { it.createdAt }
            SortOrder.TITLE -> compareByDescending<Task> { it.isPinned }.thenBy { it.title.lowercase() }
        }
    }

    fun upsertTask(task: Task) {
        viewModelScope.launch {
            if (task.id == 0L) {
                repository.insertTask(task)
            } else {
                repository.updateTask(task.copy(modifiedAt = System.currentTimeMillis()))
            }
        }
    }

    fun toggleTaskCompletion(task: Task) {
        viewModelScope.launch {
            repository.updateTask(task.copy(isCompleted = !task.isCompleted, modifiedAt = System.currentTimeMillis()))
        }
    }

    fun moveTaskToTrash(id: Long) {
        viewModelScope.launch {
            repository.moveTaskToTrash(id)
        }
    }

    fun pinTask(task: Task) {
        viewModelScope.launch {
            repository.updateTask(task.copy(isPinned = !task.isPinned, modifiedAt = System.currentTimeMillis()))
        }
    }

    fun searchTasks(query: String) {
        _searchQuery.value = query
    }

    fun setSortOrder(sortOrder: SortOrder) {
        _sortOrder.value = sortOrder
    }

    fun setFolderFilter(folderId: Long?) {
        _folderFilter.value = folderId
    }
}
