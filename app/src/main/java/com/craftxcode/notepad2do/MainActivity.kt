package com.craftxcode.notepad2do

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.craftxcode.notepad2do.ui.theme.Notepad2doTheme
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.rememberNavController
import com.craftxcode.notepad2do.data.model.AppTheme
import com.craftxcode.notepad2do.ui.navigation.AppNavigation
import com.craftxcode.notepad2do.ui.viewmodel.*

class MainActivity : ComponentActivity() {

    private val noteViewModel: NoteViewModel by viewModels {
        val app = application as NotepadApplication
        ViewModelFactory(app.repository, app.settingsManager)
    }

    private val taskViewModel: TaskViewModel by viewModels {
        val app = application as NotepadApplication
        ViewModelFactory(app.repository, app.settingsManager)
    }

    private val folderViewModel: FolderViewModel by viewModels {
        val app = application as NotepadApplication
        ViewModelFactory(app.repository, app.settingsManager)
    }

    private val trashViewModel: TrashViewModel by viewModels {
        val app = application as NotepadApplication
        ViewModelFactory(app.repository, app.settingsManager)
    }

    private val settingsViewModel: SettingsViewModel by viewModels {
        val app = application as NotepadApplication
        ViewModelFactory(app.repository, app.settingsManager)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            enableEdgeToEdge()
            setContent {
                val appTheme by settingsViewModel.appTheme.collectAsState()
                val primaryColorLong by settingsViewModel.primaryColor.collectAsState()
                
                val isDarkTheme = when (appTheme) {
                    AppTheme.LIGHT -> false
                    AppTheme.DARK -> true
                    AppTheme.SYSTEM -> isSystemInDarkTheme()
                }

                Notepad2doTheme(
                    darkTheme = isDarkTheme,
                    primaryColor = Color(primaryColorLong)
                ) {
                    val navController = rememberNavController()
                    AppNavigation(
                        navController = navController,
                        noteViewModel = noteViewModel,
                        taskViewModel = taskViewModel,
                        folderViewModel = folderViewModel,
                        trashViewModel = trashViewModel,
                        settingsViewModel = settingsViewModel
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
