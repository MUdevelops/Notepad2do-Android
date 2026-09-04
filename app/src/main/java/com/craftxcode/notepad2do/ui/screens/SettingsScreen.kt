package com.craftxcode.notepad2do.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.craftxcode.notepad2do.data.model.AppColors
import com.craftxcode.notepad2do.data.model.AppTheme
import com.craftxcode.notepad2do.ui.viewmodel.SettingsViewModel
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel,
    onBack: () -> Unit
) {
    val appTheme by settingsViewModel.appTheme.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            item {
                Text(
                    text = "Appearance",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(16.dp)
                )
            }
            item {
                var showThemeDialog by remember { mutableStateOf(false) }
                ListItem(
                    headlineContent = { Text("App Theme") },
                    supportingContent = { Text(appTheme.name) },
                    leadingContent = { Icon(Icons.Default.Palette, contentDescription = null) },
                    modifier = Modifier.clickable { showThemeDialog = true }
                )

                if (showThemeDialog) {
                    AlertDialog(
                        onDismissRequest = { showThemeDialog = false },
                        title = { Text("Choose Theme") },
                        text = {
                            Column {
                                AppTheme.entries.forEach { theme ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                settingsViewModel.updateAppTheme(theme)
                                                showThemeDialog = false
                                            }
                                            .padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        RadioButton(
                                            selected = appTheme == theme,
                                            onClick = {
                                                settingsViewModel.updateAppTheme(theme)
                                                showThemeDialog = false
                                            }
                                        )
                                        Text(text = theme.name, modifier = Modifier.padding(start = 16.dp))
                                    }
                                }
                            }
                        },
                        confirmButton = {
                            TextButton(onClick = { showThemeDialog = false }) { Text("Close") }
                        }
                    )
                }
            }
            item {
                Text(
                    text = "Theme Colors",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(16.dp)
                )
            }
            item {
                val primaryColorLong by settingsViewModel.primaryColor.collectAsStateWithLifecycle()
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AppColors.forEach { color ->
                        val isSelected = color == primaryColorLong
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clickable { settingsViewModel.updatePrimaryColor(color) }
                                .padding(4.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = Color(color),
                                border = if (isSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.onBackground) else null,
                                modifier = Modifier.fillMaxSize()
                            ) {}
                        }
                    }
                }
            }
        }
    }
}
