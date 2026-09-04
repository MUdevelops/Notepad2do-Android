package com.craftxcode.notepad2do.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun PriorityIcon(priority: Int, modifier: Modifier = Modifier) {
    val color = when (priority) {
        0 -> Color.Green
        1 -> Color(0xFFFFA500) // Orange
        2 -> Color.Red
        else -> Color.Gray
    }
    Icon(
        imageVector = Icons.Default.Flag,
        contentDescription = "Priority",
        tint = color,
        modifier = modifier
    )
}
