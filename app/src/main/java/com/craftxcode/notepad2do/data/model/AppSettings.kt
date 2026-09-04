package com.craftxcode.notepad2do.data.model

enum class AppTheme {
    LIGHT, DARK, SYSTEM
}

enum class ViewType {
    LIST, GRID
}

enum class SortOrder {
    DATE_CREATED, DATE_MODIFIED, TITLE, PRIORITY
}

val AppColors = listOf(
    0xFF6750A4L, // Default Purple
    0xFF006C4CL, // Green
    0xFF8B4100L, // Orange
    0xFF006494L, // Blue
    0xFF7D5260L  // Pink
)
