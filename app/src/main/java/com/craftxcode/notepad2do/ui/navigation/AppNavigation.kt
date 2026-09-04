package com.craftxcode.notepad2do.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.craftxcode.notepad2do.ui.screens.*
import com.craftxcode.notepad2do.ui.viewmodel.*

@Composable
fun AppNavigation(
    navController: NavHostController,
    noteViewModel: NoteViewModel,
    taskViewModel: TaskViewModel,
    folderViewModel: FolderViewModel,
    trashViewModel: TrashViewModel,
    settingsViewModel: SettingsViewModel
) {
    NavHost(navController = navController, startDestination = Screen.Splash.route) {
        composable(Screen.Splash.route) {
            SplashScreen(onFinished = {
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Splash.route) { inclusive = true }
                }
            })
        }
        composable(Screen.Home.route) {
            HomeScreen(
                noteViewModel = noteViewModel,
                taskViewModel = taskViewModel,
                folderViewModel = folderViewModel,
                settingsViewModel = settingsViewModel,
                onNoteClick = { id -> navController.navigate(Screen.NoteEdit.createRoute(id)) },
                onTaskClick = { id -> navController.navigate(Screen.TaskEdit.createRoute(id)) },
                onTrashClick = { navController.navigate(Screen.Trash.route) },
                onSettingsClick = { navController.navigate(Screen.Settings.route) }
            )
        }
        composable(
            route = Screen.NoteEdit.route,
            arguments = listOf(navArgument("noteId") { type = NavType.LongType })
        ) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getLong("noteId") ?: -1L
            NoteEditScreen(
                noteId = noteId,
                noteViewModel = noteViewModel,
                folderViewModel = folderViewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            route = Screen.TaskEdit.route,
            arguments = listOf(navArgument("taskId") { type = NavType.LongType })
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getLong("taskId") ?: -1L
            TaskEditScreen(
                taskId = taskId,
                taskViewModel = taskViewModel,
                folderViewModel = folderViewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Trash.route) {
            TrashScreen(
                trashViewModel = trashViewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Settings.route) {
            SettingsScreen(
                settingsViewModel = settingsViewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
