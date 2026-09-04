package com.craftxcode.notepad2do.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.craftxcode.notepad2do.data.model.AppTheme
import com.craftxcode.notepad2do.data.model.SortOrder
import com.craftxcode.notepad2do.data.model.ViewType
import com.craftxcode.notepad2do.data.settings.SettingsManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(private val settingsManager: SettingsManager) : ViewModel() {

    val appTheme: StateFlow<AppTheme> = settingsManager.appThemeFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AppTheme.SYSTEM
    )

    val viewType: StateFlow<ViewType> = settingsManager.viewTypeFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ViewType.LIST
    )

    val sortOrder: StateFlow<SortOrder> = settingsManager.sortOrderFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SortOrder.DATE_MODIFIED
    )

    val primaryColor: StateFlow<Long> = settingsManager.primaryColorFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0xFF6750A4L
    )

    fun updateAppTheme(theme: AppTheme) {
        viewModelScope.launch {
            settingsManager.updateAppTheme(theme)
        }
    }

    fun updateViewType(viewType: ViewType) {
        viewModelScope.launch {
            settingsManager.updateViewType(viewType)
        }
    }

    fun updateSortOrder(sortOrder: SortOrder) {
        viewModelScope.launch {
            settingsManager.updateSortOrder(sortOrder)
        }
    }

    fun updatePrimaryColor(color: Long) {
        viewModelScope.launch {
            settingsManager.updatePrimaryColor(color)
        }
    }
}
