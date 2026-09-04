package com.craftxcode.notepad2do.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.craftxcode.notepad2do.data.repository.AppRepository
import com.craftxcode.notepad2do.data.settings.SettingsManager

class ViewModelFactory(
    private val repository: AppRepository,
    private val settingsManager: SettingsManager
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(NoteViewModel::class.java) -> NoteViewModel(repository) as T
            modelClass.isAssignableFrom(TaskViewModel::class.java) -> TaskViewModel(repository) as T
            modelClass.isAssignableFrom(FolderViewModel::class.java) -> FolderViewModel(repository) as T
            modelClass.isAssignableFrom(TrashViewModel::class.java) -> TrashViewModel(repository) as T
            modelClass.isAssignableFrom(SettingsViewModel::class.java) -> SettingsViewModel(settingsManager) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
