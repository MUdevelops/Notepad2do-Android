package com.craftxcode.notepad2do

import android.app.Application
import com.craftxcode.notepad2do.data.local.AppDatabase
import com.craftxcode.notepad2do.data.model.Folder
import com.craftxcode.notepad2do.data.repository.AppRepository
import com.craftxcode.notepad2do.data.repository.AppRepositoryImpl
import com.craftxcode.notepad2do.data.settings.SettingsManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class NotepadApplication : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    
    val database by lazy { AppDatabase.getDatabase(this) }
    val repository: AppRepository by lazy { 
        AppRepositoryImpl(
            database.folderDao(),
            database.noteDao(),
            database.taskDao()
        ) 
    }
    val settingsManager by lazy { SettingsManager(this) }

    override fun onCreate() {
        super.onCreate()
        // Initialize default folder safely on a background thread
        applicationScope.launch(Dispatchers.IO) {
            try {
                val folders = repository.getAllFolders().first()
                if (folders.isEmpty()) {
                    repository.insertFolder(
                        Folder(
                            name = "Default",
                            icon = "folder",
                            color = -7829368 // Gray
                        )
                    )
                }
            } catch (e: Exception) {
                // Log or handle error
            }
        }
    }
}
