package com.craftxcode.notepad2do.ui.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Home : Screen("home")
    object NoteEdit : Screen("note_edit/{noteId}") {
        fun createRoute(noteId: Long) = "note_edit/$noteId"
    }
    object TaskEdit : Screen("task_edit/{taskId}") {
        fun createRoute(taskId: Long) = "task_edit/$taskId"
    }
    object Trash : Screen("trash")
    object Settings : Screen("settings")
}
